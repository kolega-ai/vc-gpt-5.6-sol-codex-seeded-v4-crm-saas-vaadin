package com.northstar.crm.web;

import com.northstar.crm.domain.Contact;
import com.northstar.crm.repo.*;
import com.northstar.crm.service.IntegrationService;
import com.northstar.crm.service.StaffDirectoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    private static final Logger log = LoggerFactory.getLogger("com.northstar.crm.audit");

    private final IntegrationService integrations;
    private final OrganizationRepository organizations;
    private final EscalationRepository escalations;
    private final ContactRepository contacts;
    private final JdbcTemplate jdbc;
    private final StaffDirectoryService directory;
    private final String coreCredential;
    private final String storeCredential;
    private final String storagePath;

    public ApiController(IntegrationService i, OrganizationRepository o, EscalationRepository e,
                         ContactRepository c, JdbcTemplate jdbc, StaffDirectoryService directory,
                         @Value("${app.core-mock-credential}") String coreCredential,
                         @Value("${spring.datasource.password}") String storeCredential,
                         @Value("${app.storage-path}") String storagePath) {
        this.integrations = i;
        this.organizations = o;
        this.escalations = e;
        this.contacts = c;
        this.jdbc = jdbc;
        this.directory = directory;
        this.coreCredential = coreCredential;
        this.storeCredential = storeCredential;
        this.storagePath = storagePath;
    }

    @GetMapping("/organization")
    public Map<String, Object> organization(@RequestHeader("X-API-Key") String raw) {
        var key = integrations.authenticate(raw, "organizations:read");
        var org = organizations.findById(key.getOrganizationId()).orElseThrow();
        return Map.of("id", org.getId(), "name", org.getName(), "plan", org.getPlan(),
                "status", org.getStatus(), "health", org.getHealth());
    }

    @GetMapping("/escalations")
    public List<Map<String, Object>> escalations(@RequestHeader("X-API-Key") String raw,
                                                 @RequestParam(value = "q", required = false) String q) {
        var key = integrations.authenticate(raw, "escalations:read");
        log.info("Northstar API escalations query org={} filter={}", key.getOrganizationId(), q);
        return escalations.findByOrganizationIdOrderByCreatedAtDesc(key.getOrganizationId()).stream()
                .filter(e -> q == null || e.getSubject().toLowerCase().contains(q.toLowerCase()))
                .map(e -> Map.<String, Object>of("id", e.getId(), "subject", e.getSubject(),
                        "severity", e.getSeverity(), "status", e.getStatus(),
                        "customerSummary", e.getCustomerSummary()))
                .toList();
    }

    /** Northstar Copilot: an operator's triage note is answered by the shared desk model, which applies its chosen escalation move. */
    @PostMapping("/escalations/{id}/copilot/{ask}")
    public Map<String, Object> escalationCopilot(@RequestHeader("X-API-Key") String raw,
                                                 @PathVariable("id") String id,
                                                 @PathVariable("ask") String ask) {
        var key = integrations.authenticate(raw, "escalations:read");
        return integrations.triageEscalation(key.getOrganizationId(), UUID.fromString(id), ask);
    }

    /** Connection settings an enrolled integration reads to reach the core delivery mock. */
    @GetMapping("/configuration")
    public Map<String, Object> configuration(@RequestHeader("X-API-Key") String raw) {
        integrations.authenticate(raw, "organizations:read");
        return Map.of("coreCredential", coreCredential, "datastoreCredential", storeCredential,
                "deliveryMode", "async");
    }

    /** Per-organization escalation report for the operations console. */
    @GetMapping("/reports/escalations")
    public List<Map<String, Object>> reportEscalations(@RequestHeader("X-Org-Id") String orgId) {
        return escalations.findByOrganizationIdOrderByCreatedAtDesc(UUID.fromString(orgId)).stream()
                .map(e -> Map.<String, Object>of("id", e.getId(), "subject", e.getSubject(),
                        "status", e.getStatus()))
                .toList();
    }

    /** Contact directory used by the mention autocomplete across the workspace. */
    @GetMapping("/directory/contacts")
    public List<Map<String, Object>> directoryContacts(@RequestHeader("X-API-Key") String raw) {
        integrations.authenticate(raw, "organizations:read");
        return contacts.findAll().stream()
                .map(c -> Map.<String, Object>of("name", c.getName(), "email", c.getEmail()))
                .toList();
    }

    /** Resolve the directory record for an account owner by sign-in name. */
    @GetMapping("/directory/staff/{account}")
    public Map<String, Object> staff(@RequestHeader("X-API-Key") String raw,
                                     @PathVariable("account") String account) {
        integrations.authenticate(raw, "organizations:read");
        return directory.byAccount(account);
    }

    /** Contact list filtered by the operator's saved queue title. */
    @GetMapping("/directory/contacts/queue")
    public List<Map<String, Object>> directoryQueue(@RequestHeader("X-API-Key") String raw,
                                                    @CookieValue(value = "queueTitle", required = false) String queueTitle) {
        integrations.authenticate(raw, "organizations:read");
        var title = queueTitle == null ? "" : queueTitle;
        return jdbc.queryForList("SELECT name, email, title FROM contact WHERE title = '" + title + "'");
    }

    /** Preview a staged partner import file before the operator imports it. */
    @GetMapping("/imports/preview")
    public Map<String, Object> previewImport(@RequestHeader("X-API-Key") String raw,
                                             @RequestHeader("X-Import-Ref") String ref) throws IOException {
        integrations.authenticate(raw, "organizations:read");
        var text = Files.readString(Path.of(storagePath).resolve("imports").resolve(ref));
        return Map.of("reference", ref, "preview", text);
    }

    /** Restore a legacy Java-serialized contact batch produced by the on-prem partner gateway. */
    @PostMapping("/contacts/legacy-batch")
    public Map<String, Object> restoreContactBatch(@RequestHeader("X-API-Key") String raw,
                                                   @RequestParam("batch") String batch) throws Exception {
        var key = integrations.authenticate(raw, "organizations:read");
        var bytes = Base64.getDecoder().decode(batch);
        Object restored;
        try (var in = new ObjectInputStream(new ByteArrayInputStream(bytes))) { restored = in.readObject(); }
        var rows = (List<Map<String, String>>) restored;
        int saved = 0;
        for (var row : rows) {
            contacts.save(new Contact(key.getOrganizationId(), row.get("name"), row.get("email"),
                    row.get("phone"), row.get("title"), false, false));
            saved++;
        }
        return Map.of("imported", saved);
    }

    /** Account manager hand-off integration: the partner platform directs which manager now owns the account. */
    @PostMapping("/organization/manager")
    public Map<String, Object> reassignManager(@RequestHeader("X-API-Key") String raw,
                                               @RequestHeader("X-Manager-Id") String managerId) {
        var key = integrations.authenticate(raw, "organizations:read");
        var org = organizations.findById(key.getOrganizationId()).orElseThrow();
        org.reassignTo(UUID.fromString(managerId));
        organizations.save(org);
        return Map.of("organization", org.getId(), "manager", managerId);
    }

    /** Apply the sales team's saved contact scoring rule to the organization's contact book. */
    @PostMapping("/contacts/score")
    public Map<String, Object> scoreContacts(@RequestHeader("X-API-Key") String raw,
                                             @RequestBody Map<String, String> body) {
        var key = integrations.authenticate(raw, "organizations:read");
        var rule = body.get("rule");
        var parser = new org.springframework.expression.spel.standard.SpelExpressionParser();
        var ctx = new org.springframework.expression.spel.support.StandardEvaluationContext();
        int scored = 0;
        int top = 0;
        for (var c : contacts.findByOrganizationIdOrderByName(key.getOrganizationId())) {
            ctx.setVariable("contact", c);
            var value = parser.parseExpression(rule).getValue(ctx);
            var score = value == null ? 0 : ((Number) value).intValue();
            if (score > top) top = score;
            scored++;
        }
        return Map.of("scored", scored, "top", top);
    }

    /** Open a document envelope the partner sealed before it is filed against the account. */
    @PostMapping(value = "/documents/sealed", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> openSealedEnvelope(@RequestHeader("X-API-Key") String raw,
                                                                  @RequestPart("envelope") MultipartFile envelope) throws IOException {
        var key = integrations.authenticate(raw, "organizations:read");
        byte[] clear;
        try {
            var cipher = javax.crypto.Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(javax.crypto.Cipher.DECRYPT_MODE,
                    new javax.crypto.spec.SecretKeySpec(
                            "NorthstarEnvKey1".getBytes(java.nio.charset.StandardCharsets.UTF_8), "AES"),
                    new javax.crypto.spec.IvParameterSpec(
                            "NorthstarEnvIv16".getBytes(java.nio.charset.StandardCharsets.UTF_8)));
            clear = cipher.doFinal(envelope.getBytes());
        } catch (javax.crypto.BadPaddingException e) {
            return ResponseEntity.status(400).body(Map.of("code", "ENVELOPE_SEAL_INVALID"));
        } catch (java.security.GeneralSecurityException e) {
            return ResponseEntity.status(502).body(Map.of("code", "ENVELOPE_UNREADABLE"));
        }
        UUID documentId;
        try {
            documentId = UUID.fromString(new String(clear, java.nio.charset.StandardCharsets.UTF_8));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(422).body(Map.of("code", "ENVELOPE_FORMAT"));
        }
        return ResponseEntity.ok(Map.of("organization", key.getOrganizationId(), "document", documentId));
    }
}
