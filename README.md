# AgentNurse 照护助理

> "非诊断/非医疗建议"的智能照护助理系统

AgentNurse 是一个面向慢病/术后患者的照护助理系统，提供记录、提醒、信息整理与人工复核的照护服务。

## 功能特性

### ✅ AGE-1: 账号与角色基础（已完成）

- **三类角色支持**：患者 (PATIENT)、家属 (FAMILY)、护理员 (CAREGIVER)
- **用户注册**：支持邮箱注册，密码强度验证，BCrypt 加密
- **用户登录**：支持用户名或邮箱登录，JWT 令牌认证
- **资料管理**：完整的用户信息查询和更新功能
- **OAuth 扩展**：架构设计支持未来扩展 OAuth 第三方登录

## 技术栈

- **后端框架**：Spring Boot 3.2.0
- **数据库**：MySQL 8.0
- **安全认证**：Spring Security + JWT (jjwt 0.12.3)
- **ORM**：Spring Data JPA + Hibernate
- **构建工具**：Maven
- **Java 版本**：17

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 2. 配置环境变量

创建 `.env` 文件（基于模板）：

```bash
# 复制环境变量模板
cp .env.example .env

# 编辑 .env 文件，设置你的密码和密钥
nano .env
```

**重要配置项**：

```bash
# MySQL 配置
MYSQL_ROOT_PASSWORD=your_secure_root_password
DB_PASSWORD=your_secure_root_password  # 应与 MYSQL_ROOT_PASSWORD 一致

# JWT 密钥（至少32字符）
JWT_SECRET=your_jwt_secret_key_min_32_chars
```

详细配置说明请参考 [ENV_SETUP.md](ENV_SETUP.md)。

### 3. 启动数据库

使用 Docker Compose 快速启动 MySQL：

```bash
docker-compose up -d mysql
```

或手动创建数据库：

```sql
CREATE DATABASE agent_nurse CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 4. 运行应用

```bash
# 编译
mvn clean package

# 运行
java -jar target/agent-nurse-0.0.1-SNAPSHOT.jar

# 或使用 Maven 直接运行
mvn spring-boot:run
```

应用将在 `http://localhost:8080/api` 启动。

## API 文档

### 认证接口

#### 1. 用户注册

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "patient001",
  "email": "patient@example.com",
  "password": "Pass@123",
  "role": "PATIENT",
  "realName": "张三",
  "phone": "13800138000"
}
```

**响应示例**：

```json
{
  "success": true,
  "message": "注册成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "user": {
      "id": 1,
      "username": "patient001",
      "email": "patient@example.com",
      "role": "PATIENT",
      "realName": "张三",
      "enabled": true,
      "emailVerified": false
    }
  },
  "timestamp": "2024-10-28T12:00:00"
}
```

#### 2. 用户登录

```http
POST /api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "patient001",
  "password": "Pass@123"
}
```

#### 3. 健康检查

```http
GET /api/auth/health
```

### 用户接口（需要认证）

所有以下接口需要在请求头中携带 JWT 令牌：

```http
Authorization: Bearer <your_jwt_token>
```

#### 1. 获取当前用户信息

```http
GET /api/users/me
```

#### 2. 更新用户资料

```http
PUT /api/users/me
Content-Type: application/json

{
  "realName": "张三",
  "phone": "13800138000",
  "gender": "M",
  "birthDate": "1990-01-01",
  "address": "北京市朝阳区"
}
```

#### 3. 删除账户

```http
DELETE /api/users/me
```

详细的 API 示例请参考 [API_EXAMPLES.md](API_EXAMPLES.md)。

## 用户角色说明

| 角色 | 角色代码 | 说明 |
|------|---------|------|
| 患者 | `PATIENT` | 接受照护的主体用户 |
| 家属 | `FAMILY` | 患者的家庭成员，可协助管理 |
| 护理员 | `CAREGIVER` | 专业护理人员 |
| 管理员 | `ADMIN` | 系统管理员 |

## 密码要求

- 长度：8-20 个字符
- 必须包含：大写字母、小写字母、数字、特殊字符 (@$!%*?&)
- 示例：`Pass@123`

## 安全特性

- ✅ BCrypt 密码加密（强度 10）
- ✅ JWT 令牌认证（24小时有效期）
- ✅ 用户名和邮箱唯一性验证
- ✅ 密码强度验证
- ✅ CORS 跨域支持
- ✅ 全局异常处理

## 项目结构

```
agent-nurse/
├── src/
│   ├── main/
│   │   ├── java/com/agentnurse/
│   │   │   ├── controller/        # 控制器层
│   │   │   ├── service/           # 业务逻辑层
│   │   │   ├── repository/        # 数据访问层
│   │   │   ├── model/
│   │   │   │   ├── entity/        # 实体类
│   │   │   │   ├── dto/           # 数据传输对象
│   │   │   │   └── enums/         # 枚举类
│   │   │   ├── security/          # 安全配置
│   │   │   ├── exception/         # 异常处理
│   │   │   └── AgentNurseApplication.java
│   │   └── resources/
│   │       ├── application.yml    # 主配置文件
│   │       └── db/schema.sql      # 数据库初始化脚本
│   └── test/                      # 测试代码
├── pom.xml                        # Maven 配置
├── docker-compose.yml             # Docker 配置
└── README.md                      # 项目文档
```

## 开发路线图

### M0 基线（已完成）
- ✅ 账号与角色管理
- ⏳ 数据模型设计
- ⏳ 记录表单功能
- ⏳ 提醒基础功能
- ⏳ 非诊断拦截器

### M1 助理对话（规划中）
- ⏳ AI 对话助理
- ⏳ 建议与待办生成
- ⏳ 升级规则与人工复核

### M2 内容与个性化（规划中）
- ⏳ 内容库管理
- ⏳ 用户画像与推荐
- ⏳ 数据导出与删除
- ⏳ 审计日志完善

## 测试

```bash
# 运行所有测试
mvn test

# 运行特定测试
mvn test -Dtest=AuthServiceTest
```

## 部署

### Docker 部署

```bash
# 构建镜像
docker build -t agent-nurse:latest .

# 运行容器（包含数据库）
docker-compose up -d
```

### 生产环境配置

1. 修改 `application-prod.yml`
2. 设置环境变量：

```bash
export DB_URL=jdbc:mysql://your-db-host:3306/agent_nurse
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export JWT_SECRET=your_jwt_secret_key
```

3. 运行应用：

```bash
java -jar agent-nurse.jar --spring.profiles.active=prod
```

## 许可证

Apache License 2.0

## 贡献

欢迎提交 Issue 和 Pull Request！

## 联系方式

- GitHub: [AgentNurse](https://github.com/CNAi-Yan/AgentNurse)
- Linear 项目: AI照护助理 MVP

---

**重要提示**：本系统不提供诊断、处方或紧急医疗建议。如有紧急情况，请立即联系医疗机构。
