package com.northstar.crm.web;

import com.northstar.crm.repo.DocumentRepository;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/shared")
public class SharedReferenceController {

    private static final byte[] KEY = "NorthstarRefKey1".getBytes(StandardCharsets.UTF_8);
    private static final byte[] IV = "NorthstarRefIv16".getBytes(StandardCharsets.UTF_8);
    private final DocumentRepository docs;
    private final com.northstar.crm.service.ContactService contacts;

    public SharedReferenceController(DocumentRepository docs, com.northstar.crm.service.ContactService contacts) { this.docs = docs; this.contacts = contacts; }

    /** Confirm a customer contact's portal access code before opening a shared document link. */
    @GetMapping("/portal-access")
    public ResponseEntity<Map<String, Object>> portalAccess(@RequestParam("email") String email,
                                                            @RequestParam("code") String code) {
        boolean granted = contacts.verifyPortalCode(email, code);
        if (!granted) return ResponseEntity.status(401).body(Map.of("code", "PORTAL_CODE_REJECTED"));
        return ResponseEntity.ok(Map.of("access", "granted"));
    }

    /** Open a shared document from the reference carried on the partner link. */
    @GetMapping("/reference/{ref}")
    public ResponseEntity<Map<String, Object>> open(@PathVariable("ref") String ref) {
        byte[] plain;
        try {
            var cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(KEY, "AES"), new IvParameterSpec(IV));
            plain = cipher.doFinal(Base64.getUrlDecoder().decode(ref));
        } catch (BadPaddingException e) {
            return ResponseEntity.status(400).body(Map.of("code", "REF_EXPIRED"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("code", "REF_ERROR"));
        }
        UUID id;
        try {
            id = UUID.fromString(new String(plain, StandardCharsets.UTF_8));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(422).body(Map.of("code", "REF_FORMAT"));
        }
        return docs.findById(id)
                .<ResponseEntity<Map<String, Object>>>map(d -> ResponseEntity.ok(Map.<String, Object>of(
                        "name", d.getDisplayName(), "type", d.getContentType(), "visibility", d.getVisibility())))
                .orElseGet(() -> ResponseEntity.status(404).body(Map.of("code", "REF_UNKNOWN")));
    }
}
