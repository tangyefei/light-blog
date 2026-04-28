-- ============================================================
-- 技术博客系统 V1.0 数据库建表脚本
-- 引擎：InnoDB | 字符集：utf8mb4 | 排序规则：utf8mb4_unicode_ci
-- ============================================================

-- 创建数据库（如未创建）
CREATE DATABASE IF NOT EXISTS techblog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE techblog;

-- ============================================================
-- 1. 用户表 (user)
-- 存储博客的注册用户信息
-- ============================================================
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键自增',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名，用于登录和展示',
    `password` VARCHAR(255) NOT NULL COMMENT '密码，BCrypt加密后的密文',
    `email` VARCHAR(100) NOT NULL COMMENT '邮箱，用于找回密码和通知',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `bio` VARCHAR(500) DEFAULT NULL COMMENT '个人简介',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================================
-- 2. 分类表 (category)
-- 文章的分类，如"后端"、"前端"、"生活"等
-- ============================================================
CREATE TABLE `category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章分类表';

-- ============================================================
-- 3. 标签表 (tag)
-- 文章的标签，如"Java"、"Spring Boot"、"面试"等
-- ============================================================
CREATE TABLE `tag` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '标签ID',
    `name` VARCHAR(50) NOT NULL COMMENT '标签名称',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签表';

-- ============================================================
-- 4. 文章表 (article)
-- 核心内容表，存储文章的元数据和内容
-- ============================================================
CREATE TABLE `article` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '文章ID',
    `title` VARCHAR(200) NOT NULL COMMENT '文章标题',
    `summary` VARCHAR(500) DEFAULT NULL COMMENT '文章摘要，用于列表页展示',
    `content` LONGTEXT NOT NULL COMMENT 'Markdown格式的原始内容',
    `content_html` LONGTEXT DEFAULT NULL COMMENT 'Markdown转换后的HTML内容，冗余存储以提升读取性能',
    `user_id` BIGINT NOT NULL COMMENT '作者ID，关联user表',
    `category_id` BIGINT NOT NULL COMMENT '分类ID，关联category表',
    `views` BIGINT NOT NULL DEFAULT 0 COMMENT '浏览量，每次阅读+1',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '文章状态：0=草稿，1=已发布',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '软删除标记：0=正常，1=已删除',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_created_at` (`created_at`),
    KEY `idx_user_deleted` (`user_id`, `is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- ============================================================
-- 5. 文章标签关联表 (article_tag)
-- 实现 article 与 tag 的多对多关系
-- ============================================================
CREATE TABLE `article_tag` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联记录ID',
    `article_id` BIGINT NOT NULL COMMENT '文章ID',
    `tag_id` BIGINT NOT NULL COMMENT '标签ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关联时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_article_tag` (`article_id`, `tag_id`),
    KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章标签关联表';

-- ============================================================
-- 初始化数据（可选）
-- ============================================================
INSERT INTO `category` (`name`) VALUES
    ('后端开发'),
    ('前端开发'),
    ('系统设计'),
    ('面试经验'),
    ('生活随笔');

INSERT INTO `tag` (`name`) VALUES
    ('Java'),
    ('Spring Boot'),
    ('MySQL'),
    ('Redis'),
    ('Docker'),
    ('微服务'),
    ('面试');