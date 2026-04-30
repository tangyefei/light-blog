-- light-blog 数据库初始化脚本
-- 数据库：light-blog

CREATE DATABASE IF NOT EXISTS `techblog` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `techblog`;


-- 删除用户表
DROP TABLE `user`;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `username`   VARCHAR(20)  NOT NULL COMMENT '用户名（唯一）',
    `password`   VARCHAR(100) NOT NULL COMMENT '密码（BCrypt 加密）',
    `email`      VARCHAR(100) NOT NULL COMMENT '邮箱（唯一）',
    `avatar`     VARCHAR(255) DEFAULT NULL COMMENT '头像 URL',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '用户表';
