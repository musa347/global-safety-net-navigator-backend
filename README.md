# Global Safety Net Navigator - Backend API

Spring Boot REST API for the Global Safety Net Navigator income protection information system.

## Tech Stack

- **Java 21**
- **Spring Boot 4.0.2**
- **Maven 3.9+**
- In-memory data repositories (no database required for MVP)

## Prerequisites

- Java 21 (JDK 21)
- Maven 3.9 or higher

## Quick Start

### Build the Project

```bash
mvn clean install
```

### Run the Application

```bash
mvn spring-boot:run
```

The API will start on `http://localhost:8080`

### Run Tests

```bash
mvn test
```

## API Endpoints

### 1. Get Scenario Catalogue

**Endpoint:** `GET /api/v1/income/scenarios`

**Description:** Returns the list of supported income-disruption scenarios.

**Example:**
```bash
curl http://localhost:8080/api/v1/income/scenarios
```

**Response:**
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
    "lastUpdated": "2026-10-11T12:00:00Z"
  }
}
```

### 2. Submit Journey Request

**Endpoint:** `POST /api/v1/income/journeys`

**Description:** Submit a scenario selection and receive benefit information.

**Example:**
```bash
curl -X POST http://localhost:8080/api/v1/income/journeys \
  -H "Content-Type: application/json" \
  -d '{"scenarioId": "CONTRACT_CANCELLED"}'
```

**Response:** Complete journey response with benefit information, policy conditions, sources, and next steps (see API specification).

## Project Structure

```
src/main/java/com/safetywing/navigator/
├── GlobalNavigatorApplication.java      # Main application
├── api/
│   ├── controller/                      # REST controllers
│   ├── dto/                             # Request/Response DTOs
│   └── exception/                       # Exception handlers
├── domain/
│   ├── model/                           # Domain models
│   ├── repository/                      # In-memory repositories
│   └── service/                         # Business logic
└── infrastructure/
    ├── config/                          # Configuration classes
    └── util/                            # Utilities
```

## Configuration

### application.properties

```properties
server.port=8080
cors.allowed-origins=http://localhost:3000
```

## Code Quality

- **Spotless:** Code formatting with Palantir Java Format
- **JaCoCo:** 80% minimum test coverage requirement
- **Git Commit ID:** Build info tracking

### Format Code

```bash
mvn spotless:apply
```

### Check Code Coverage

```bash
mvn verify
```

## Integration with Frontend

The Next.js frontend should be configured with:

```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api/v1
```

## Architecture Highlights

- **Modular Monolith:** Domain-driven package structure
- **In-Memory Data:** No database for MVP (verified content from policy documents)
- **Source Attribution:** All policy conditions reference official sources
- **Validation:** Request validation with Bean Validation
- **Error Handling:** Centralized exception handling with consistent error responses

## Acceptance Criteria

This implementation satisfies all 10 acceptance criteria from User Story US-001:

✅ AC-001: Scenario catalogue returns only verified scenarios  
✅ AC-002: Valid scenario returns source-attributed information  
✅ AC-003: Benefit information includes "up to" qualifier  
✅ AC-004: Policy conditions distinguished from eligibility  
✅ AC-005: Official links available  
✅ AC-006: No qualification/approval claims  
✅ AC-007: Comprehensive test coverage  
✅ AC-008: Error handling for invalid requests  
✅ AC-009: Content matches verification document  
✅ AC-010: Proper UI state handling (frontend)

## Next Steps

1. **Run locally:** `mvn spring-boot:run`
2. **Test endpoints:** Use curl or Postman
3. **Connect frontend:** Configure NEXT_PUBLIC_API_URL
4. **Review logs:** Check console output for request tracking

## License

Educational prototype - not affiliated with SafetyWing.
