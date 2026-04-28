# 项目规则（Project Rules）

> 本文件定义了 `light-blog` 项目的开发约定。所有 AI 助手在该项目中工作时，必须遵守以下规则。

## 📁 文档存放规则

### 规则 1：所有 Markdown 文档默认放入 `docs/` 目录

- **范围**：所有由 AI 助手生成的 `.md` 文档（除项目根目录的 `README.md`、`HELP.md` 等约定文件外）
- **路径**：`docs/<文档名>.md`
- **示例**：
  - ✅ `docs/项目初始化改动总结-初学者版.md`
  - ✅ `docs/Spring Boot 4.0 + MyBatis-Plus + Knife4j 踩坑总结.md`
  - ✅ `docs/API 设计文档.md`
  - ❌ `项目说明.md`（不应放在根目录）
  - ❌ `src/main/java/notes.md`（不应放在源码目录）

### 规则 2：文档命名约定

- 使用**中文命名**，名称要清晰描述文档主题
- 如有版本/受众区分，用 `-` 分隔，如：`xxx-初学者版.md`、`xxx-v2.md`
- 踩坑/经验类文档建议加上明确分类，如：`xxx 踩坑总结.md`

### 规则 3：文档结构建议

- 顶部添加 YAML Front Matter（如适合 Obsidian 使用）：
  ```yaml
  ---
  title: 文档标题
  date: YYYY-MM-DD
  tags: [标签1, 标签2]
  ---
  ```
- 使用清晰的章节层级（`##`、`###`），不要使用 `#`（避免与标题冲突）

## 🛠️ 当前项目技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 框架 | Spring Boot | 4.0.6 |
| JDK | Java | 17 |
| ORM | MyBatis-Plus | 3.5.16 |
| API 文档 | Knife4j + SpringDoc | 4.5.0 + 3.0.0 |
| 数据库 | MySQL | 8.x |

## 📌 重要历史决策

> 这些决策是经过踩坑验证的，非必要不要修改：

1. **SpringDoc 必须显式引入 3.0.0**，并排除 Knife4j 自带的 2.3.0 版本
   - 原因：Spring Boot 4.0 使用 Jackson 3，Knife4j 自带的 SpringDoc 2.3.0 用 Jackson 2，会冲突
   - 详见：`docs/Spring Boot 4.0 + MyBatis-Plus + Knife4j 踩坑总结.md`

2. **MyBatis-Plus 必须使用 `mybatis-plus-spring-boot4-starter`**，不能使用 `spring-boot3-starter`
   - 原因：Spring Boot 大版本不同，starter 不通用

## 🎯 后续协作建议

- 修改 pom.xml 依赖前，先阅读 `docs/` 下的踩坑文档，避免重复踩坑
- 新增功能模块时，建议同步在 `docs/` 下补充设计文档或使用说明
