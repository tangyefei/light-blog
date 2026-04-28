# light-blog 项目级记忆索引

## 项目信息

- **项目名称**：light-blog
- **技术栈**：Spring Boot 4.0.6 + MyBatis-Plus 3.5.16 + MySQL 8.x + Knife4j 4.5.0
- **包名**：com.example.demo
- **数据库**：light-blog（localhost:3306, root/1234qwer）

## 资产索引

### data-models（数据模型）

| 条目标题 | 文件 | 描述 |
|---------|------|------|
| User-用户实体 | data-models/User-用户实体.md | 用户表实体类，含 id/username/password/email/avatar/token/createdAt/updatedAt |

### api-interfaces（API 接口）

| 条目标题 | 文件 | 描述 |
|---------|------|------|
| POST-api-users-register | api-interfaces/POST-api-users-register.md | 用户注册接口 |
| POST-api-users-login | api-interfaces/POST-api-users-login.md | 用户登录接口，返回 UUID Token |

### business-logic（业务逻辑）

| 条目标题 | 文件 | 描述 |
|---------|------|------|
| 用户注册业务规则 | business-logic/用户注册业务规则.md | 用户名/邮箱唯一性校验、BCrypt 密码加密 |
| 用户登录业务规则 | business-logic/用户登录业务规则.md | 支持用户名或邮箱登录，UUID Token 存库 |

### configurations（配置规范）

| 条目标题 | 文件 | 描述 |
|---------|------|------|
| 项目目录结构 | configurations/项目目录结构.md | 包结构约定：entity/mapper/service/controller/dto/common/config |
