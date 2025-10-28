# 变更日志

本文档记录 AgentNurse 项目的所有重要变更。

## [0.0.1] - 2024-10-28

### ✨ 新增功能

#### AGE-1: 账号与角色基础
- 实现三类用户角色：患者 (PATIENT)、家属 (FAMILY)、护理员 (CAREGIVER)
- 用户注册功能
  - 用户名唯一性验证
  - 邮箱格式验证和唯一性检查
  - 密码强度验证（大小写字母+数字+特殊字符）
  - BCrypt 密码加密
- 用户登录功能
  - 支持用户名登录
  - 支持邮箱登录
  - JWT 令牌生成（24小时有效期）
  - 自动更新最后登录时间
- 用户资料管理
  - 查询当前用户信息
  - 更新用户资料（姓名、手机、性别、生日、地址、头像）
  - 邮箱变更（需重新验证）
  - 删除账户功能
- 管理员功能
  - 根据用户 ID 查询用户信息

### 🔒 安全特性
- Spring Security 集成
- JWT 认证机制
- BCrypt 密码加密
- CORS 跨域配置
- 全局异常处理
- 请求参数验证

### 🗃️ 数据库
- MySQL 8.0 支持
- 用户表设计和索引优化
- 自动时间戳（创建时间、更新时间）
- 数据库初始化脚本

### 📦 技术栈
- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- JWT (jjwt 0.12.3)
- MySQL 8.0
- Lombok

### 📝 文档
- README 项目介绍和快速开始指南
- API_EXAMPLES 完整的 API 调用示例
- 数据库 Schema 文档
- Docker Compose 配置

### 🐳 DevOps
- Docker Compose 支持
- Dockerfile 构建配置
- 多环境配置（dev/prod）
- .gitignore 配置

## 下一个版本计划

### [0.0.2] - 规划中
- 邮箱验证功能
- 忘记密码/重置密码
- 用户头像上传
- OAuth 第三方登录（Google, GitHub）
- 用户列表和搜索（管理员）
- 角色权限细化

### [0.1.0] - M0 基线完成
- 数据模型设计
- 记录表单功能
- 提醒基础功能
- 非诊断拦截器

---

## 版本说明

- **[Unreleased]**: 正在开发的功能
- **[0.x.x]**: MVP 阶段，快速迭代
- **[1.0.0]**: 正式发布版本

## 提交规范

- ✨ feat: 新功能
- 🐛 fix: 修复 Bug
- 📝 docs: 文档更新
- 🎨 style: 代码格式
- ♻️ refactor: 重构
- ⚡ perf: 性能优化
- ✅ test: 测试
- 🔧 chore: 构建/工具
- 🔒 security: 安全
