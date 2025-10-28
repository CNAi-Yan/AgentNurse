# 环境变量配置指南

本文档说明如何配置 AgentNurse 项目的环境变量。

## 快速开始

### 1. 创建 .env 文件

```bash
# 复制示例文件
cp .env.example .env

# 编辑 .env 文件，填入实际的密码和密钥
nano .env  # 或使用你喜欢的编辑器
```

### 2. 配置说明

`.env` 文件包含以下敏感配置：

#### MySQL 配置

```bash
# MySQL root 用户密码（强密码）
MYSQL_ROOT_PASSWORD=your_secure_root_password

# 数据库名称
MYSQL_DATABASE=agent_nurse

# MySQL 普通用户（可选，通常不使用）
MYSQL_USER=agent_nurse
MYSQL_PASSWORD=your_secure_user_password
```

#### 应用数据库配置

```bash
# 数据库主机（本地开发使用 localhost，Docker 内使用 mysql）
DB_HOST=localhost

# 数据库端口
DB_PORT=3306

# 数据库名称
DB_NAME=agent_nurse

# 应用连接使用的用户名（通常使用 root）
DB_USERNAME=root

# 应用连接使用的密码（应与 MYSQL_ROOT_PASSWORD 一致）
DB_PASSWORD=your_secure_root_password
```

**重要**: `DB_PASSWORD` 应该与 `MYSQL_ROOT_PASSWORD` 保持一致，因为应用使用 root 用户连接数据库。

#### JWT 配置

```bash
# JWT 密钥（至少 32 个字符的强随机字符串）
JWT_SECRET=your_jwt_secret_key_min_32_chars
```

生成强随机密钥：

```bash
# 使用 openssl 生成 32 字节的随机密钥
openssl rand -base64 32

# 或使用 Python
python3 -c "import secrets; print(secrets.token_urlsafe(32))"
```

#### 应用配置

```bash
# 应用端口
SERVER_PORT=8080

# Spring Profile（dev/prod）
SPRING_PROFILE=dev
```

## 环境变量使用场景

### 场景 1: 本地开发（不使用 Docker）

1. 手动启动 MySQL 或使用 Docker 启动 MySQL：
   ```bash
   docker-compose up -d mysql
   ```

2. 配置 `.env`：
   ```bash
   DB_HOST=localhost
   DB_PORT=3306
   DB_USERNAME=root
   DB_PASSWORD=你的root密码
   JWT_SECRET=你的JWT密钥
   ```

3. 运行应用：
   ```bash
   # 加载 .env 文件（需要 dotenv 工具）
   export $(cat .env | xargs)
   
   # 或手动导出环境变量
   source .env
   
   # 运行应用
   mvn spring-boot:run
   ```

### 场景 2: 使用 Docker Compose

Docker Compose 会自动读取 `.env` 文件：

```bash
# 启动所有服务
docker-compose up -d

# 启动仅 MySQL
docker-compose up -d mysql
```

### 场景 3: 生产环境部署

在生产环境中，**不要使用 .env 文件**，而是通过系统环境变量设置：

```bash
# 方式 1: 直接设置环境变量
export DB_HOST=your-db-host
export DB_PORT=3306
export DB_NAME=agent_nurse
export DB_USERNAME=root
export DB_PASSWORD=your_secure_password
export JWT_SECRET=your_secure_jwt_secret

# 运行应用
java -jar agent-nurse.jar --spring.profiles.active=prod
```

```bash
# 方式 2: 通过启动参数传递
java -jar agent-nurse.jar \
  --spring.profiles.active=prod \
  --spring.datasource.password=${DB_PASSWORD} \
  --jwt.secret=${JWT_SECRET}
```

## 安全最佳实践

### 1. 密码强度要求

- **MySQL 密码**: 至少 16 字符，包含大小写字母、数字、特殊字符
- **JWT 密钥**: 至少 32 字符的强随机字符串

### 2. 不要提交 .env 文件

`.env` 文件已添加到 `.gitignore`，确保不会被提交到版本控制。

检查：
```bash
git status
# .env 不应出现在待提交文件列表中
```

### 3. 定期更换密钥

在生产环境中，定期更换以下密钥：
- JWT 密钥（会使现有 token 失效）
- 数据库密码

### 4. 使用密钥管理服务

在生产环境中，考虑使用：
- **AWS Secrets Manager**
- **HashiCorp Vault**
- **Azure Key Vault**
- **Google Secret Manager**

### 5. 限制文件权限

```bash
# 限制 .env 文件权限为仅所有者可读写
chmod 600 .env

# 验证权限
ls -la .env
# 应显示：-rw------- 1 user group ... .env
```

## 环境变量验证

### 检查环境变量是否加载

```bash
# 方式 1: 使用 printenv
printenv | grep -E "DB_|JWT_|MYSQL_"

# 方式 2: 使用 echo
echo $DB_PASSWORD
echo $JWT_SECRET
```

### Spring Boot 启动日志

应用启动时会显示使用的配置（密码会被隐藏）：

```
Using datasource url: jdbc:mysql://localhost:3306/agent_nurse
Using datasource username: root
```

## 常见问题

### Q: 为什么 DB_PASSWORD 和 MYSQL_ROOT_PASSWORD 要一致？

A: 因为应用使用 `root` 用户连接数据库。如果你想使用普通用户，需要：
1. 在 MySQL 中创建该用户并授权
2. 设置 `DB_USERNAME` 和 `DB_PASSWORD` 为该用户的凭证

### Q: JWT_SECRET 可以使用默认值吗？

A: **绝对不行！** 在生产环境中必须使用强随机密钥。默认值只用于开发环境。

### Q: 如何在 Docker 内的应用访问 MySQL？

A: 在 Docker Compose 中，服务之间通过服务名访问：
```bash
DB_HOST=mysql  # 不是 localhost
```

### Q: 应用启动失败，提示数据库连接错误？

A: 检查：
1. `.env` 文件是否存在
2. 环境变量是否正确加载：`echo $DB_PASSWORD`
3. MySQL 是否正在运行：`docker-compose ps`
4. 密码是否正确：`mysql -u root -p`

## 不同环境的配置示例

### 开发环境 (.env.dev)

```bash
DB_HOST=localhost
DB_PORT=3306
DB_NAME=agent_nurse_dev
DB_USERNAME=root
DB_PASSWORD=dev_password_123
JWT_SECRET=dev_jwt_secret_32_characters_long
SERVER_PORT=8080
SPRING_PROFILE=dev
```

### 生产环境 (.env.prod)

```bash
DB_HOST=production-db-host.example.com
DB_PORT=3306
DB_NAME=agent_nurse
DB_USERNAME=root
DB_PASSWORD=very_secure_production_password_2024!
JWT_SECRET=production_jwt_secret_key_very_long_and_secure_random_string
SERVER_PORT=8080
SPRING_PROFILE=prod
```

## 相关文件

- `.env` - 实际配置文件（不提交）
- `.env.example` - 配置模板（提交到仓库）
- `application.yml` - 主配置文件，引用环境变量
- `application-dev.yml` - 开发环境配置
- `application-prod.yml` - 生产环境配置
- `docker-compose.yml` - Docker 配置，读取 .env

## 参考资源

- [Spring Boot External Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [Docker Compose Environment Variables](https://docs.docker.com/compose/environment-variables/)
- [12-Factor App: Config](https://12factor.net/config)
