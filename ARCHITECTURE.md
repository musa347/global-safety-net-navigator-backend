# Global Safety Net Navigator - Architecture

## System Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                         USER BROWSER                            │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         │ HTTPS
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                    NEXT.JS FRONTEND                             │
│                   (Vercel Deployment)                           │
│                                                                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐        │
│  │   Scenario   │  │   Journey    │  │   Result     │        │
│  │   Selection  │─▶│   Request    │─▶│   Display    │        │
│  └──────────────┘  └──────────────┘  └──────────────┘        │
│                                                                 │
│  - Client-side routing                                         │
│  - Source attribution UI                                       │
│  - Disclaimer presentation                                     │
│  - Responsive design                                           │
└────────────────────────┬───────────────────────────────────────┘
                         │
                         │ REST API (JSON)
                         │ + CORS
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                  SPRING BOOT BACKEND                            │
│                   (Render Deployment)                           │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐  │
│  │                    API LAYER                            │  │
│  │  ┌────────────────┐          ┌────────────────┐        │  │
│  │  │   Scenario     │          │    Journey     │        │  │
│  │  │  Controller    │          │   Controller   │        │  │
│  │  └────────────────┘          └────────────────┘        │  │
│  │         │                            │                  │  │
│  │         │  ┌──────────────────────┐  │                 │  │
│  │         └─▶│  Exception Handler   │◀─┘                 │  │
│  │            │  (Global)            │                     │  │
│  │            └──────────────────────┘                     │  │
│  └─────────────────────┬───────────────────────────────────┘  │
│                        │                                       │
│  ┌─────────────────────▼───────────────────────────────────┐  │
│  │                  DOMAIN LAYER                           │  │
│  │  ┌────────────────┐  ┌────────────────┐  ┌──────────┐ │  │
│  │  │   Content      │  │    Source      │  │ Journey  │ │  │
│  │  │   Service      │  │  Validation    │  │ Service  │ │  │
│  │  └────────────────┘  │    Service     │  └──────────┘ │  │
│  │         │             └────────────────┘       │       │  │
│  │         │                     │                │       │  │
│  │  ┌──────▼──────────┐   ┌──────▼──────┐  ┌────▼─────┐ │  │
│  │  │  BenefitContent │   │   Source    │  │ Scenario │ │  │
│  │  │     (Record)    │   │  (Record)   │  │ (Record) │ │  │
│  │  └─────────────────┘   └─────────────┘  └──────────┘ │  │
│  └─────────────────────┬───────────────────────────────────┘  │
│                        │                                       │
│  ┌─────────────────────▼───────────────────────────────────┐  │
│  │              REPOSITORY LAYER                           │  │
│  │  ┌────────────────┐  ┌────────────────┐  ┌──────────┐ │  │
│  │  │   Content      │  │    Source      │  │ Scenario │ │  │
│  │  │  Repository    │  │   Repository   │  │Repository│ │  │
│  │  └────────────────┘  └────────────────┘  └──────────┘ │  │
│  │         │                     │                │       │  │
│  │         └─────────────────────┴────────────────┘       │  │
│  │                        │                                │  │
│  │  ┌─────────────────────▼───────────────────────────┐   │  │
│  │  │         IN-MEMORY DATA STORES                   │   │  │
│  │  │  - Curated policy content                       │   │  │
│  │  │  - Official source references                   │   │  │
│  │  │  - Verified scenarios                           │   │  │
│  │  └─────────────────────────────────────────────────┘   │  │
│  └─────────────────────────────────────────────────────────┘  │
│                                                                │
│  ┌─────────────────────────────────────────────────────────┐  │
│  │              INFRASTRUCTURE LAYER                       │  │
│  │  - CORS Configuration                                   │  │
│  │  - Request ID Generation                                │  │
│  │  - Exception Mapping                                    │  │
│  │  - Health Endpoints (Actuator)                          │  │
│  └─────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    EXTERNAL REFERENCES                          │
│  (Not part of system, linked for user verification)            │
│                                                                 │
│  - SafetyWing Product Pages                                    │
│  - Nomad Citizen Policy Certificate (PDF)                      │
└─────────────────────────────────────────────────────────────────┘
```

## Data Flow: User Journey

```
1. User selects scenario
   ├─▶ GET /api/v1/income/scenarios
   └─▶ Returns list of verified scenarios

2. User submits journey request
   ├─▶ POST /api/v1/income/journeys
   │   Body: { "scenarioId": "CONTRACT_CANCELLED" }
   │
   ├─▶ Controller validates request
   │   └─▶ Generates unique requestId
   │
   ├─▶ Service layer fetches content
   │   ├─▶ ContentRepository.findByScenarioId()
   │   ├─▶ SourceValidationService.validateSourceIntegrity()
   │   └─▶ SourceRepository.findById() for each sourceId
   │
   └─▶ Returns structured journey response
       ├─ Scenario information
       ├─ Benefit information
       ├─ Policy conditions (with sourceIds)
       ├─ Items to verify
       ├─ Disclaimer
       ├─ Complete source references
       ├─ Next steps
       └─ Metadata (requestId, timestamp, contentVersion)

3. Frontend displays result
   ├─▶ Structured policy information
   ├─▶ Source attribution for each condition
   ├─▶ Links to official documentation
   └─▶ Prominent disclaimer
```

## Key Architectural Decisions

### 1. Modular Monolith
**Decision:** Single deployable application with clear module boundaries

**Rationale:**
- MVP scope doesn't justify distributed services overhead
- Clear separation between API, domain, and infrastructure layers
- Can extract services later if scaling or ownership boundaries emerge
- Simpler deployment and local development

**Trade-offs:**
- Lower operational complexity
- Easier local development
- Single deployment unit
- Must maintain discipline on module boundaries

### 2. In-Memory Storage
**Decision:** No database; use in-memory repositories with curated content

**Rationale:**
- Content is relatively static and curated
- No user accounts, claims, or transactions
- No persistent user state required
- MVP focuses on information presentation, not data management

**Trade-offs:**
- Zero database infrastructure/cost
- Simple deployment
- Fast reads (no network I/O)
- Content updates require code deployment
- Not suitable for user-generated content

**When to add database:**
- Content versioning/audit history needed
- Editorial workflows required
- User accounts or persistent state added
- Multiple content editors needed

### 3. Source Integrity as Backend Concern
**Decision:** Validate source references at service layer, not just frontend

**Rationale:**
- Treat source attribution as a product invariant
- Prevent unattributed claims from reaching users
- API contract guarantees source integrity
- Testing validates this constraint

**Implementation:**
```java
// Every PolicyCondition has sourceId
PolicyCondition("VC_INCOME_REDUCTION", "...", "...", "SRC-004")

// SourceValidationService ensures sourceIds exist
validateSourceIntegrity(content) → throws if missing sources

// Tests validate this invariant
@Test void policyConditions_AllHaveValidSourceIds()
```

### 4. Informational Result Type (Never Eligibility)
**Decision:** All results are INFORMATIONAL, never ELIGIBLE/APPROVED

**Rationale:**
- Product boundary: explain policy vs determine eligibility
- Reduces liability and user confusion
- Clear separation between information and decision
- Enforced in code and tests

**Implementation:**
```java
// Result type is enum with only informational value
public enum ResultType {
    INFORMATIONAL  // Only option
}

// Benefits include "up to" qualifier
new MonetaryBenefit(4000, "USD", "up to")
new Duration(3, "months", "up to")

// Tests validate no eligibility language
@Test void responseContent_NeverContainsEligibilityLanguage()
```

### 5. Request ID Lifecycle
**Decision:** Generate once per request, reuse in logs and error responses

**Rationale:**
- Enables request tracing across layers
- Consistent identifier for debugging
- Included in error responses for support
- Generated at API boundary, flows through services

**Implementation:**
```java
String requestId = RequestIdGenerator.generate();
log.info("Processing [requestId={}]", requestId);
return response.withRequestId(requestId);
```

### 6. Fixed Content Timestamps
**Decision:** `lastUpdated` uses content-review date, not `Instant.now()`

**Rationale:**
- Timestamp represents content version, not request time
- Prevents false impression of continuously updated content
- Honest about when content was actually reviewed
- Enables cache validation on client side

**Implementation:**
```java
private static final Instant CONTENT_REVIEW_DATE = 
    Instant.parse("2026-10-10T00:00:00Z");
```

### 7. Test Strategy
**Decision:** Focus on critical behaviors and invariants, not 100% coverage

**Test Suite:**
- API contract (valid/invalid requests, status codes)
- Source integrity validation
- INFORMATIONAL result enforcement
- Benefit wording ("up to" qualifier)
- No eligibility language in responses

**Coverage: 78%** (threshold: 75%)

**Rationale:**
- Testing behaviors that protect product boundaries
- Strong answer for "how did you ensure reliability?"
- Pragmatic vs vanity metric

### 8. Deployment Strategy
**Decision:** Auto-deploy from GitHub main branch

**Pipeline:**
```
git push → GitHub → Render auto-deploy
         → Vercel auto-deploy

Backend: Render (free tier, cold starts acceptable for MVP)
Frontend: Vercel (Edge network, instant)
```

**Quality gates:**
- `mvn verify` must pass (tests + coverage + formatting)
- Spotless formatting enforced at compile time
- Docker build uses `-DskipTests` for speed (tests run in CI)

## Technology Choices

### Backend
- **Java 21**: LTS with modern features (records, pattern matching)
- **Spring Boot 4.0**: Standard enterprise framework
- **Maven**: Dependency management + build lifecycle
- **JUnit 5 + MockMvc**: Testing framework
- **JaCoCo**: Coverage measurement
- **Spotless**: Code formatting enforcement

### Frontend
- **Next.js 14**: React with SSR/SSG capabilities
- **TypeScript**: Type safety
- **Tailwind CSS**: Utility-first styling
- **Vercel**: Zero-config deployment

### Infrastructure
- **Render**: Backend hosting (auto-deploy from GitHub)
- **Vercel**: Frontend hosting (Edge network)
- **GitHub**: Source control + CI trigger

## Security Considerations

### 1. No Authentication (Intentional)
- MVP is public information tool
- No user accounts or PII
- No claims or financial data
- Authentication would add complexity without current benefit

### 2. CORS
- Explicit origin allowlist
- Frontend origin: `https://safetynetnavigator.vercel.app`
- Localhost for development: `http://localhost:3000`

### 3. Input Validation
- Bean Validation on all request DTOs
- Scenario ID validation against supported list
- Structured error responses (no internal details leaked)

### 4. Dependency Security
- Spring Boot managed dependencies
- Regular updates via Renovate (configured)
- No known CVEs in current stack

## Observability

### Health Endpoint
```
GET /actuator/health
→ {"status":"UP"}
```

### Request Tracing
- Unique requestId per request
- Logged at entry, service, and error points
- Included in error responses

### Structured Logging
```
INFO  - Processing journey request [requestId=abc-123, scenarioId=CONTRACT_CANCELLED]
INFO  - Journey request processed successfully [requestId=abc-123]
ERROR - Unsupported scenario [requestId=abc-123, scenarioId=INVALID]
```

## Future Evolution Paths

### Short-term (Next Iterations)
- [DONE] Add 2nd verified scenario
- Content versioning metadata
- Additional test scenarios (medical, disability, etc.)
- Enhanced frontend accessibility

### Medium-term
- Database for content versioning
- Editorial workflow for content updates
- Contract testing (Pact) between frontend/backend
- Enhanced observability (metrics, traces)

### Long-term (If Product Scales)
- Content management system for editors
- Multi-tenancy (different insurance products)
- API versioning strategy
- Extract services if ownership/scaling boundaries emerge
- Caching layer (Redis) if traffic increases

## Performance Characteristics

### Backend
- **Cold start**: ~20-30 seconds (Render free tier)
- **Warm response**: ~100-200ms
- **In-memory data**: Sub-millisecond access
- **Acceptable for MVP**: Yes (no SLA requirements)

### Frontend
- **Vercel Edge**: Global CDN distribution
- **Initial load**: <2 seconds
- **Navigation**: Instant (client-side routing)

## Cost Structure

**Current (MVP):**
- Backend: $0 (Render free tier)
- Frontend: $0 (Vercel free tier)
- **Total: $0/month**

**At Scale (if product grows):**
- Backend: ~$7-25/month (Render standard)
- Database: ~$7-20/month (if added)
- Frontend: $0-20/month (Vercel pro if needed)
- **Estimated: $15-65/month**

## Diagram: Critical Invariants

```
┌────────────────────────────────────────────────────────┐
│              PRODUCT BOUNDARIES                        │
│  (Enforced by architecture, validated by tests)       │
├────────────────────────────────────────────────────────┤
│                                                        │
│  1. Source Integrity                                   │
│     Every PolicyCondition → valid sourceId            │
│     Every sourceId → Source in response               │
│                                                        │
│  2. Informational Only                                 │
│     ResultType = INFORMATIONAL (only option)          │
│     No ELIGIBLE, APPROVED, GUARANTEED language        │
│                                                        │
│  3. Benefit Qualification                              │
│     All benefits include "up to" qualifier            │
│     No absolute guarantees in wording                 │
│                                                        │
│  4. Request Traceability                               │
│     Every request → unique requestId                  │
│     Logged + returned in response/errors              │
│                                                        │
│  5. Honest Timestamps                                  │
│     lastUpdated = content review date                 │
│     Not Instant.now() (misleading freshness)          │
│                                                        │
└────────────────────────────────────────────────────────┘
```

## Summary

This architecture demonstrates:

1. **Clear separation of concerns** (API, domain, infrastructure)
2. **Appropriate technology choices** for MVP scope
3. **Product boundaries enforced in code** (source integrity, informational results)
4. **Pragmatic testing** (critical behaviors, not vanity metrics)
5. **Zero-cost deployment** enabling rapid validation
6. **Evolution path** when requirements grow

The system is deliberately simple for MVP, with clear paths to add complexity only when product requirements demand it.
