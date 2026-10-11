# Global Safety Net Navigator - Backend

An independent Spring Boot API that helps users explore publicly available income-protection information, understand what the published policy states, identify what they may need to verify, and find the official source documents.

**Independent educational prototype:** This project is not affiliated with, endorsed by, or operated by SafetyWing. It does not determine personal eligibility, approve claims, initiate claims, or guarantee payouts.

## Overview

The Global Safety Net Navigator is a product-engineering prototype built around a simple hypothesis:

> When someone's income changes unexpectedly, the difficult part may not be finding policy documents—it may be understanding which published information is relevant to their situation and what they should verify next.

The backend provides a small, source-attributed API that turns a supported income situation into structured informational guidance.

The MVP deliberately focuses on **one verified scenario**: "My client cancelled a contract"

This constraint allows the product to validate the complete journey—from source research and domain modelling through API design, validation, source attribution, error handling, testing, and frontend integration—before expanding the domain.

## Product Scope

The API currently provides information for cases involving:

- Termination of a client contract
- Severe loss of variable income
- Published income-protection conditions
- Documented benefit structures
- Information users may need to verify
- Official source references
- Appropriate next steps

The API is intentionally **informational rather than decision-making**.

It does not:

- Determine whether a person qualifies
- Calculate a person's eligibility
- Approve or reject claims
- Initiate claims
- Collect sensitive personal information
- Provide legal or insurance advice
- Guarantee a payout

The distinction between **published policy conditions** and **individual eligibility** is a core product requirement.

## Architecture

The backend follows a modular monolith structure:

```
Next.js Frontend
       |
       v
Spring Boot REST API
       |
       v
  Controllers
       |
       v
    Services
       |
       v
Repository Interfaces
       |
       v
In-Memory Repositories
       |
       v
Curated Policy Content
       |
       v
Official Source References
```

### Package Structure

```
src/main/java/com/safetywing/navigator/
├── api/
│   ├── controller/         # REST controllers
│   ├── dto/
│   │   ├── request/        # Request DTOs
│   │   └── response/       # Response DTOs
│   └── exception/          # API exceptions
├── domain/
│   ├── model/              # Domain entities
│   ├── repository/         # Repository interfaces
│   └── service/            # Business logic
└── infrastructure/
    ├── config/             # Spring configuration
    └── util/               # Utilities
```

The separation keeps HTTP concerns, product/domain logic, persistence abstractions, and infrastructure concerns independent.

## Technology Stack

| Technology | Purpose |
|------------|---------|
| Java 21 | Application language |
| Spring Boot 4.0.2 | REST API framework |
| Maven 3.9+ | Build and dependency management |
| Spring Web | HTTP/REST API |
| Bean Validation | Request validation |
| Spring Boot Actuator | Application health endpoints |
| JUnit 5 | Testing framework |
| Mockito | Test doubles |
| MockMvc | HTTP/API testing |
| JaCoCo | Code coverage |
| Spotless | Code formatting enforcement |

### Why In-Memory Storage?

The MVP intentionally does not use PostgreSQL or another database.

The current product contains curated and relatively static policy information. It does not currently store:

- User accounts
- Claims
- Financial transactions
- User-generated content
- Persistent user journeys

Introducing a database at this stage would add infrastructure without solving an existing product requirement.

Instead, the application uses repository interfaces with an in-memory implementation. This keeps the domain and service layers persistence-agnostic.

A database would become appropriate if the product evolves to require:

- Versioned policy content
- Editorial workflows
- Audit history
- Persistent user state
- Content management
- Multiple independently maintained policy products

This is an intentional MVP trade-off rather than a limitation of the domain model.

## API

**Base path:** `/api/v1`

The API uses JSON and ISO-8601 timestamps.

### Get Supported Scenarios

```http
GET /api/v1/income/scenarios
```

**Example Response:**

```json
{
  "scenarios": [
    {
      "id": "CONTRACT_CANCELLED",
      "label": "My client cancelled a contract",
      "description": "Explore income-protection information for cancelled contracts and severe loss of variable income."
    }
  ],
  "metadata": {
    "totalCount": 1,
    "lastUpdated": "2026-10-10T09:00:00Z"
  }
}
```

`lastUpdated` represents the reviewed content version rather than the time of the individual API request.

### Explore an Income Scenario

```http
POST /api/v1/income/journeys
Content-Type: application/json
```

**Request:**

```json
{
  "scenarioId": "CONTRACT_CANCELLED"
}
```

**Response contains:**

- Selected scenario
- Benefit information
- Policy conditions
- Items to verify
- Disclaimer
- Official sources
- Next steps
- Content metadata

The response uses an informational result type rather than an eligibility or approval result.

## Source Attribution

Source integrity is treated as a backend concern rather than simply a frontend presentation detail.

Policy conditions contain references to their source documents.

The API maintains the invariant:

```
Every policy condition has a sourceId
            ↓
Every sourceId exists in the response's sources
```

This allows the frontend to present the origin of policy information while reducing the risk of displaying unattributed or unsupported claims.

The current reviewed sources include official SafetyWing documentation and the Nomad Citizen policy certificate.

The product does not reproduce the complete policy document. It provides a structured explanation of selected published information and links users back to the authoritative sources.

## Current Policy Scope

The current scenario is based on publicly available Nomad Citizen documentation reviewed in October 2026.

For the variable-income protection scenario, the reviewed policy describes conditions including:

- A 90% or greater income decrease averaged over 90 consecutive days due to termination or inability to agree on a new contract
- The required income-history comparison
- Actively seeking jobs or income matching skills and education
- At least 24 consecutive months of coverage before the income loss
- Supporting documentation relating to income loss and previous income

The published benefit structure describes:

- Up to US$4,000 per month
- Up to 3 months
- Access to up to 3 months every 24 months

These statements describe published policy information. They do not establish an individual's eligibility.

**Only the policy issuer can make authoritative determinations regarding individual eligibility and claims.**

## Error Handling

The API uses structured errors to provide predictable client behaviour.

| HTTP Status | Meaning |
|-------------|---------|
| 400 | Invalid or malformed request |
| 422 | Unsupported scenario |
| 500 | Unexpected internal/application error |
| 503 | Service unavailable |

Errors include a request identifier and timestamp to make failures easier to trace.

**Example:**

```json
{
  "code": "UNSUPPORTED_SCENARIO",
  "message": "The requested scenario is not currently supported.",
  "requestId": "7b9c6e2a-7d6f-4f6d-a1e2-5b2e4d9c8f11",
  "timestamp": "2026-10-10T09:15:32Z"
}
```

Request IDs are generated per request rather than using fixed identifiers.

## Validation and Safety Boundaries

The API deliberately validates both technical input and product-level constraints.

Examples include:

- Required request fields
- Supported scenario identifiers
- Source attribution
- Informational result types
- Benefit wording such as "up to"
- Separation of policy conditions from individual eligibility
- Structured error responses

The backend should never return language that implies:

- ELIGIBLE
- APPROVED
- CLAIM INITIATED
- GUARANTEED PAYOUT

The product instead communicates:

- "The policy states..."
- "The policy requires..."
- "You may need to verify..."
- "Contact the policy issuer for an authoritative determination."

## Testing

The project uses automated tests to validate both API behaviour and product constraints.

Key scenarios include:

- Retrieving supported scenarios
- Submitting a valid scenario
- Rejecting unsupported scenarios
- Validating malformed requests
- Verifying source attribution
- Verifying benefit wording
- Ensuring informational results do not become eligibility decisions
- Validating structured error responses
- Ensuring request identifiers are present
- Validating application behaviour under expected failures

JaCoCo is configured with an 80% minimum line-coverage threshold.

The objective is not simply high coverage; tests are intended to protect the product's most important behavioural and policy boundaries.

## Code Quality

The project uses automated formatting and build checks.

**Check Formatting:**

```bash
./mvnw spotless:check
```

**Apply Formatting:**

```bash
./mvnw spotless:apply
```

**Run Tests:**

```bash
./mvnw test
```

**Run Full Verification:**

```bash
./mvnw verify
```

The build includes JaCoCo coverage verification and Git commit metadata.

## Running Locally

### Requirements

- Java 21
- Maven 3.9+

### Start the Application

```bash
./mvnw spring-boot:run
```

The API starts on: `http://localhost:8080`

### Example Request

```bash
curl http://localhost:8080/api/v1/income/scenarios
```

## Configuration

The application can be configured through Spring properties and environment variables.

For local frontend development, the frontend API base URL is:

```
http://localhost:8080/api/v1
```

CORS origins should be configured according to the deployment environment rather than assuming localhost in production.

## Project Decisions

### Why One Scenario?

The MVP is intentionally narrow.

Rather than building a large catalogue of partially verified scenarios, the project validates one complete product journey with carefully reviewed source material.

This creates a stronger foundation for expanding the domain later.

### Why a Modular Monolith?

The product does not currently have enough domain or operational complexity to justify distributed services.

A modular monolith provides:

- Clear domain boundaries
- Simpler deployment
- Lower operational overhead
- Easier local development
- Room to extract services if genuine scaling or ownership boundaries emerge

### Why No Authentication?

The MVP does not require accounts or persistent user state.

Removing authentication keeps the prototype focused on its primary product hypothesis: helping users understand published information.

### Why Not Build an Eligibility Engine?

Determining personal eligibility would introduce a fundamentally different product responsibility.

The MVP deliberately helps users understand what the published policy says and what they should verify, rather than attempting to make an insurance decision.

## Content Governance

Policy information is treated as curated product content rather than arbitrary generated text.

Before new scenarios are added, the expected workflow is:

```
Official source
      ↓
Policy/content review
      ↓
Structured domain model
      ↓
Source attribution
      ↓
Automated validation/tests
      ↓
API
      ↓
Frontend
```

Future versions could introduce a dedicated content-management or editorial workflow if the number of supported policies and scenarios grows.

## Future Evolution

Possible future improvements include:

- Additional verified income scenarios
- Versioned policy-content management
- Editorial review workflows
- Persistent content storage when audit/versioning requirements emerge
- Contract testing between frontend and backend
- Expanded observability and operational dashboards
- More comprehensive end-to-end testing
- Accessibility testing across the complete user journey

These are deliberately future considerations rather than requirements for the current MVP.

## Related Project

**Global Safety Net Navigator - Frontend**

The frontend provides the user-facing scenario selection and source-attributed information journey.

## Disclaimer

This project is an independent educational prototype created for product-engineering demonstration and research purposes.

It is not affiliated with, endorsed by, or operated by SafetyWing.

The information presented is based on publicly available documentation reviewed during development. It does not constitute insurance, legal, financial, or other professional advice.

The application does not determine personal eligibility, approve claims, initiate claims, or guarantee payouts.

Users should consult the official policy documentation and contact the relevant provider for authoritative information about their individual circumstances.

## License

This project is provided for educational and portfolio purposes.
