# Backend Quick Reference Guide

## Framework Information
- **Framework:** Spring Boot 3.1.5
- **Language:** Java 11
- **Build Tool:** Maven
- **Project Root:** `/Users/mengwei/ww/github/synapsetest-qwen/backend`

## Database Connections
```
PostgreSQL:  jdbc:postgresql://localhost:5432/test_management
MongoDB:     mongodb://localhost:27017/test_management_ai
Redis:       localhost:6379
Kafka:       localhost:9092
RabbitMQ:    localhost:5672
```

## Core Entity Models

### User Story 1: Test Task Scheduling
- **TestTask** - Main test task entity
- **TestEnvironment** - Environment configuration
- **TestVersion** - Version configuration
- **ResourcePool** - Test resource management

### User Story 2: AI Test Case Generation
- **TestCase** - Test case definition
- **AIModel** - AI model metadata with security audit

### User Story 3: Monitoring & Reporting
- **QualityReport** - Test result analysis and risk assessment
- **MonitoringData** - Real-time test execution metrics

## Service Layer (13 Total)

### US1 Services
1. TestTaskService - Task CRUD and lifecycle
2. TestEnvironmentService - Environment management
3. TestVersionService - Version management
4. ResourcePoolService - Resource allocation/release
5. TestRecommendationService - AI recommendations

### US2 Services
6. AITestCaseGenerationService - Generate from text
7. AITestCaseOptimizationService - Optimize cases
8. AIModelService - Model lifecycle
9. TestCaseService - Case CRUD

### US3 Services
10. QualityReportService - Report generation & risk assessment
11. MonitoringService - Real-time monitoring
12. ReportingService - Report generation
13. QualityTraceabilityService - Quality tracking

## API Endpoints Structure
- Base URL: `http://localhost:8080/api/v1`
- Controllers:
  - /test-tasks (TestTaskController)
  - /test-cases (TestCaseController)
  - /test-environments (TestEnvironmentController)
  - /test-versions (TestVersionController)
  - /reports (ReportController)
  - /monitoring (MonitoringController)

## Database Design

### PostgreSQL Tables (Relational)
- test_tasks (Primary: test task records)
- test_cases (Primary: test case definitions)
- test_environments (Primary: environment configs)
- test_versions (Primary: version configs)
- resource_pools (Primary: resource management)
- task_test_cases (Association: task-case mapping)

### MongoDB Collections (Flexible Schema)
- ai_models (AI model records)
- quality_reports (Quality analysis reports)
- monitoring_data (Real-time metrics)

## File Structure Reference

```
src/main/java/com/synapsetest/testmanagement/
├── config/              # Spring config beans
├── controller/          # REST endpoints (7 files)
├── service/             # Business logic (13 files)
├── model/               # Entities (8 files)
├── repository/          # Data access (8 files)
├── dto/                 # Transfer objects (6 files)
├── entity/              # Base entity
├── exception/           # Custom exceptions
└── interceptor/         # Request interceptors

resources/
├── application.yml      # Main config
├── schema.sql           # PostgreSQL schema
└── application-config.yml
```

## Key Design Patterns Used

1. **Repository Pattern** - Data access abstraction
2. **Service Layer Pattern** - Business logic separation
3. **DTO Pattern** - Request/response mapping
4. **Exception Handling** - Global exception handler
5. **Dependency Injection** - Spring IoC
6. **Transactional Management** - @Transactional on services
7. **Entity Inheritance** - BaseEntity with audit fields

## Important Features

### User Story 1: Intelligent Scheduling
- Task lifecycle: PENDING → RUNNING → COMPLETED/CANCELLED
- Priority levels: 0-10 scale
- Resource allocation with capacity checks
- Environment and version management

### User Story 2: AI Test Generation
- Natural language input processing
- Scenario extraction
- Test step generation
- Confidence scoring
- Support for FUNCTIONAL, PERFORMANCE, SECURITY types
- Security audit for AI models

### User Story 3: Monitoring & Reporting
- Real-time progress tracking
- Quality metrics calculation
- Pass rate computation
- Risk assessment (LOW, MEDIUM, HIGH, CRITICAL)
- Module-level risk identification
- Defect classification (critical, major, minor)
- Performance metrics (response time, throughput)

## Important Annotations Used

```java
@Entity              // JPA entity
@Document            // MongoDB document
@Repository          // Data access
@Service             // Business logic
@RestController      // REST endpoint
@RequestMapping      // URL mapping
@Transactional       // Transaction management
@CreatedDate         // Auto timestamp
@LastModifiedDate    // Auto timestamp
@Data                // Lombok getter/setter
@Slf4j              // Logging
```

## Test Structure (Empty but Ready)
- `/tests/unit/` - Unit tests
- `/tests/integration/` - Integration tests
- `/tests/contract/` - Contract tests

## Configuration Highlights

```yaml
# Server Config
server.port: 8080
server.servlet.context-path: /api/v1

# Database Config
spring.jpa.hibernate.ddl-auto: update
spring.data.mongodb.uri: mongodb://localhost:27017/test_management_ai

# Monitoring
management.endpoints.web.exposure.include: health,info,metrics,prometheus

# Resilience
resilience4j.circuitbreaker.failure-rate-threshold: 50%
resilience4j.circuitbreaker.sliding-window-size: 10
```

## Common Task Queries

### Repository Methods
```
TestTaskRepository:
  - findByStatus(status)
  - findByEnvironment(environment)
  - findByCreatedBy(username)
  - findByStatusOrderByPriorityDesc(status)

QualityReportRepository:
  - findByTaskId(taskId)
  - findByStatus(status)
  - findByGeneratedAtBetween(start, end)
  - findTop10ByOrderByGeneratedAtDesc()

ResourcePoolRepository:
  - findByStatus(status)
  - findByType(type)
  - findByStatusAndType(status, type)
```

## Development Checklist

When adding new features:
1. [ ] Create model in `/model/`
2. [ ] Create repository in `/repository/`
3. [ ] Create service in `/service/`
4. [ ] Create controller in `/controller/`
5. [ ] Create DTOs in `/dto/` if needed
6. [ ] Update `schema.sql` for PostgreSQL changes
7. [ ] Add tests to `/tests/`
8. [ ] Update `application.yml` if needed

## Important Constants

### Task Status
- PENDING
- RUNNING
- COMPLETED
- CANCELLED

### Test Types
- FUNCTIONAL
- PERFORMANCE
- SECURITY

### Environment Status
- AVAILABLE
- MAINTENANCE
- UNAVAILABLE

### Risk Levels
- LOW
- MEDIUM
- HIGH
- CRITICAL

### Resource Pool Types
- VM
- CONTAINER
- DEVICE

