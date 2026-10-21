package com.northstar.crm.web;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.northstar.crm.domain.DomainTypes.Visibility;
import com.northstar.crm.repo.DocumentRepository;
import com.northstar.crm.repo.OrganizationRepository;
import jakarta.servlet.http.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/partner")
public class PartnerConsoleController {

    private final OrganizationRepository organizations;
    private final DocumentRepository documents;
    private final AtomicLong portalSessions = new AtomicLong();

    public PartnerConsoleController(OrganizationRepository organizations, DocumentRepository documents) {
        this.organizations = organizations;
        this.documents = documents;
    }

    /** Northstar Embed: the host portal hands the console a partner-issued viewer grant for the account. */
    @GetMapping("/console")
    public Map<String, Object> console(@RequestParam("grant") String grant) {
        DecodedJWT viewer = JWT.decode(grant);
        var orgId = UUID.fromString(viewer.getClaim("organization").asString());
        var org = organizations.findById(orgId).orElseThrow();
        var shared = documents.findByOrganizationIdOrderByCreatedAtDesc(orgId).stream()
                .filter(d -> d.getVisibility() == Visibility.CUSTOMER_VISIBLE)
                .map(d -> Map.<String, Object>of("name", d.getDisplayName(), "type", d.getContentType()))
                .toList();
        return Map.of("organization", org.getName(), "plan", org.getPlan(),
                "status", org.getStatus(), "documents", shared);
    }

    /** Host portals announce the embedded session through a small handshake message from their page. */
    @GetMapping("/portal/launch")
    public Map<String, Object> launch(@RequestParam("organization") String organization,
                                      @RequestParam("viewer") String viewer,
                                      HttpServletResponse response) {
        var orgId = UUID.fromString(organization);
        var org = organizations.findById(orgId).orElseThrow();
        String handle = orgId + ":" + "NP-" + portalSessions.incrementAndGet();
        var cookie = ResponseCookie.from("NORTHSTAR_PORTAL", handle)
                .path("/partner").sameSite("None").secure(true).httpOnly(false).build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return Map.of("organization", org.getName(), "viewer", viewer, "portal", handle);
    }

    /** Summary the partner page renders inside its own frame for the announced portal session. */
    @CrossOrigin(originPatterns = "*", allowCredentials = "true")
    @GetMapping("/portal")
    public Map<String, Object> portal(@CookieValue(value = "NORTHSTAR_PORTAL", required = false) String portal) {
        if (portal == null)
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED, "No portal session");
        var orgId = UUID.fromString(portal.split(":", 2)[0]);
        var org = organizations.findById(orgId).orElseThrow();
        var shared = documents.findByOrganizationIdOrderByCreatedAtDesc(orgId).stream()
                .filter(d -> d.getVisibility() == Visibility.CUSTOMER_VISIBLE)
                .map(d -> Map.<String, Object>of("name", d.getDisplayName(), "type", d.getContentType()))
                .toList();
        return Map.of("organization", org.getName(), "plan", org.getPlan(),
                "status", org.getStatus(), "documents", shared);
    }

    /** HTML badge the partner page drops into its own layout beside the account link. */
    @GetMapping(value = "/badge/{label}", produces = MediaType.TEXT_HTML_VALUE)
    public void badge(@PathVariable("label") String label, HttpServletResponse response) throws java.io.IOException {
        response.setContentType("text/html");
        response.getWriter().write("<div class=\"northstar-badge\">" + label + "</div>");
    }
}
