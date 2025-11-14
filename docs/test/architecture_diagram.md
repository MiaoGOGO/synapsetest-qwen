# Backend Architecture Diagram

## High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                      REST API Layer (Port 8080)                      │
│              Spring Boot 3.1.5 - Context Path: /api/v1               │
└────────────────────────────┬────────────────────────────────────────┘
                             │
         ┌───────────────────┼───────────────────┐
         │                   │                   │
    ┌────▼────┐      ┌──────▼──────┐      ┌─────▼─────┐
    │ US1: Task│      │ US2: AI Test │      │ US3: Report│
    │Scheduling│      │   Generation │      │ & Monitor  │
    └────┬────┘      └──────┬──────┘      └─────┬─────┘
         │                   │                   │
    ┌────┴───────────────────┴───────────────────┴────┐
    │         Service Layer (Business Logic)          │
    │  13 Services: Task, Case, Report, Monitoring   │
    └────┬───────────────────┬───────────────────┬───┘
         │                   │                   │
    ┌────▼────┐      ┌──────▼──────┐      ┌─────▼─────┐
    │ Repository│     │ Repository   │      │ Repository│
    │ (JPA)    │     │ (MongoDB)    │      │ (Kafka)   │
    └────┬────┘      └──────┬──────┘      └─────┬─────┘
         │                   │                   │
         │       ┌───────────┴───────────┐       │
         │       │                       │       │
    ┌────▼───────▼──────────────┬────────▼──┐   │
    │   PostgreSQL              │ MongoDB   │   │
    │   test_management DB      │ AI DB    │   │
    │   - test_tasks            │ - models │   │
    │   - test_cases            │ - reports│   │
    │   - environments          │ - monitor│   │
    │   - versions              │         │   │
    │   - resource_pools        │         │   │
    └─────────────────────────────────────────┘

         ┌─────────────────────────────────────┐
         │      Infrastructure Services        │
         │  ┌──────────┐  ┌──────────────┐   │
         │  │ Kafka    │  │ RabbitMQ     │   │
         │  │ Streams  │  │ (Messaging)  │   │
         │  └──────────┘  └──────────────┘   │
         │  ┌──────────────────────────────┐ │
         │  │  Redis Cache (localhost:6379) │ │
         │  └──────────────────────────────┘ │
         │  ┌────────────────────────────────┐│
         │  │ Spring Cloud Gateway + Config  ││
         │  └────────────────────────────────┘│
         └─────────────────────────────────────┘
```

## Component Interaction Flow

### User Story 1: Test Task Scheduling
```
Client Request
    │
    ▼
┌─────────────────────────────┐
│ TestTaskController          │
│ POST /test-tasks            │
└──────────┬──────────────────┘
           │
           ▼
┌─────────────────────────────┐
│ TestTaskService             │
│ - Create task               │
│ - Get AI recommendations    │
└──────────┬──────────────────┘
           │
      ┌────┴────┬──────────────────┐
      │          │                  │
      ▼          ▼                  ▼
  ┌────────┐  ┌──────────────┐  ┌──────────────┐
  │TestTask│  │ResourcePool  │  │TestRecommend│
  │Service │  │Service       │  │Service       │
  └───┬────┘  └──────┬───────┘  └──────┬───────┘
      │              │                  │
      └──────────────┼──────────────────┘
                     │
                     ▼
           ┌──────────────────────┐
           │ PostgreSQL Database  │
           │ (test_tasks,         │
           │  resource_pools)     │
           └──────────────────────┘
```

### User Story 2: AI Test Case Generation
```
Client Request (Natural Language)
    │
    ▼
┌──────────────────────────┐
│ TestCaseController       │
│ POST /test-cases/generate│
└──────────┬───────────────┘
           │
           ▼
┌──────────────────────────────┐
│ AITestCaseGenerationService  │
│ - Analyze scenarios           │
│ - Generate test steps         │
│ - Calculate confidence        │
└──────────┬───────────────────┘
           │
      ┌────┴────┬──────────────────┐
      │          │                  │
      ▼          ▼                  ▼
  ┌────────┐  ┌──────────────┐  ┌──────────────┐
  │TestCase│  │AIModel       │  │Optimization  │
  │Service │  │Service       │  │Service       │
  └───┬────┘  └──────┬───────┘  └──────┬───────┘
      │              │                  │
      └──────────────┼──────────────────┘
                     │
        ┌────────────┴────────────┐
        │                         │
        ▼                         ▼
    ┌────────────┐         ┌──────────────┐
    │PostgreSQL  │         │MongoDB       │
    │(test_cases)│         │(ai_models)   │
    └────────────┘         └──────────────┘
```

### User Story 3: Monitoring & Reporting
```
Test Execution in Progress
    │
    ▼
┌──────────────────────┐
│ MonitoringController │
│ GET /monitoring      │
└──────────┬───────────┘
           │
           ▼
┌──────────────────────┐
│ MonitoringService    │
│ - Track progress     │
│ - Update metrics     │
└──────────┬───────────┘
           │
           ▼
    ┌──────────────────┐
    │ MongoDB          │
    │ (monitoring_data)│
    └──────────────────┘

Task Completion
    │
    ▼
┌──────────────────────┐
│ ReportController     │
│ POST /reports        │
└──────────┬───────────┘
           │
           ▼
┌──────────────────────────┐
│ QualityReportService     │
│ - Calculate metrics      │
│ - Assess risk            │
│ - Generate report        │
└──────────┬───────────────┘
           │
      ┌────┴─────────────────────┐
      │                          │
      ▼                          ▼
  ┌──────────┐           ┌──────────────┐
  │Reporting │           │QualityReport │
  │Service   │           │Service       │
  └────┬─────┘           └──────┬───────┘
       │                        │
       └────────────┬───────────┘
                    │
                    ▼
            ┌──────────────────┐
            │ MongoDB          │
            │ (quality_reports)│
            └──────────────────┘
```

## Database Schema Relationships

```
PostgreSQL Schema:
┌─────────────────────────────────────────────────────┐
│                  test_tasks                          │
│  ├─ id (PK, UUID)                                   │
│  ├─ name                                            │
│  ├─ environment (FK to test_environments)           │
│  ├─ version (FK to test_versions)                   │
│  ├─ resource_pool (FK to resource_pools)            │
│  └─ status (PENDING/RUNNING/COMPLETED)              │
└────────────────┬────────────────────────────────────┘
                 │
        ┌────────┴────────┬─────────────────┬────────────────┐
        │                 │                 │                │
        ▼                 ▼                 ▼                ▼
    ┌────────┐    ┌─────────────┐  ┌──────────┐   ┌──────────────┐
    │test_   │    │test_        │  │resource_ │   │task_test_    │
    │cases   │    │environments │  │pools     │   │cases (assoc) │
    └────────┘    └─────────────┘  └──────────┘   └──────┬───────┘
                                                          │
                                                    ┌─────▼──────┐
                                                    │test_cases  │
                                                    └────────────┘

MongoDB Collections:
┌──────────────┐    ┌───────────────────┐    ┌──────────────────┐
│  ai_models   │    │quality_reports    │    │monitoring_data   │
├──────────────┤    ├───────────────────┤    ├──────────────────┤
│ id (PK)      │    │ id (PK)           │    │ id (PK)          │
│ name         │    │ taskId (FK)       │    │ taskId (FK)      │
│ version      │    │ testResults[]     │    │ progress         │
│ filePath     │    │ defectStats       │    │ executedCases    │
│ securityStat │    │ performanceMetric │    │ passedCases      │
│ compliance   │    │ riskAssessment    │    │ resourceUsage    │
└──────────────┘    └───────────────────┘    └──────────────────┘
```

## API Request/Response Flow

```
┌──────────────────────────────────────────────────────────────┐
│              REST API Request Flow                            │
└──────────────────────────────────────────────────────────────┘

1. Request arrives at Controller
   ├─ URL Mapping matched
   ├─ PathVariable/RequestBody extracted
   └─ @Valid annotation triggers validation

2. Controller delegates to Service
   ├─ Service method called
   ├─ Business logic executed
   └─ @Transactional ensures consistency

3. Service calls Repository
   ├─ Custom query methods or standard CRUD
   ├─ JPA/MongoDB handles database operations
   └─ Data returned to service

4. Service formats response
   ├─ Entity converted to DTO
   ├─ Recommendations/calculations added
   └─ Response prepared

5. Controller wraps in ApiResponse
   ├─ Success/error status
   ├─ Data payload
   └─ HTTP status code set

6. Response sent to client
   ├─ Content-Type: application/json
   └─ Body contains ApiResponse wrapper
```

## Service Layer Responsibilities

```
        ┌──────────────────────────────────────┐
        │       Service Layer Pattern           │
        └──────────────────────────────────────┘
                 │
    ┌────────────┼────────────┬────────────┐
    │            │            │            │
    ▼            ▼            ▼            ▼
┌────────┐  ┌────────┐  ┌─────────┐  ┌──────────┐
│Business│  │Data    │  │Error    │  │Cross-    │
│Logic   │  │Mapping │  │Handling │  │cutting   │
│        │  │        │  │         │  │Concerns  │
└────────┘  └────────┘  └─────────┘  └──────────┘
   │           │           │            │
   │           │           │            │
   ├─ Validat- ├─ Entity→ ├─ Try/Catch ├─ Logging
   │  ion       │   DTO    │  Global    │
   ├─ Rules    ├─ Data    │  Exception │
   │            │  Enrich- │  Handler   │
   └─ Algorithm└─ ment     └─ Custom    └─ Trans-
                             Exceptions    action
```

## Data Flow for User Story 3: Quality Report

```
Execution starts
    │
    ▼
┌──────────────────────────────────────┐
│ MonitoringService                     │
│ - createMonitoringData(taskId)       │
│ - Save initial state to MongoDB      │
└──────────────────────────────────────┘
    │
    ▼ (updates during execution)
┌──────────────────────────────────────┐
│ MonitoringService                     │
│ - updateMonitoringData(progress)     │
│ - Track: executed, passed, failed    │
└──────────────────────────────────────┘
    │
    ▼ (execution completes)
┌──────────────────────────────────────┐
│ QualityReportService                  │
│ - generateReport(testResults)        │
└──────────────────────────────────────┘
    │
    ├─ calculateDefectStats()
    ├─ calculatePerformanceMetrics()
    ├─ assessRisk()
    │  ├─ calculateRiskScore()
    │  ├─ identifyHighRiskModules()
    │  └─ generateRecommendations()
    └─ generateSummary()
    │
    ▼
┌──────────────────────────────────────┐
│ MongoDB: quality_reports             │
│ - Store complete report              │
│ - Include all calculations           │
│ - Ready for visualization            │
└──────────────────────────────────────┘
```

## Configuration & Infrastructure

```
┌────────────────────────────────────────────────────────┐
│          Infrastructure Configuration                  │
└────────────────────────────────────────────────────────┘

Application Properties
├─ server.port: 8080
├─ server.servlet.context-path: /api/v1
└─ Datasources
   ├─ PostgreSQL (JDBC)
   ├─ MongoDB (URI)
   ├─ Redis (Standalone)
   ├─ Kafka (Bootstrap)
   └─ RabbitMQ (Connection)

Spring Boot Modules
├─ spring-boot-starter-web
├─ spring-boot-starter-data-jpa
├─ spring-boot-starter-data-mongodb
├─ spring-boot-starter-data-redis
├─ spring-cloud-starter-gateway
├─ spring-kafka
├─ spring-boot-starter-amqp
└─ spring-boot-starter-actuator

Supporting Libraries
├─ Lombok (Code generation)
├─ Resilience4j (Circuit breaker)
├─ Micrometer (Metrics)
└─ JUnit 5 (Testing)
```

