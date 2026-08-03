# AGENTS.md

This file provides guidance to AI coding agents when working with code in this repository.

## What this is

The **Wahllokalsystem (WLS)** supports elections for the city of Munich: election boards
("Wahlvorstand") in each constituency enter counted votes and deliver them securely to the
election office. The system documents board attendance and incidents, has offline capabilities
for broken network connections, and can broadcast messages to all election offices.

It is a **multi-repo-in-one** monorepo: ~13 independent Spring Boot microservices, two Vue.js
frontends, a shared `wls-common` library, a local `stack/` (Podman/Docker Compose), and VitePress
`docs/`. There is **no root `pom.xml`** — each `wls-*-service` is its own Maven project built
independently. The documentation under `docs/src/technik/` is the authoritative source for
architecture and conventions and is far more detailed than this file.

## Layout

- `wls-<domain>-service/` — backend microservices (Spring Boot 3.5, Java 21). E.g. `basisdaten`,
  `briefwahl`, `broadcast`, `ergebnismeldung`, `infomanagement`, `monitoring`,
  `vorfaelleundvorkommnisse`, `wahlvorbereitung`, `wahlvorstand`, `admin`.
- `wls-eai-service/` — EAI (Enterprise Application Integration): the gateway/adapter to external
  election systems. Other services reach external data through it.
- `wls-auth-service/` — authentication; `wls-api-gateway/` — Spring Cloud Gateway (refarch gateway).
- `wls-common/` — shared multi-module library (`exception`, `security`, `swagger`, `monitoring`,
  `testing`). Published as `de.muenchen.oss.wahllokalsystem.wls-common` and consumed by services
  via a pinned version (e.g. `1.7.1`), **not** as a reactor module. The in-repo source is a newer
  SNAPSHOT — changes here are only picked up by services after a release/version bump.
- `wls-gui-wahllokalsystem/` — main election-board PWA (Vue 3 + Vuetify). `wls-gui-admintool/` —
  admin/monitoring frontend.
- `stack/` — `docker-compose*.yml`, gateway config, LDAP, Oracle DB, `http_requests/` for seeding,
  IntelliJ `run_configurations/`.
- `docs/` — VitePress site (`npm run` inside `docs/`). Read `docs/src/technik/` first.

## Commands

### Backend (run inside a specific `wls-*-service/` directory)
```bash
mvn clean verify              # full build incl. tests, Spotless, Checkstyle, JaCoCo
mvn test                      # unit + integration tests only
mvn test -Dtest=ClassName     # single test class
mvn test -Dtest=ClassName#should_returnDTO_when_givenValidId   # single test method
mvn spotless:apply            # auto-format (build FAILS on unformatted code; Spotless runs `check`)
mvn checkstyle:check          # lint
./runLocal.sh                 # run with profiles local,db-dummydata,standalone (also runLocal.bat,
                              # runLocalNoSecurity.sh)
```
Code style is enforced by **Spotless** (Eclipse formatter via `itm-java-codeformat` + googleJavaFormat)
and **Checkstyle** (`checkstyle.xml` per service). Run `mvn spotless:apply` before committing or the
build breaks. See https://github.com/it-at-m/itm-java-codeformat for IDE setup.

### Frontend (run inside `wls-gui-wahllokalsystem/` or `wls-gui-admintool/`)
```bash
npm run dev          # Vite dev server
npm run test         # vitest run
npm run lint         # prettier --check + eslint + vue-tsc --noEmit
npm run fix          # prettier --write + eslint --fix
npm run build        # vue-tsc --build && vite build
npm run storybook    # component explorer on :6006
```
Requires Node `>=24.11 <25`. API clients are **generated** from OpenAPI JSON in
`src/resources/openapis/` via `npm run gen:<service>-api` (e.g. `gen:wahlvorstand-api`). Generated
code lands in `src/api/wls-clients/generated-*-api/` — do not edit it by hand; regenerate after
updating the OpenAPI spec.

### Running the full system locally
See `docs/src/technik/get_started/index.md`. Order: Oracle DB → auth-service → api-gateway →
frontend → domain services, then run the seeding requests in `stack/http_requests/`
(`initOracleDB_Komw.http` or `initOracleDB_MBW.http`). Hosts file needs `127.0.0.1
host.docker.internal` (+ `auth.wls.` and `gui.wls.` variants).

## Backend architecture (per service)

Each service follows a strict **3-layer** structure, organized by **fachliche Domain** (business
domain) as subpackages. Base package is `de.muenchen.oss.wahllokalsystem.<service>service`.

1. **Access layer** (`rest/<domain>/`) — `*Controller`, `*DTO` (records), `*DTOMapper`
   (MapStruct interface). Handles HTTP and **authentication**.
2. **Service layer** (`service/<domain>/`) — `*Service` (business logic), `*Validator`
   (throws `FachlicheWlsException` on invalid input), `*Model` records (the service's own data
   model), `*ModelMapper` (MapStruct, model ↔ entity), and **authorization** (`*SecurityProxy`
   / `@PreAuthorize`). Calls to other microservices are abstracted behind a `*Client` interface
   here, implemented in `client/`.
3. **Persistence layer** (`domain/<domain>/`) — JPA `*` entities + Spring Data `*Repository`.

Cross-domain shared classes go in a `common` subpackage of the respective layer.

Key conventions:
- **MapStruct** generates mappers (`-Amapstruct.defaultComponentModel=spring`,
  `unmappedTargetPolicy=ERROR` — an unmapped target field fails compilation). **Lombok** is used
  throughout (order matters in the annotation processor path).
- **Authorization** is two-pronged: an authority check (`hasAuthority`) **and** a
  `BezirkIDPermissionEvaluator` check ensuring the user may act for that specific Wahlbezirk.
  `DummyBezirkIdPermissionEvaluatorImpl` bypasses this for local/test profiles.
- **Error handling** is centralized: throw subclasses of `WlsException`
  (`FachlicheWlsException`→400, `TechnischeWlsException`→500, `InfrastrukturelleWlsException`→500,
  `SicherheitsWlsException`→403); `GlobalExceptionHandler` (from `wls-common:exception`) maps them
  to a `WlsExceptionDTO` + status. REST clients use `WlsResponseErrorHandler` to re-throw as
  `WlsException`.
- A **DummyClient** in `client/` implements every external-service `*Client` interface so a
  service can run standalone (activated by the `dummy.clients` / `standalone` profile).
- **Flyway** migrations under `src/main/resources/db/migration`. Convention: SQL keywords UPPERCASE,
  table names UpperCamelCase (matching class names), columns lowerCamelCase. Supports both H2 and
  Oracle.

### Spring profiles (combine as needed)
`db-h2` / `db-oracle` / `db-dummydata`, `no-security`, `dummy.nobezirkid.check`, `dummy.clients`,
`standalone` (= dummy.clients + dummy.nobezirkid.check + db-h2), `local` (sets the service's fixed
port), `plainTextLogging` (else logs are JSON). Each service has a **fixed port** (e.g. eai 8300,
auth 8100, wahlvorstand 8207) — only one instance per service can run at once. Full table in the
get-started doc.

## Frontend architecture (`wls-gui-*`)

Vue 3 Composition API + TypeScript + Vuetify, Pinia for state, vue-router, PWA via
`vite-plugin-pwa` + Workbox (offline support is a core requirement). Source organized by domain
subfolder (`common` for shared, `<domain>` for domain-specific) across: `api/` (generated clients),
`components/`, `composables/`, `stores/`, `views/`, `types/`, `plugins/`, `service-worker/`,
`resources/openapis/`.

- Component hierarchy: **Views** → **SingleUse** components → **Basis** components. Views and
  SingleUse components may read stores; Basis components may **not** (props/events only). Basis
  components use composables only for formatting/validation, not business logic. Saving is a
  SingleUse-component responsibility.
- Backend access path mirrors the backend: `store → service (composable) → client + mapper`.
- View state is preserved across navigation via `keep-alive` (with a per-path `key` when one view
  serves multiple routes) and/or stores.
- `tests/` and `stories/` mirror the `src/` folder structure exactly. Component tests use Vitest
  snapshots (`__snapshots__/`); MSW mocks backend calls.

## Testing conventions

- Test names follow `should_<expectedResult>_when_<stateUnderTest>` in English, CamelCase. Backend
  groups methods with JUnit5 `@Nested`; frontend with `describe(...)`.
- Backend test stages (all must pass before merge), each in a conventional location:
  - Unit tests — aim for 100% line/method coverage of own classes; mock collaborators.
  - `configuration.SecurityConfigurationTest` — authentication on endpoints.
  - `service.<Domain>SecurityTest` — authorization (test all authority combinations, allow + deny).
  - `rest.<Domain>ControllerIntegrationTest` — end-to-end through endpoints with auth.
  - `ArchUnitTest` — architecture rules; uses shared rules from `wls-common:testing`
    (`archunit.rule` package). **Do not** modify ArchUnit tests when implementing a normal feature.
- Frontend: unit-test composables/stores/types (mock cross-module calls and verify interaction);
  component tests verify rendering (snapshots) + event handling.

## Conventions & process

- Commit/PR messages reference an issue number (e.g. `#791 - ...`). Default branch and PR target is
  `dev`. Quality gates (lint, tests, build for changed frontend/backend/docs) must pass locally and
  in CI before merge — see `CONTRIBUTING.md`.
- Architecture decisions are recorded as ADRs in `docs/src/technik/adr/`. Notable ones:
  service-layer data model can sometimes be skipped (`adr002`), path variables as method arguments
  (issue 804), gender-sensitive German language, no i18n at start, no H2 console in production.
- Guides for common tasks live in `docs/src/technik/guides/` (new microservice, new frontend,
  generating API clients, DB access, ArchUnit rules, mock server, SSL certs, LDIF data).
- This is a public open-source project (MIT, it@M / opensource@muenchen.de). User-facing text and
  much documentation is in **German**; keep that language for domain terms and UI strings.
