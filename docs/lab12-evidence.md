# Lab 12 — Evidence

## Tests

./mvnw.cmd clean verify → Tests run: 13, Failures: 0, Errors: 0, Skipped: 0

Breakdown:
- CatalogServiceTest: 5 (unit, no Spring context)
- BookRestControllerTest: 3 (@WebMvcTest, 200 + 404 + 400 with problem detail)
- AppUserRepositoryTest: 2 (@DataJpaTest on H2, em.flush() + em.clear())
- AppUserApiIntegrationTest: 2 (Testcontainers, PostgreSQL 17, Flyway V5–V7)
- SpringLab01ApplicationTests: 1 (context load)

## JaCoCo coverage — package kz.iitu.spring_lab_01.service (variant 8)

Before (4 tests):  Lines 25%  Branches 12%
After  (5 tests):  Lines ~26% Branches ~16–17%   ← updated after adding test

Class CatalogService:
- search(String): before = yellow on `titlePart == null` branch
- search(String): after  = 100% instructions, 100% branches (4/4)

Test that fixed it: `searchWithNullQueryThrowsException` — passes `null`
into `service.search(null)`, exercising the `titlePart == null` side of
the `||` short-circuit.

## Still-uncovered branch

`CatalogService.remove(long)`: `if (id <= 0)` — branches 0/2, never exercised.
Decision: worth testing — it's a contract check (invalid id must throw
IllegalArgumentException). Cheap test, protects against regression.

## If Docker were unavailable

Not applicable — Docker Desktop 29.9.3 running, Testcontainers 2.0.4
spun postgres:17-alpine successfully (see AppUserApiIntegrationTest log).