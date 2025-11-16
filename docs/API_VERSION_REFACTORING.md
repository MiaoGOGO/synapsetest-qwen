# API 版本管理重构文档

## 📋 变更概述

将 API 版本管理从 `application.yml` 的 `context-path` 迁移到 Controller 层的 `@RequestMapping`，实现更灵活的 RESTful API 版本管理。

## 🎯 变更动机

### 之前的方式（不推荐）
```yaml
# application.yml
server:
  servlet:
    context-path: /api/v1
```
```java
@RestController
@RequestMapping("/test-cases")  // 实际路径: /api/v1/test-cases
```

**问题：**
- 所有端点强制带版本前缀
- 无法灵活支持多版本 API
- 健康检查等系统端点也被强制加上版本号

### 当前的方式（推荐）✅
```yaml
# application.yml
server:
  port: 8081
  # No context-path - API versioning in Controllers
```
```java
@RestController
@RequestMapping(ApiVersion.V1 + "/test-cases")  // 清晰的完整路径
```

**优势：**
- ✅ 灵活支持多版本 API 共存
- ✅ 系统端点可以不带版本号
- ✅ 符合 RESTful API 最佳实践
- ✅ API 路径在 Controller 中一目了然

## 📝 具体变更

### 1. 创建 API 版本常量类

**文件：** `backend/src/main/java/com/synapsetest/testmanagement/constants/ApiVersion.java`

```java
public final class ApiVersion {
    public static final String V1 = "/api/v1";
    public static final String V2 = "/api/v2";  // 预留 v2
}
```

### 2. 修改 application.yml

```yaml
# 移除 context-path 配置
server:
  port: 8081
  # No context-path - API versioning is handled in Controllers
```

### 3. 更新所有 Controller

#### 业务 API（带版本号）
```java
// Before
@RequestMapping("/test-cases")

// After
import com.synapsetest.testmanagement.constants.ApiVersion;

@RequestMapping(ApiVersion.V1 + "/test-cases")
```

**已更新的 Controller：**
- ✅ TestCaseController → `/api/v1/test-cases`
- ✅ TestTaskController → `/api/v1/test-tasks`
- ✅ TestEnvironmentController → `/api/v1/test-environments`
- ✅ TestVersionController → `/api/v1/test-versions`
- ✅ MonitoringController → `/api/v1/monitoring` (需要 mongodb profile)
- ✅ ReportController → `/api/v1/reports` (需要 mongodb profile)

#### 系统端点（不带版本号）
```java
// HealthController - 保持不变
@RequestMapping("")
public class HealthController {
    @GetMapping("/health")  // 访问路径: /health
}
```

### 4. 更新 SecurityConfig

```java
.authorizeRequests()
    // 系统端点（无版本）
    .antMatchers("/health", "/actuator/**").permitAll()
    // 认证端点
    .antMatchers("/auth/**").permitAll()
    // API v1 端点 - 开发/测试环境允许访问
    .antMatchers("/api/v1/**").permitAll()
    // 其他请求需要认证
    .anyRequest().authenticated();
```

## 🔍 API 访问示例

### 系统端点
```bash
# 健康检查（无版本号）
curl http://localhost:8081/health

# 响应：
{
  "success": true,
  "data": {
    "status": "UP",
    "service": "test-management-backend"
  }
}
```

### 业务 API v1
```bash
# 获取所有测试用例
curl http://localhost:8081/api/v1/test-cases

# 获取所有测试任务
curl http://localhost:8081/api/v1/test-tasks

# 获取所有测试环境
curl http://localhost:8081/api/v1/test-environments
```

### 未来支持 v2（预留）
```bash
# 可以同时支持 v1 和 v2
curl http://localhost:8081/api/v1/test-cases  # v1 版本
curl http://localhost:8081/api/v2/test-cases  # v2 版本（未来）
```

## 📊 目录结构

```
backend/src/main/java/com/synapsetest/testmanagement/
├── constants/
│   └── ApiVersion.java          ← 新增：API 版本常量
├── controller/
│   ├── HealthController.java    ← 修改：系统端点（无版本）
│   ├── TestCaseController.java  ← 修改：使用 ApiVersion.V1
│   ├── TestTaskController.java  ← 修改：使用 ApiVersion.V1
│   ├── TestEnvironmentController.java  ← 修改
│   ├── TestVersionController.java      ← 修改
│   ├── MonitoringController.java       ← 修改
│   └── ReportController.java           ← 修改
└── config/
    └── SecurityConfig.java      ← 修改：更新路径匹配规则
```

## ✅ 验证结果

所有端点测试通过：

| 端点 | 路径 | 状态 |
|------|------|------|
| Health | `/health` | ✅ 200 OK |
| Test Cases | `/api/v1/test-cases` | ✅ 200 OK |
| Test Tasks | `/api/v1/test-tasks` | ✅ 200 OK |
| Test Environments | `/api/v1/test-environments` | ✅ 200 OK |
| Test Versions | `/api/v1/test-versions` | ✅ 200 OK |

## 🚀 未来扩展

### 支持 API v2
```java
// 创建 v2 版本的 Controller
@RestController
@RequestMapping(ApiVersion.V2 + "/test-cases")
public class TestCaseControllerV2 {
    // v2 版本的新功能
}

// v1 和 v2 可以同时存在
```

### 版本废弃管理
```java
@Deprecated
@RestController
@RequestMapping(ApiVersion.V1 + "/test-cases")
public class TestCaseController {
    // 标记为废弃，但仍可用于向后兼容
}
```

## 📚 最佳实践

1. **版本化策略**
   - 主要版本变更：URL 路径版本化（/api/v1, /api/v2）
   - 次要版本变更：通过 HTTP Header 控制

2. **向后兼容**
   - 保持旧版本 API 至少 2-3 个版本周期
   - 提供清晰的废弃通知和迁移指南

3. **文档管理**
   - 使用 Swagger/OpenAPI 为每个版本生成独立文档
   - 在响应头中包含 API 版本信息

## 🔗 相关链接

- [RESTful API Versioning Best Practices](https://restfulapi.net/versioning/)
- [Spring Boot REST API Versioning](https://www.baeldung.com/rest-versioning)

---

**变更日期：** 2025-11-16  
**变更人：** AI Assistant  
**审核状态：** ✅ 已验证

