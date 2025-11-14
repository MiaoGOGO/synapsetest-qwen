# Backend Directory Exploration - Complete Index

This is a comprehensive guide to the SynapseTest backend architecture. Use this document to navigate the codebase efficiently.

## Documentation Files Overview

This exploration includes 4 comprehensive documents:

1. **backend_exploration_summary.md** - Complete detailed analysis
   - Technology stack details
   - Package structure breakdown
   - Database architecture (PostgreSQL + MongoDB)
   - User story mapping
   - Development guidelines

2. **quick_reference.md** - Fast lookup guide
   - Framework info
   - Database connections
   - Service inventory
   - API endpoints
   - Common queries
   - Development checklist

3. **architecture_diagram.md** - Visual representations
   - High-level architecture
   - Component interactions
   - Data flow diagrams
   - Database relationships
   - Request/response flows

4. **INDEX.md** - This file
   - Document navigation
   - Quick file location guide
   - Key information at a glance

---

## Quick File Location Guide

### By File Type

#### Models (8 files)
| File | Story | Location |
|------|-------|----------|
| TestTask.java | US1 | `/model/TestTask.java` |
| TestEnvironment.java | US1 | `/model/TestEnvironment.java` |
| TestVersion.java | US1 | `/model/TestVersion.java` |
| ResourcePool.java | US1 | `/model/ResourcePool.java` |
| TestCase.java | US2 | `/model/TestCase.java` |
| AIModel.java | US2 | `/model/AIModel.java` |
| QualityReport.java | US3 | `/model/QualityReport.java` |
| MonitoringData.java | US3 | `/model/MonitoringData.java` |

#### Services (13 files)
| File | Story | Responsibility |
|------|-------|-----------------|
| TestTaskService.java | US1 | Task CRUD and lifecycle |
| TestEnvironmentService.java | US1 | Environment management |
| TestVersionService.java | US1 | Version management |
| ResourcePoolService.java | US1 | Resource allocation |
| TestRecommendationService.java | US1 | AI recommendations |
| AITestCaseGenerationService.java | US2 | Generate from text |
| AITestCaseOptimizationService.java | US2 | Optimize cases |
| AIModelService.java | US2 | Model lifecycle |
| TestCaseService.java | US2 | Case CRUD |
| QualityReportService.java | US3 | Report generation |
| MonitoringService.java | US3 | Real-time monitoring |
| ReportingService.java | US3 | Report details |
| QualityTraceabilityService.java | US3 | Quality tracking |

#### Controllers (7 files)
| File | Endpoints | Story |
|------|-----------|-------|
| TestTaskController.java | /test-tasks | US1 |
| TestEnvironmentController.java | /test-environments | US1 |
| TestVersionController.java | /test-versions | US1 |
| TestCaseController.java | /test-cases | US2 |
| ReportController.java | /reports | US3 |
| MonitoringController.java | /monitoring | US3 |
| HealthController.java | /health | All |

#### Repositories (8 files)
| File | Database | Story |
|------|----------|-------|
| TestTaskRepository.java | PostgreSQL | US1 |
| TestEnvironmentRepository.java | PostgreSQL | US1 |
| TestVersionRepository.java | PostgreSQL | US1 |
| ResourcePoolRepository.java | PostgreSQL | US1 |
| TestCaseRepository.java | PostgreSQL | US2 |
| AIModelRepository.java | MongoDB | US2 |
| QualityReportRepository.java | MongoDB | US3 |
| MonitoringDataRepository.java | MongoDB | US3 |

---

## User Story Quick Links

### User Story 1: Intelligent Test Task Scheduling
**Goal:** Smart task scheduling with AI recommendations and resource management

**Key Files:**
- Models: TestTask, TestEnvironment, TestVersion, ResourcePool
- Services: TestTaskService, ResourcePoolService, TestRecommendationService
- Controller: TestTaskController
- DB: PostgreSQL (test_tasks, test_environments, test_versions, resource_pools)
- Key Methods: createTestTask(), allocateResources(), getRecommendation()

**Critical Paths:**
- Task Creation: `POST /api/v1/test-tasks` → TestTaskController → TestTaskService → PostgreSQL
- Resource Allocation: `ResourcePoolService.allocateResources()` → Capacity check → Update DB
- Task Scheduling: `TestRecommendationService.getTestRecommendation()` → AI logic

### User Story 2: AI-Powered Test Case Generation
**Goal:** Generate test cases from natural language using AI/ML

**Key Files:**
- Models: TestCase, AIModel
- Services: AITestCaseGenerationService, AITestCaseOptimizationService, AIModelService
- Controller: TestCaseController
- DB: PostgreSQL (test_cases), MongoDB (ai_models)
- Key Methods: generateTestCases(), optimizeTestCases(), calculateConfidenceScore()

**Critical Paths:**
- Generation: `POST /api/v1/test-cases/generate` → AITestCaseGenerationService → Scenario analysis → MySQL
- Optimization: `AITestCaseOptimizationService.optimize()` → ML model evaluation
- Security Audit: `AIModelService` → Vulnerability scan → Compliance tracking

### User Story 3: Monitoring & Reporting (Visualization & Analysis)
**Goal:** Real-time test monitoring and comprehensive quality reporting

**Key Files:**
- Models: QualityReport, MonitoringData
- Services: MonitoringService, QualityReportService, ReportingService, QualityTraceabilityService
- Controllers: MonitoringController, ReportController
- DB: MongoDB (quality_reports, monitoring_data)
- Key Methods: updateMonitoringData(), generateReport(), assessRisk()

**Critical Paths:**
- Monitoring: `GET /api/v1/monitoring/{taskId}` → MonitoringService → Real-time metrics
- Report Generation: Task completion → QualityReportService → Risk assessment → Report creation
- Risk Analysis: Defect stats + Performance metrics → Risk calculation → Recommendations

---

## Database Location Reference

### PostgreSQL Tables
**Connection:** `jdbc:postgresql://localhost:5432/test_management`
- test_tasks (US1)
- test_cases (US2)
- test_environments (US1)
- test_versions (US1)
- resource_pools (US1)
- task_test_cases (Association)

**Schema Location:** `/src/main/resources/schema.sql`

### MongoDB Collections
**Connection:** `mongodb://localhost:27017/test_management_ai`
- ai_models (US2)
- quality_reports (US3)
- monitoring_data (US3)

---

## Configuration Files

### Spring Boot Configuration
- **Main Config:** `/src/main/resources/application.yml`
  - Server: port 8080, context-path /api/v1
  - Datasources: PostgreSQL, MongoDB, Redis, Kafka, RabbitMQ
  - Management: Prometheus metrics enabled
  - Resilience: Circuit breaker config

- **Additional Config:** `/src/main/resources/application-config.yml`
- **Service Discovery:** `/src/main/resources/service-discovery.yml`

### Configuration Classes
Located in `/config/`:
- SecurityConfig.java - Auth setup
- KafkaConfig.java - Stream processing
- RabbitMQConfig.java - Async messaging
- MonitoringConfig.java - Prometheus integration
- GatewayConfig.java - API routing
- WebConfig.java - Web settings

---

## Directory Tree (Complete)

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/synapsetest/testmanagement/
│   │   │   ├── config/               (6 config files)
│   │   │   ├── controller/           (7 controllers)
│   │   │   ├── service/              (13 services)
│   │   │   ├── model/                (8 models)
│   │   │   ├── repository/           (8 repositories)
│   │   │   ├── dto/                  (6 DTOs)
│   │   │   ├── entity/               (BaseEntity)
│   │   │   ├── exception/            (Custom exceptions)
│   │   │   ├── interceptor/          (HTTP interceptors)
│   │   │   └── TestManagementApplication.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-config.yml
│   │       ├── service-discovery.yml
│   │       └── schema.sql
│   └── tests/
│       ├── unit/
│       ├── integration/
│       └── contract/
├── pom.xml                           (Maven config)
└── Dockerfile                        (Docker config)
```

---

## Package Organization

```
com.synapsetest.testmanagement
├── config              Spring configuration beans
├── controller          REST API endpoints (@RestController)
├── service             Business logic (@Service)
├── model               Domain entities (@Entity, @Document)
├── repository          Data access (@Repository)
├── dto                 Transfer objects (Request/Response)
├── entity              Base entity classes
├── exception           Custom exception classes
└── interceptor         HTTP interceptors
```

---

## API Base URL
```
http://localhost:8080/api/v1
```

### Main Endpoint Groups
- `/test-tasks` - Test task management
- `/test-cases` - Test case management
- `/test-environments` - Environment configuration
- `/test-versions` - Version management
- `/reports` - Quality reports
- `/monitoring` - Real-time monitoring
- `/health` - System health

---

## Key Statistics

| Metric | Value |
|--------|-------|
| Total Models | 8 |
| Total Services | 13 |
| Total Controllers | 7 |
| Total Repositories | 8 |
| PostgreSQL Tables | 6 |
| MongoDB Collections | 3 |
| API Endpoints | 30+ |
| Configuration Files | 3 |
| Config Classes | 6 |

---

## Technology Stack Summary

| Component | Technology | Version |
|-----------|-----------|---------|
| Framework | Spring Boot | 3.1.5 |
| Language | Java | 11+ |
| Build Tool | Maven | 3.x |
| Primary DB | PostgreSQL | Latest |
| NoSQL DB | MongoDB | Latest |
| Cache | Redis | Latest |
| Message Queue | Kafka/RabbitMQ | Latest |
| API Gateway | Spring Cloud Gateway | 2022.0.4 |
| Testing | JUnit 5 | Latest |
| Resilience | Resilience4j | 2.0.2 |
| Monitoring | Prometheus | Latest |

---

## Development Workflow

### To Add a New Feature:
1. Create Model in `/model/` (extends BaseEntity)
2. Create Repository in `/repository/` (extends JpaRepository/MongoRepository)
3. Create Service in `/service/` (with @Transactional methods)
4. Create Controller in `/controller/` (with @RestController)
5. Add DTOs in `/dto/` if needed
6. Update `/resources/schema.sql` for DB changes
7. Add tests to `/tests/`
8. Update `/resources/application.yml` if needed

### To Understand Existing Code:
1. Start with User Story mapping (see section above)
2. Locate corresponding model in `/model/`
3. Review service implementation in `/service/`
4. Check controller endpoints in `/controller/`
5. Review repository queries in `/repository/`
6. Examine database schema in `schema.sql`

---

## Important Notes

### Data Storage Strategy
- **PostgreSQL:** Relational data (tasks, cases, environments, versions, resources)
- **MongoDB:** Flexible schema data (AI models, quality reports, monitoring)
- **Redis:** Session and cache storage

### Architecture Patterns
- Repository Pattern for data access
- Service Layer for business logic
- DTO Pattern for API communication
- Dependency Injection for loose coupling
- Global Exception Handling
- Transactional boundaries at service layer

### Key Features
- RESTful API with standard HTTP methods
- Request validation with JSR-303
- Pagination support for list endpoints
- Logging with SLF4J
- Health checks and metrics
- Circuit breaker for resilience
- Prometheus monitoring

---

## Document Navigation Matrix

| Need | Document | Section |
|------|----------|---------|
| Architecture overview | architecture_diagram.md | High-Level Architecture |
| File locations | INDEX.md | Quick File Location Guide |
| Service details | backend_exploration_summary.md | Service Layer (Section 3.3) |
| Database schema | backend_exploration_summary.md | Database Architecture (Section 4) |
| API endpoints | quick_reference.md | API Endpoints Structure |
| User story info | backend_exploration_summary.md | User Story Mapping (Section 6) |
| Development guide | backend_exploration_summary.md | Development Guidelines (Section 12) |
| Quick lookup | quick_reference.md | All sections |

---

## Last Updated
- Framework: Spring Boot 3.1.5
- Java Version: 11+
- Database: PostgreSQL + MongoDB
- Total Components: 50+ files in main codebase

---

END OF INDEX
