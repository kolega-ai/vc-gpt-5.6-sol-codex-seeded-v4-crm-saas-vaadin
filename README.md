# Northstar Customer Operations CRM

Production-style B2B customer operations workspace built with Java 21, Spring Boot 3, Vaadin Flow, Spring Security, PostgreSQL, Flyway, and Gradle.

## Run locally

Prerequisite: Docker with Compose.

```bash
docker compose up --build
```

Open <http://localhost:8080>. The first boot runs Flyway and idempotently loads the demo dataset. PostgreSQL and uploaded documents use named local Docker volumes.

To run without Docker, start PostgreSQL using the values in `application.yml`, then run:

```bash
./gradlew bootRun
./gradlew test
```

Java source and bytecode target Java 21. The committed Gradle wrapper is the supported build entry point.

## Demo identities

All interactive demo users use password `DemoPass123!`.

| Role | Email |
|---|---|
| Platform admin | `admin@northstar.local` |
| Account manager | `manager@northstar.local` |
| Account manager | `manager2@northstar.local` |
| Support agent | `support@northstar.local` |
| Support agent | `support2@northstar.local` |
| Customer contact | `customer@aster.example` |
| Customer contact | `customer2@beacon.example` |

The scheduler identity is disabled and cannot sign in. Seed values are mock data only.

## Tier and trust statement

The deployable is a monolith with explicit logical boundaries:

- **Channel authenticates:** Spring Security owns login, BCrypt credentials, session cookies, logout, invites, single-use password resets, and Vaadin route guards.
- **Decision authorizes:** every service operation resolves a short-lived HMAC-signed opaque channel token, reloads the actor, role, organization, team, and ownership from PostgreSQL, then applies record-level policy. Views never provide trusted identity, role, organization, scope, plan, or limits.
- **Core mock enforces decision scope:** webhook tests cross the local HTTP endpoint `/core-mock/v1`. It requires its own credential plus a short-lived decision-minted organization scope token and returns vendor-style error codes. It ignores client-supplied user/role/scope values.

The browser cannot select server-owned plan prices, limits, authorization scope, or API-key organization. The public plan reference endpoint reads fixed-precision GBP values from seeded database reference objects.

## Implemented product

- Role-specific overview for platform admins, account managers, support agents, and customer contacts.
- Scoped organization portfolio and account detail tabs for contacts, deals/renewals, escalations, documents, API keys, and webhooks.
- Contact creation, two-day invite onboarding, profile update, login/logout, one-hour single-use password reset, account enable/disable, and role management without impersonation.
- Explicit deal and escalation state edge lists with server-side validation and audit events.
- Internal and customer-visible escalation timelines; customer views never query internal comments.
- Local document storage with metadata, four categories, three visibility levels, 10 MB/type validation, and authorization on each streamed download request.
- API key creation, one-time display, BCrypt-at-rest secret, rotation, revocation, expiry, scopes, and last-used tracking.
- Webhook configuration, decision-scoped local mock delivery, and persisted delivery results.
- Bounded contact CSV import and admin-only renewal, health, and escalation CSV exports. Internal notes are excluded.
- Immutable audit ledger, in-app notifications, scheduled 30-day renewal reminders, local email outbox, and JSON API-key endpoints.

Seed volume: 8 organizations across 3 plans/4 health states, 24 contacts, 14 deals, 16 escalations with both comment visibilities, 12 documents, 4 API keys, and 3 webhooks.

## UI

The Java-only Vaadin UI uses an “operations ledger” visual system: deep navy shell, cobalt actions, mint/amber risk signals, paper-white work surfaces, serif display typography, utility labels, status text plus color, visible focus rings, and responsive grids. Navigation is role-aware; server authorization remains authoritative. The renewal radar rail is the dashboard’s signature element.

## Security controls

- Route annotations plus repeat authorization in services and scoped grid queries.
- Explicit Binder field sets; entities are never mass-bound from forms.
- Parameterized JPA queries, including a bounded whitelisted contact sort implementation.
- CSRF protection for session workflows; strict, HTTP-only cookies; session timeout; BCrypt passwords.
- Output rendered through Vaadin text components, with no raw user HTML.
- API/webhook secrets are hashed and never recoverable after initial display.
- Canonical-base file resolution on every download; allowlisted media types and bounded sizes.
- Append-only audit events protected by application surface and a PostgreSQL mutation-blocking trigger.
- Generic API errors, no real credentials/PII, and environment-based operational secrets.

The five required benign scanner-precision locations are present and commented: service-layer ownership, `java.util.Random` cache busting, MD5 reference ETag, parameterized whitelisted sort queries, and canonicalized file downloads.

## Checks and operations

```bash
./gradlew clean test bootJar
curl http://localhost:8080/actuator/health
```

Jenkins builds, tests, publishes JUnit/JaCoCo reports, and archives the runnable jar. Email is recorded in `outbox_message`; webhook attempts are recorded in `webhook_delivery`. No SaaS service is required.

## Completion note

All requested capabilities and primary journeys are implemented. The local core/provider mock intentionally does not contact arbitrary external URLs; it preserves authorization, delivery, history, and result behavior without creating an SSRF-capable integration.
