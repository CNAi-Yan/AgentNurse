# AgentNurse API 使用示例

本文档提供详细的 API 调用示例，包括 cURL 命令和预期响应。

## 基础信息

- **Base URL**: `http://localhost:8080/api`
- **认证方式**: JWT Bearer Token
- **内容类型**: `application/json`

## 1. 认证接口

### 1.1 用户注册

#### 请求示例（患者）

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "patient001",
    "email": "patient@example.com",
    "password": "Patient@123",
    "role": "PATIENT",
    "realName": "张三",
    "phone": "13800138000"
  }'
```

#### 请求示例（家属）

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "family001",
    "email": "family@example.com",
    "password": "Family@123",
    "role": "FAMILY",
    "realName": "李四",
    "phone": "13800138001"
  }'
```

#### 请求示例（护理员）

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "caregiver001",
    "email": "caregiver@example.com",
    "password": "Caregiver@123",
    "role": "CAREGIVER",
    "realName": "王五",
    "phone": "13800138002"
  }'
```

#### 成功响应

```json
{
  "success": true,
  "message": "注册成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJwYXRpZW50MDAxIiwiaWF0IjoxNzA0MTAwMDAwLCJleHAiOjE3MDQxODY0MDB9.abcdefg...",
    "tokenType": "Bearer",
    "user": {
      "id": 1,
      "username": "patient001",
      "email": "patient@example.com",
      "role": "PATIENT",
      "realName": "张三",
      "phone": "13800138000",
      "gender": null,
      "birthDate": null,
      "address": null,
      "avatarUrl": null,
      "enabled": true,
      "emailVerified": false,
      "createdAt": "2024-10-28T12:00:00",
      "lastLoginAt": null
    }
  },
  "timestamp": "2024-10-28T12:00:00"
}
```

#### 错误响应示例

**用户名已存在**:
```json
{
  "success": false,
  "message": "用户名已存在",
  "data": null,
  "timestamp": "2024-10-28T12:00:00"
}
```

**密码强度不足**:
```json
{
  "success": false,
  "message": "参数验证失败",
  "data": null,
  "timestamp": "2024-10-28T12:00:00"
}
```

### 1.2 用户登录

#### 使用用户名登录

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "patient001",
    "password": "Patient@123"
  }'
```

#### 使用邮箱登录

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "patient@example.com",
    "password": "Patient@123"
  }'
```

#### 成功响应

```json
{
  "success": true,
  "message": "登录成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "user": {
      "id": 1,
      "username": "patient001",
      "email": "patient@example.com",
      "role": "PATIENT",
      "realName": "张三",
      "phone": "13800138000",
      "enabled": true,
      "emailVerified": false,
      "createdAt": "2024-10-28T12:00:00",
      "lastLoginAt": "2024-10-28T13:30:00"
    }
  },
  "timestamp": "2024-10-28T13:30:00"
}
```

#### 错误响应

```json
{
  "success": false,
  "message": "用户名/邮箱或密码错误",
  "data": null,
  "timestamp": "2024-10-28T12:00:00"
}
```

### 1.3 健康检查

```bash
curl -X GET http://localhost:8080/api/auth/health
```

#### 响应

```json
{
  "success": true,
  "message": "操作成功",
  "data": "AgentNurse 服务运行正常",
  "timestamp": "2024-10-28T12:00:00"
}
```

## 2. 用户管理接口（需要认证）

### 设置认证 Token

先登录获取 token，然后在后续请求中使用：

```bash
# 登录并保存 token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "patient001",
    "password": "Patient@123"
  }' | jq -r '.data.accessToken')

echo "Token: $TOKEN"
```

### 2.1 获取当前用户信息

```bash
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer $TOKEN"
```

#### 成功响应

```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "patient001",
    "email": "patient@example.com",
    "role": "PATIENT",
    "realName": "张三",
    "phone": "13800138000",
    "gender": "M",
    "birthDate": "1990-01-01",
    "address": "北京市朝阳区",
    "avatarUrl": "https://example.com/avatar.jpg",
    "enabled": true,
    "emailVerified": false,
    "createdAt": "2024-10-28T12:00:00",
    "lastLoginAt": "2024-10-28T13:30:00"
  },
  "timestamp": "2024-10-28T14:00:00"
}
```

### 2.2 更新用户资料

```bash
curl -X PUT http://localhost:8080/api/users/me \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "realName": "张三丰",
    "phone": "13900139000",
    "gender": "M",
    "birthDate": "1990-01-01",
    "address": "北京市朝阳区建国路1号",
    "avatarUrl": "https://example.com/avatar-new.jpg"
  }'
```

#### 成功响应

```json
{
  "success": true,
  "message": "资料更新成功",
  "data": {
    "id": 1,
    "username": "patient001",
    "email": "patient@example.com",
    "role": "PATIENT",
    "realName": "张三丰",
    "phone": "13900139000",
    "gender": "M",
    "birthDate": "1990-01-01",
    "address": "北京市朝阳区建国路1号",
    "avatarUrl": "https://example.com/avatar-new.jpg",
    "enabled": true,
    "emailVerified": false,
    "createdAt": "2024-10-28T12:00:00",
    "lastLoginAt": "2024-10-28T13:30:00"
  },
  "timestamp": "2024-10-28T14:30:00"
}
```

### 2.3 更新邮箱（需要重新验证）

```bash
curl -X PUT http://localhost:8080/api/users/me \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "newemail@example.com"
  }'
```

### 2.4 删除账户

```bash
curl -X DELETE http://localhost:8080/api/users/me \
  -H "Authorization: Bearer $TOKEN"
```

#### 成功响应

```json
{
  "success": true,
  "message": "账户已删除",
  "data": null,
  "timestamp": "2024-10-28T15:00:00"
}
```

## 3. 管理员接口（需要 ADMIN 角色）

### 3.1 根据 ID 获取用户信息

```bash
curl -X GET http://localhost:8080/api/users/1 \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

## 4. 错误处理

### 4.1 未认证

```bash
curl -X GET http://localhost:8080/api/users/me
```

响应（HTTP 401）：

```json
{
  "success": false,
  "message": "未授权访问",
  "data": null,
  "timestamp": "2024-10-28T12:00:00"
}
```

### 4.2 Token 过期

响应（HTTP 401）：

```json
{
  "success": false,
  "message": "Token已过期",
  "data": null,
  "timestamp": "2024-10-28T12:00:00"
}
```

### 4.3 权限不足

响应（HTTP 403）：

```json
{
  "success": false,
  "message": "没有权限访问此资源",
  "data": null,
  "timestamp": "2024-10-28T12:00:00"
}
```

## 5. 完整测试流程

### Shell 脚本示例

```bash
#!/bin/bash

BASE_URL="http://localhost:8080/api"

echo "=== 1. 健康检查 ==="
curl -s "$BASE_URL/auth/health" | jq

echo -e "\n=== 2. 注册患者账户 ==="
REGISTER_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test_patient",
    "email": "test@example.com",
    "password": "Test@123",
    "role": "PATIENT",
    "realName": "测试患者"
  }')

echo $REGISTER_RESPONSE | jq

TOKEN=$(echo $REGISTER_RESPONSE | jq -r '.data.accessToken')

echo -e "\n=== 3. 获取用户信息 ==="
curl -s "$BASE_URL/users/me" \
  -H "Authorization: Bearer $TOKEN" | jq

echo -e "\n=== 4. 更新用户资料 ==="
curl -s -X PUT "$BASE_URL/users/me" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "realName": "测试患者（已更新）",
    "phone": "13800138000",
    "gender": "M"
  }' | jq

echo -e "\n=== 5. 登出并重新登录 ==="
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "test_patient",
    "password": "Test@123"
  }')

echo $LOGIN_RESPONSE | jq

echo -e "\n测试完成！"
```

## 6. Postman Collection

可以导入以下 JSON 到 Postman：

```json
{
  "info": {
    "name": "AgentNurse API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Auth",
      "item": [
        {
          "name": "Register",
          "request": {
            "method": "POST",
            "header": [],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"username\": \"patient001\",\n  \"email\": \"patient@example.com\",\n  \"password\": \"Patient@123\",\n  \"role\": \"PATIENT\"\n}",
              "options": {
                "raw": {
                  "language": "json"
                }
              }
            },
            "url": {
              "raw": "{{base_url}}/auth/register",
              "host": ["{{base_url}}"],
              "path": ["auth", "register"]
            }
          }
        },
        {
          "name": "Login",
          "request": {
            "method": "POST",
            "header": [],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"usernameOrEmail\": \"patient001\",\n  \"password\": \"Patient@123\"\n}",
              "options": {
                "raw": {
                  "language": "json"
                }
              }
            },
            "url": {
              "raw": "{{base_url}}/auth/login",
              "host": ["{{base_url}}"],
              "path": ["auth", "login"]
            }
          }
        }
      ]
    }
  ],
  "variable": [
    {
      "key": "base_url",
      "value": "http://localhost:8080/api"
    }
  ]
}
```

## 7. 注意事项

1. **Token 有效期**：JWT token 有效期为 24 小时
2. **密码要求**：8-20 字符，包含大小写字母、数字和特殊字符
3. **用户名规则**：3-50 字符，只能包含字母、数字和下划线
4. **邮箱验证**：注册后 `emailVerified` 为 `false`，需要实现邮箱验证功能
5. **时区**：所有时间戳使用 GMT+8（北京时间）

## 8. 故障排查

### 数据库连接失败

```bash
# 检查 MySQL 是否运行
docker-compose ps

# 查看数据库日志
docker-compose logs mysql
```

### 应用启动失败

```bash
# 查看应用日志
tail -f logs/spring.log

# 检查配置
cat src/main/resources/application.yml
```
