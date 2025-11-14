# Backend Architecture Exploration Summary

## Project Overview
**Name:** SynapseTest - AI驱动测试任务管理系统 (AI-Driven Test Task Management System)  
**Version:** 1.0.0-SNAPSHOT  
**Type:** Multi-module microservices backend  

---

## 1. Technology Stack & Framework

### Primary Framework: Spring Boot 3.1.5
- **Language:** Java 11
- **Build Tool:** Maven 3.x
- **Parent POM:** spring-boot-starter-parent:3.1.5

### Key Technologies:
- **Web Framework:** Spring Boot Web (REST APIs)
- **ORM/Data Access:** Spring Data JPA + Hibernate
- **Primary Database:** PostgreSQL (relational data)
- **NoSQL Database:** MongoDB (AI models, reports, monitoring data)
- **Cache:** Redis (session/cache management)
- **Message Queues:** 
  - Kafka (stream processing)
  - RabbitMQ (async messaging)
- **API Gateway:** Spring Cloud Gateway
- **Resilience:** Resilience4j (circuit breaker pattern)
- **Monitoring:** Micrometer + Prometheus
- **Testing Framework:** JUnit 5, Spring Boot Test

### Dependencies Summary:
```xml
- spring-boot-starter-web
- spring-boot-starter-data-jpa (PostgreSQL via Hibernate)
- spring-boot-starter-data-mongodb (MongoDB for AI/reports)
- spring-boot-starter-data-redis
- spring-cloud-starter-gateway
- spring-cloud-starter-config
- spring-kafka & spring-kafka-test
- spring-boot-starter-amqp (RabbitMQ)
- resilience4j-spring-boot3 (v2.0.2)
- micrometer-registry-prometheus
- spring-boot-starter-actuator
- spring-boot-starter-validation
- lombok (utility library)
```

---

## 2. Folder Structure & Architecture

### Base Directory
```
/Users/mengwei/ww/github/synapsetest-qwen/backend/
├── src/
│   ├── main/
│   │   ├── java/com/synapsetest/testmanagement/
│   │   │   ├── config/                 (Spring configurations)
│   │   │   ├── controller/             (REST API endpoints)
│   │   │   ├── service/                (Business logic)
│   │   │   ├── model/                  (Domain models/entities)
│   │   │   ├── repository/             (Data access layer)
│   │   │   ├── dto/                    (Data transfer objects)
│   │   │   ├── entity/                 (Base entity classes)
│   │   │   ├── exception/              (Custom exceptions)
│   │   │   ├── interceptor/            (HTTP interceptors)
│   │   │   └── TestManagementApplication.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-config.yml
│   │       ├── service-discovery.yml
│   │       └── schema.sql
│   └── tests/
│       ├── unit/                       (Unit tests)
│       ├── integration/                (Integration tests)
│       └── contract/                   (Contract tests)
├── pom.xml
└── Dockerfile
```

---

## 3. Package Structure Details

### 3.1 Configuration Modules
**Location:** `/src/main/java/com/synapsetest/testmanagement/config/`

| File | Purpose |
|------|---------|
| `SecurityConfig.java` | Spring Security configuration, auth setup |
| `KafkaConfig.java` | Kafka producer/consumer configuration |
| `RabbitMQConfig.java` | RabbitMQ message queue setup |
| `MonitoringConfig.java` | Prometheus/Micrometer metrics configuration |
| `GatewayConfig.java` | Spring Cloud Gateway routing configuration |
| `WebConfig.java` | Web MVC configuration, CORS, interceptors |

### 3.2 Controllers (REST API Endpoints)
**Location:** `/src/main/java/com/synapsetest/testmanagement/controller/`

| Controller | Endpoints | Purpose |
|-----------|-----------|---------|
| `TestTaskController.java` | `/api/v1/test-tasks` | Task CRUD, start/cancel operations |
| `TestCaseController.java` | `/api/v1/test-cases` | Test case management |
| `TestEnvironmentController.java` | `/api/v1/test-environments` | Environment configuration |
| `TestVersionController.java` | `/api/v1/test-versions` | Version management |
| `ReportController.java` | `/api/v1/reports` | Quality report endpoints |
| `MonitoringController.java` | `/api/v1/monitoring` | Real-time monitoring data |
| `HealthController.java` | `/api/v1/health` | Health check endpoint |

### 3.3 Service Layer (Business Logic)
**Location:** `/src/main/java/com/synapsetest/testmanagement/service/`

#### User Story 1: Test Task Scheduling Services
| Service | Responsibility |
|---------|-----------------|
| `TestTaskService.java` | Create, retrieve, start, cancel test tasks |
| `TestEnvironmentService.java` | Manage test environments |
| `TestVersionService.java` | Manage test versions |
| `ResourcePoolService.java` | Allocate/release test resources |
| `TestRecommendationService.java` | Provide AI-based task recommendations |

#### User Story 2: AI Test Case Generation Services
| Service | Responsibility |
|---------|-----------------|
| `AITestCaseGenerationService.java` | Generate test cases from natural language using AI/ML |
| `AITestCaseOptimizationService.java` | Optimize generated test cases |
| `AIModelService.java` | Manage AI models (security audit, compliance) |
| `TestCaseService.java` | Test case CRUD operations |

#### User Story 3: Monitoring & Reporting Services
| Service | Responsibility |
|---------|-----------------|
| `QualityReportService.java` | Generate quality reports with risk assessment |
| `MonitoringService.java` | Real-time test execution monitoring |
| `ReportingService.java` | Generate detailed test reports |
| `QualityTraceabilityService.java` | Track quality metrics and traceability |

### 3.4 Data Models (Domain Entities)
**Location:** `/src/main/java/com/synapsetest/testmanagement/model/`

#### User Story 1 Models (PostgreSQL)
```java
TestTask.java              // Fields: name, description, environment, version, testScope, status, priority, createdBy
TestEnvironment.java       // Fields: name, description, config (JSONB), status
TestVersion.java           // Fields: name, description, productVersion, baselineVersion, config (JSONB)
ResourcePool.java          // Fields: name, type (VM/CONTAINER/DEVICE), capacity, used, config, status
```

#### User Story 2 Models
```java
TestCase.java              // PostgreSQL: title, description, steps, expectedResults, priority, type, status, tags, createdBy
AIModel.java               // MongoDB: name, version, description, filePath, securityStatus, vulnerabilityScanResult, complianceStatus, metrics
```

#### User Story 3 Models (MongoDB)
```java
QualityReport.java         // taskId, name, summary, testResults[], defectStats, performanceMetrics, riskAssessment
MonitoringData.java        // taskId, status, progress, executedCases, passedCases, failedCases, resourceUsage, performanceMetrics
```

#### Base Entity
```java
BaseEntity.java            // Abstract parent: id (UUID), createdAt, updatedAt
```

### 3.5 Data Access Layer (Repositories)
**Location:** `/src/main/java/com/synapsetest/testmanagement/repository/`

#### PostgreSQL Repositories (JPA)
- `TestTaskRepository` - Methods: findByStatus(), findByEnvironment(), findByCreatedBy(), findByStatusOrderByPriorityDesc()
- `TestCaseRepository` - Methods: findByType(), findByStatus(), findByCreatedBy()
- `TestEnvironmentRepository` - Methods: findByStatus(), findByName()
- `TestVersionRepository` - Methods: findByProductVersion()
- `ResourcePoolRepository` - Methods: findByStatus(), findByType(), findByStatusAndType()

#### MongoDB Repositories
- `QualityReportRepository` - Methods: findByTaskId(), findByStatus(), findByGeneratedAtBetween(), findTop10ByOrderByGeneratedAtDesc()
- `MonitoringDataRepository` - Methods: findByTaskId(), findByStatus()
- `AIModelRepository` - Methods: findBySecurityStatus(), findByName()

### 3.6 DTOs (Data Transfer Objects)
**Location:** `/src/main/java/com/synapsetest/testmanagement/dto/`

| DTO Class | Purpose |
|-----------|---------|
| `TestTaskRequest.java` | Input DTO for creating test tasks |
| `TestTaskResponse.java` | Output DTO with recommendations |
| `TestCaseRequest.java` | Input DTO for test case creation |
| `TestCaseResponse.java` | Output DTO for test cases |
| `AITestCaseGenerationRequest.java` | Input for AI test case generation |
| `ApiResponse.java` | Standard API response wrapper |

### 3.7 Exception Handling
**Location:** `/src/main/java/com/synapsetest/testmanagement/exception/`

- `GlobalExceptionHandler.java` - Centralized exception handling
- `ResourceNotFoundException.java` - 404 errors
- `ValidationException.java` - Validation error handling

### 3.8 Interceptors
**Location:** `/src/main/java/com/synapsetest/testmanagement/interceptor/`

- `AuthInterceptor.java` - Authentication token validation
- `LoggingInterceptor.java` - Request/response logging

---

## 4. Database Architecture

### PostgreSQL Schema (Relational Data)
**Connection:** `jdbc:postgresql://localhost:5432/test_management`

#### Tables
1. **test_tasks** (User Story 1)
   - PK: id (UUID)
   - Fields: name, description, environment, version, testScope, status, priority, createdBy
   - Indexes: idx_test_tasks_status, idx_test_tasks_created_at
   - Constraints: status in (PENDING, RUNNING, COMPLETED, CANCELLED), priority 0-10

2. **test_cases** (User Story 2)
   - PK: id (UUID)
   - Fields: title, description, steps (JSONB), expectedResults, priority, type, status, tags, createdBy
   - Indexes: idx_test_cases_type, idx_test_cases_status

3. **test_environments** (User Story 1)
   - PK: id (UUID)
   - Fields: name (unique), description, config (JSONB), status
   - Index: idx_test_environments_status

4. **test_versions** (User Story 1)
   - PK: id (UUID)
   - Fields: name, description, productVersion, baselineVersion, config (JSONB)

5. **resource_pools** (User Story 1)
   - PK: id (UUID)
   - Fields: name (unique), description, type, capacity, used, config, status
   - Constraint: used <= capacity
   - Index: idx_resource_pools_status

6. **task_test_cases** (Association)
   - PK: (task_id, test_case_id)
   - FKs: References test_tasks, test_cases
   - Field: execution_order

#### Features
- Auto-generated UUIDs for all primary keys
- Automatic `created_at`/`updated_at` timestamp management via triggers
- PostgreSQL JSONB support for flexible config storage
- Foreign key constraints with CASCADE delete

### MongoDB Collections (NoSQL - Flexible Schema)
**Connection:** `mongodb://localhost:27017/test_management_ai`

1. **ai_models** (User Story 2)
   - Fields: id, name, version, description, filePath, securityStatus, complianceStatus, vulnerabilityScanResult, metrics

2. **quality_reports** (User Story 3)
   - Fields: taskId, name, summary, testResults[], defectStats, performanceMetrics, riskAssessment

3. **monitoring_data** (User Story 3)
   - Fields: taskId, status, progress, executedCases, passedCases, failedCases, resourceUsage, performanceMetrics

### Redis Cache
**Connection:** `localhost:6379`
- Session storage
- Cache management

---

## 5. Test Structure

### Test Directory Organization
```
/backend/tests/
├── unit/                  (Unit tests - mostly empty, ready for implementation)
├── integration/           (Integration tests - mostly empty, ready for implementation)
└── contract/             (Contract tests - mostly empty, ready for implementation)
```

### Current State
- **Test Framework:** JUnit 5 (Jupiter)
- **Test Support:** spring-boot-starter-test, spring-kafka-test
- **Status:** Test directories exist but are mostly empty (placeholder structure)

### Recommended Test Coverage Areas
1. Service layer tests (business logic)
2. Controller tests (API endpoint validation)
3. Repository tests (data access)
4. Integration tests (end-to-end flows)
5. Contract tests (API contracts)

---

## 6. User Story Mapping

### User Story 1: Intelligent Test Task Scheduling
**Key Components:**
- Models: TestTask, TestEnvironment, TestVersion, ResourcePool
- Services: TestTaskService, TestEnvironmentService, TestVersionService, ResourcePoolService, TestRecommendationService
- Controllers: TestTaskController, TestEnvironmentController, TestVersionController
- DB: PostgreSQL tables (test_tasks, test_environments, test_versions, resource_pools)
- Key Features:
  - Task creation with AI recommendations
  - Dynamic resource allocation
  - Environment and version management
  - Task status tracking (PENDING → RUNNING → COMPLETED/CANCELLED)

**API Endpoints:**
- POST `/api/v1/test-tasks` - Create task with recommendations
- GET `/api/v1/test-tasks/{id}` - Retrieve task
- GET `/api/v1/test-tasks?status=PENDING` - Filter by status
- POST `/api/v1/test-tasks/{id}/start` - Start task
- POST `/api/v1/test-tasks/{id}/cancel` - Cancel task

### User Story 2: AI Test Case Generation
**Key Components:**
- Models: TestCase, AIModel
- Services: AITestCaseGenerationService, AITestCaseOptimizationService, AIModelService, TestCaseService
- Controllers: TestCaseController
- DB: PostgreSQL (test_cases), MongoDB (ai_models)
- Key Features:
  - Generate test cases from natural language
  - AI model management with security audit
  - Scenario-based test case generation
  - Confidence scoring for generated cases
  - Support for multiple test types (FUNCTIONAL, PERFORMANCE, SECURITY)

**API Endpoints:**
- POST `/api/v1/test-cases/generate` - AI-generate test cases
- POST `/api/v1/test-cases` - Create test case
- GET `/api/v1/test-cases` - Retrieve test cases

### User Story 3: Monitoring & Reporting (Test Result Visualization & Analysis)
**Key Components:**
- Models: QualityReport, MonitoringData
- Services: QualityReportService, MonitoringService, ReportingService, QualityTraceabilityService
- Controllers: ReportController, MonitoringController
- DB: MongoDB collections
- Key Features:
  - Real-time test execution monitoring
  - Quality report generation with risk assessment
  - Defect statistics and performance metrics
  - Risk level calculation (LOW, MEDIUM, HIGH, CRITICAL)
  - Module-level risk identification
  - Recommendations based on quality metrics

**API Endpoints:**
- POST `/api/v1/reports/generate` - Generate quality report
- GET `/api/v1/reports/{id}` - Retrieve report
- GET `/api/v1/monitoring/{taskId}` - Get real-time monitoring data
- PUT `/api/v1/monitoring/{taskId}` - Update monitoring progress

---

## 7. Key File Locations Summary

### Models
```
/backend/src/main/java/com/synapsetest/testmanagement/model/
├── TestTask.java                    # User Story 1
├── TestEnvironment.java             # User Story 1
├── TestVersion.java                 # User Story 1
├── ResourcePool.java                # User Story 1
├── TestCase.java                    # User Story 2
├── AIModel.java                     # User Story 2
├── QualityReport.java               # User Story 3
└── MonitoringData.java              # User Story 3
```

### Services
```
/backend/src/main/java/com/synapsetest/testmanagement/service/
├── TestTaskService.java                    # US1
├── TestEnvironmentService.java             # US1
├── TestVersionService.java                 # US1
├── ResourcePoolService.java                # US1
├── TestRecommendationService.java          # US1
├── AITestCaseGenerationService.java        # US2
├── AITestCaseOptimizationService.java      # US2
├── AIModelService.java                     # US2
├── TestCaseService.java                    # US2
├── QualityReportService.java               # US3
├── MonitoringService.java                  # US3
├── ReportingService.java                   # US3
└── QualityTraceabilityService.java         # US3
```

### Controllers
```
/backend/src/main/java/com/synapsetest/testmanagement/controller/
├── TestTaskController.java          # US1
├── TestEnvironmentController.java   # US1
├── TestVersionController.java       # US1
├── TestCaseController.java          # US2
├── ReportController.java            # US3
├── MonitoringController.java        # US3
└── HealthController.java
```

### Repositories
```
/backend/src/main/java/com/synapsetest/testmanagement/repository/
├── TestTaskRepository.java          # US1 (PostgreSQL)
├── TestEnvironmentRepository.java   # US1 (PostgreSQL)
├── TestVersionRepository.java       # US1 (PostgreSQL)
├── ResourcePoolRepository.java      # US1 (PostgreSQL)
├── TestCaseRepository.java          # US2 (PostgreSQL)
├── AIModelRepository.java           # US2 (MongoDB)
├── QualityReportRepository.java     # US3 (MongoDB)
└── MonitoringDataRepository.java    # US3 (MongoDB)
```

### DTOs
```
/backend/src/main/java/com/synapsetest/testmanagement/dto/
├── TestTaskRequest.java
├── TestTaskResponse.java
├── TestCaseRequest.java
├── TestCaseResponse.java
├── AITestCaseGenerationRequest.java
└── ApiResponse.java
```

### Configuration & Resources
```
/backend/src/main/resources/
├── application.yml                  # Main Spring Boot configuration
├── application-config.yml           # Additional config
├── service-discovery.yml            # Service discovery configuration
└── schema.sql                       # PostgreSQL schema

/backend/src/main/java/com/synapsetest/testmanagement/config/
├── SecurityConfig.java
├── KafkaConfig.java
├── RabbitMQConfig.java
├── MonitoringConfig.java
├── GatewayConfig.java
└── WebConfig.java
```

---

## 8. Configuration Details

### Application Configuration (`application.yml`)
```yaml
server:
  port: 8080
  servlet:
    context-path: /api/v1

datasources:
  PostgreSQL: jdbc:postgresql://localhost:5432/test_management
  MongoDB: mongodb://localhost:27017/test_management_ai
  Redis: localhost:6379
  Kafka: localhost:9092
  RabbitMQ: localhost:5672

management:
  endpoints:
    - health, info, metrics, prometheus
  metrics:
    prometheus: enabled

resilience4j:
  circuitbreaker:
    - sliding-window-size: 10
    - failure-rate-threshold: 50%
    - wait-duration: 5s
```

---

## 9. Notable Architecture Decisions

1. **Hybrid Database Strategy**
   - PostgreSQL for structured, relational data (tasks, cases, environments)
   - MongoDB for flexible schema data (AI models, reports, monitoring)
   - Redis for caching and sessions

2. **Microservices-Ready**
   - Spring Cloud Gateway for API routing
   - Spring Cloud Config for distributed configuration
   - Kafka/RabbitMQ for async communication

3. **Resilience & Observability**
   - Resilience4j for circuit breaker pattern
   - Prometheus metrics collection
   - Structured logging with SLF4J

4. **Security**
   - Spring Security integration
   - Auth interceptor for request validation
   - AI model security audit tracking

5. **Code Quality**
   - Lombok for reducing boilerplate
   - Entity validation with JSR-303
   - Global exception handling
   - Transactional service methods

---

## 10. Summary Statistics

| Category | Count |
|----------|-------|
| **Models/Entities** | 8 core models |
| **Services** | 13 services |
| **Controllers** | 7 controllers |
| **Repositories** | 8 repositories |
| **DTOs** | 6 DTO classes |
| **Configuration Classes** | 6 config files |
| **PostgreSQL Tables** | 6 tables |
| **MongoDB Collections** | 3 collections |
| **API Endpoints** | 30+ RESTful endpoints |
| **Java Versions** | 11+ |

---

## 11. Deployment Artifacts

- **Docker:** Dockerfile present for containerization
- **Build Tool:** Maven with standard plugins
- **Docker Compose:** Available for multi-service orchestration
- **Database:** Automatic schema creation via Hibernate/JPA

---

## 12. Development Guidelines

### Adding New Features
1. Create model in `/model/` (extends BaseEntity for PostgreSQL entities)
2. Create repository in `/repository/` (extend JpaRepository or MongoRepository)
3. Create service in `/service/` with @Transactional methods
4. Create controller in `/controller/` with @RestController
5. Add DTOs in `/dto/` for request/response mapping
6. Add database schema changes in `schema.sql`

### Adding Tests
1. Unit tests go to `/tests/unit/`
2. Integration tests go to `/tests/integration/`
3. Contract tests go to `/tests/contract/`
4. Use @SpringBootTest for integration tests
5. Use @DataJpaTest for repository tests
6. Use @WebMvcTest for controller tests

