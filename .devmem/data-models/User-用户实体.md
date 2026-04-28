# User-用户实体

## 基本信息

- **表名**：user
- **实体类**：com.example.demo.entity.User
- **注解**：@Data（Lombok）、@TableName("user")、@TableId(type = IdType.AUTO)

## 字段定义

| 字段名（Java） | 字段名（DB） | 类型 | 说明 |
|--------------|------------|------|------|
| id | id | Long / BIGINT | 主键，自增 |
| username | username | String / VARCHAR(20) | 用户名，唯一 |
| password | password | String / VARCHAR(100) | BCrypt 加密密码 |
| email | email | String / VARCHAR(100) | 邮箱，唯一 |
| avatar | avatar | String / VARCHAR(255) | 头像 URL，可为空 |
| token | token | String / VARCHAR(64) | UUID Token，登录后存库 |
| createdAt | created_at | LocalDateTime / DATETIME | 创建时间 |
| updatedAt | updated_at | LocalDateTime / DATETIME | 更新时间（自动更新） |

## 建表 SQL

```sql
CREATE TABLE IF NOT EXISTS `user` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `username`   VARCHAR(20)  NOT NULL COMMENT '用户名（唯一）',
    `password`   VARCHAR(100) NOT NULL COMMENT '密码（BCrypt 加密）',
    `email`      VARCHAR(100) NOT NULL COMMENT '邮箱（唯一）',
    `avatar`     VARCHAR(255) DEFAULT NULL COMMENT '头像 URL',
    `token`      VARCHAR(64)  DEFAULT NULL COMMENT '登录 Token（UUID）',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表';
```

## 相关文件

- Mapper：com.example.demo.mapper.UserMapper
- Service：com.example.demo.service.UserService
- ServiceImpl：com.example.demo.service.impl.UserServiceImpl
