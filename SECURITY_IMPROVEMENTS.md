# 安全改进总结

## 问题描述

在之前的配置中存在以下安全问题：

1. **硬编码密码**: `docker-compose.yml` 中直接写入了 MySQL root 密码 `root`
2. **配置冲突**: `docker-compose.yml` 创建了 `agent_nurse` 用户，但 `application.yml` 使用 `root` 用户
3. **默认密码**: `application.yml` 中有默认密码 `root`
4. **JWT 密钥暴露**: JWT secret 直接写在配置文件中

## 解决方案

### ✅ 1. 创建环境变量配置文件

**文件**: `.env` (不提交), `.env.example` (提交)

```bash
# .env.example - 配置模板
MYSQL_ROOT_PASSWORD=your_secure_root_password
MYSQL_DATABASE=agent_nurse
MYSQL_USER=agent_nurse
MYSQL_PASSWORD=your_secure_user_password

DB_HOST=localhost
DB_PORT=3306
DB_NAME=agent_nurse
DB_USERNAME=root
DB_PASSWORD=your_secure_root_password

JWT_SECRET=your_jwt_secret_key_min_32_chars
```

### ✅ 2. 更新 docker-compose.yml

**变更前**:
```yaml
environment:
  MYSQL_ROOT_PASSWORD: root                    # ❌ 硬编码
  MYSQL_DATABASE: agent_nurse
  MYSQL_USER: agent_nurse
  MYSQL_PASSWORD: agent_nurse_pass             # ❌ 硬编码
```

**变更后**:
```yaml
env_file:
  - .env                                        # ✅ 从 .env 文件加载
environment:
  MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}  # ✅ 使用环境变量
  MYSQL_DATABASE: ${MYSQL_DATABASE:-agent_nurse}
  MYSQL_USER: ${MYSQL_USER}
  MYSQL_PASSWORD: ${MYSQL_PASSWORD}
```

### ✅ 3. 更新 application.yml

**变更前**:
```yaml
datasource:
  url: jdbc:mysql://localhost:3306/agent_nurse
  username: root                               # ❌ 硬编码
  password: ${DB_PASSWORD:root}                # ❌ 默认值暴露

jwt:
  secret: ${JWT_SECRET:AgentNurseSecretKeyForJWTTokenGeneration2024}  # ❌ 默认值暴露 # 生产环境必须设置此环境变量
```

**变更后**:
```yaml
datasource:
  url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:agent_nurse}
  username: ${DB_USERNAME:root}                # ✅ 可配置
  password: ${DB_PASSWORD}                     # ✅ 必须提供，无默认值

jwt:
  secret: ${JWT_SECRET}                        # ✅ 必须提供，无默认值
```

### ✅ 4. 更新 .gitignore

**添加规则**:
```gitignore
# Environment variables (sensitive data)
.env
*.env
!.env.example                                  # ✅ 允许提交模板
```

### ✅ 5. 统一数据库凭证

**关键点**:
- `DB_USERNAME` 设为 `root`
- `DB_PASSWORD` 与 `MYSQL_ROOT_PASSWORD` 保持一致
- 应用使用 root 用户连接数据库

**配置示例**:
```bash
MYSQL_ROOT_PASSWORD=MySecurePassword123!
DB_USERNAME=root
DB_PASSWORD=MySecurePassword123!              # ✅ 与 root 密码一致
```

## 改进效果

### 安全性提升

| 改进项 | 变更前 | 变更后 |
|--------|--------|--------|
| 密码存储 | ❌ 明文写在配置文件 | ✅ 环境变量，不提交到仓库 |
| JWT 密钥 | ❌ 硬编码，所有人可见 | ✅ 环境变量，独立配置 |
| 配置冲突 | ❌ docker-compose 和 app 配置不一致 | ✅ 统一使用环境变量 |
| 默认值 | ❌ 有不安全的默认密码 | ✅ 强制提供，无默认值 |
| 版本控制 | ❌ 敏感信息可能被提交 | ✅ .env 已忽略 |

### 可维护性提升

1. **多环境支持**: 开发、测试、生产环境使用不同的 .env 文件
2. **配置集中**: 所有敏感配置集中在 .env 文件
3. **易于部署**: 只需修改 .env 文件，无需改动代码
4. **清晰文档**: ENV_SETUP.md 提供详细配置指南

## 使用步骤

### 本地开发

```bash
# 1. 复制配置模板
cp .env.example .env

# 2. 编辑 .env，设置你的密码
nano .env

# 3. 启动服务
docker-compose up -d mysql

# 4. 运行应用（确保环境变量已加载）
export $(cat .env | xargs)
mvn spring-boot:run
```

### 生产部署

```bash
# 1. 在服务器上创建 .env 文件
cat > .env << 'EOF'
MYSQL_ROOT_PASSWORD=ProductionSecurePassword!
DB_PASSWORD=ProductionSecurePassword!
JWT_SECRET=ProductionJWTSecretVeryLongAndSecure
EOF

# 2. 设置文件权限
chmod 600 .env

# 3. 启动服务
docker-compose up -d

# 或使用系统环境变量
export MYSQL_ROOT_PASSWORD=ProductionSecurePassword!
export DB_PASSWORD=ProductionSecurePassword!
export JWT_SECRET=ProductionJWTSecretVeryLongAndSecure
java -jar agent-nurse.jar --spring.profiles.active=prod
```

## 安全检查清单

在部署前，请确认：

- [ ] `.env` 文件已创建并配置正确的密码
- [ ] `.env` 文件未被提交到版本控制（运行 `git status` 检查）
- [ ] `.env` 文件权限设置为 600（`chmod 600 .env`）
- [ ] JWT_SECRET 长度至少 32 字符
- [ ] 数据库密码强度足够（16+ 字符，包含大小写字母、数字、特殊字符）
- [ ] `DB_PASSWORD` 与 `MYSQL_ROOT_PASSWORD` 一致
- [ ] 生产环境使用不同的密码（不使用示例密码）

## 验证

### 检查 .env 是否被忽略

```bash
git status
# 不应看到 .env 文件

git ls-files | grep .env
# 应该只显示: .env.example
```

### 检查环境变量是否加载

```bash
# 启动 docker-compose
docker-compose up -d mysql

# 检查容器环境变量
docker exec agent-nurse-mysql env | grep MYSQL

# 应显示你在 .env 中设置的值，而不是 "root"
```

### 检查应用连接

```bash
# 启动应用
mvn spring-boot:run

# 查看日志，应显示:
# Using datasource url: jdbc:mysql://localhost:3306/agent_nurse
# 不应有连接错误
```

## 迁移指南

如果你已经部署了旧版本，请按以下步骤迁移：

### 步骤 1: 备份当前配置

```bash
# 记录当前的数据库密码
echo "Current MySQL password: XXX" > migration-notes.txt
```

### 步骤 2: 拉取新代码

```bash
git pull origin cursor/AGE-1-implement-account-and-role-management-1c2e
```

### 步骤 3: 创建 .env 文件

```bash
cp .env.example .env
# 编辑 .env，使用你之前的密码
```

### 步骤 4: 重启服务

```bash
docker-compose down
docker-compose up -d
```

### 步骤 5: 验证

```bash
# 测试应用启动
mvn spring-boot:run

# 测试 API
curl http://localhost:8080/api/auth/health
```

## 常见问题

### Q: 为什么应用启动失败？

**A**: 检查环境变量是否正确加载：

```bash
echo $DB_PASSWORD
echo $JWT_SECRET

# 如果显示为空，需要导出环境变量：
export $(cat .env | xargs)
```

### Q: Docker Compose 找不到 .env 文件？

**A**: 确保 .env 文件在 docker-compose.yml 同级目录：

```bash
ls -la .env
# 应显示文件存在
```

### Q: 如何生成强随机密码？

**A**: 使用以下命令：

```bash
# 生成 MySQL 密码
openssl rand -base64 16

# 生成 JWT 密钥（32字节）
openssl rand -base64 32

# 或使用 Python
python3 -c "import secrets; print(secrets.token_urlsafe(32))"
```

### Q: 能否使用非 root 用户连接数据库？

**A**: 可以！修改 .env：

```bash
DB_USERNAME=agent_nurse
DB_PASSWORD=agent_nurse_password

# 并确保在 MySQL 中创建该用户并授权
```

然后在 MySQL 中执行：

```sql
CREATE USER 'agent_nurse'@'%' IDENTIFIED BY 'agent_nurse_password';
GRANT ALL PRIVILEGES ON agent_nurse.* TO 'agent_nurse'@'%';
FLUSH PRIVILEGES;
```

## 相关文档

- [ENV_SETUP.md](ENV_SETUP.md) - 详细的环境变量配置指南
- [README.md](README.md) - 项目快速开始指南
- [.env.example](.env.example) - 环境变量配置模板

## 参考资源

- [OWASP: Secure Configuration Guide](https://owasp.org/www-project-secure-coding-practices-quick-reference-guide/)
- [12-Factor App: Config](https://12factor.net/config)
- [Docker Compose: Environment Variables](https://docs.docker.com/compose/environment-variables/)
- [Spring Boot: External Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)

---

**提交**: 09b5a09  
**日期**: 2024-10-28  
**影响**: 所有环境（开发、生产）  
**向后兼容**: ⚠️ 需要创建 .env 文件
