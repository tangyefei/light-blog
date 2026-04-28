# Knife4j 与 Swagger 的关系

> 📅 **日期**：2026-04-28
> 🏷️ **标签**：Knife4j、Swagger、OpenAPI、SpringDoc、概念辨析
> 👶 **适合人群**：刚接触 API 文档生态的初学者

---

## 🎯 一句话总结

> 🍔 **Swagger 是"食材和厨房"，Knife4j 是"装盘和摆盘"**
>
> Knife4j 不能独立存在，必须配合 Swagger 阵营的 SpringDoc 使用。

---

## 📜 一、按时间线理清关系

```
2011 年：Swagger 诞生
   ↓ （定义了"如何描述 REST API"的规范）
2016 年：Swagger 规范捐献给 Linux 基金会，改名为 OpenAPI
   ↓ （Swagger 这个名字保留给"工具集"）
现在的格局：
   ├─ OpenAPI = 规范（一套 JSON/YAML 格式标准）
   ├─ Swagger = 一系列工具（Swagger UI、Swagger Editor 等）
   └─ Knife4j = 国人基于 Swagger UI 做的"增强版"
```

---

## 🧩 二、它们各自的职责

| 名称 | 是什么 | 干什么的 |
|------|--------|---------|
| **OpenAPI** | 规范 | 一套标准，规定"接口文档应该长什么样"（JSON 格式） |
| **Swagger Annotations**（如 `@Operation`、`@Tag`） | 注解 | 写在 Controller 上，描述接口信息 |
| **SpringDoc** | Java 库 | 扫描你的 Controller，按 OpenAPI 规范生成 JSON |
| **Swagger UI** | 前端页面 | 把 JSON 渲染成网页，访问 `/swagger-ui.html` |
| **Knife4j** | 增强前端页面 | **替换** Swagger UI，提供更好看的中文页面，访问 `/doc.html` |

---

## 🔄 三、整个链路是怎么跑通的

```
你写的 Controller 代码
    ↓ （加了 @Operation、@Tag 等 Swagger 注解）
SpringDoc 扫描
    ↓ （按 OpenAPI 规范生成）
/v3/api-docs 返回 JSON 数据
    ↓ （前端读取这个 JSON）
    ├─ Swagger UI 渲染 → /swagger-ui.html （原生丑丑的）
    └─ Knife4j 渲染   → /doc.html         （增强版好看）
```

> 💡 **关键点**：Swagger UI 和 Knife4j **共用同一份数据**（来自 SpringDoc），只是前端展示不同。所以你这次的项目能同时访问 `/swagger-ui.html` 和 `/doc.html`。

---

## 🇨🇳 四、为什么要用 Knife4j？

Knife4j 是国内开发者 [@xiaoymin](https://github.com/xiaoymin) 基于 Swagger UI 改造的，主要增强点：

| 功能 | Swagger UI | Knife4j |
|------|-----------|---------|
| 中文界面 | ❌ 全英文 | ✅ 完美中文 |
| 文档导出 | ❌ | ✅ 支持导出 Markdown / HTML |
| 接口调试体验 | 一般 | ✅ 更友好的参数填写、响应展示 |
| 全局参数（如 token） | 麻烦 | ✅ 一键设置 |
| 离线文档 | ❌ | ✅ 可生成离线文档 |
| 接口排序、分组 | 弱 | ✅ 支持 |

简单说：**Knife4j = 一个更适合中国人用的 Swagger UI 皮肤**。

---

## 🎯 五、用本项目的代码举例

打开 `pom.xml` 看看：

```xml
<!-- ① Knife4j：提供 /doc.html 美化界面 -->
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
    <version>4.5.0</version>
</dependency>

<!-- ② SpringDoc：扫描 Controller、生成 OpenAPI JSON -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>3.0.0</version>
</dependency>
```

- **SpringDoc** 是「**真正干活的**」：扫描代码 → 生成数据
- **Knife4j** 是「**摆门面的**」：把数据渲染成漂亮的网页

> 🚨 这也解释了之前踩的坑（详见 `docs/Spring Boot 4.0 + MyBatis-Plus + Knife4j 踩坑总结.md`）：Knife4j 离不开 SpringDoc，SpringDoc 挂了，Knife4j 自然就 404 了。

---

## 📝 六、常见疑问 FAQ

### Q1：Swagger 和 OpenAPI 到底是不是一回事？

不完全是。
- **2016 年之前**：Swagger 既是规范，也是工具集，名字混用
- **2016 年之后**：规范改名为 OpenAPI，Swagger 这个品牌保留给工具
- **现在的关系**：OpenAPI 是规范，Swagger 是实现这个规范的工具集（注解、UI 等）

### Q2：我能不用 Knife4j 吗？

**能**。直接用 SpringDoc 自带的 Swagger UI（访问 `/swagger-ui.html`），但界面是英文且没那么好看。如果你的项目主要面向英文用户，或者团队习惯用原生 Swagger UI，那就不需要 Knife4j。

### Q3：能不能不用 SpringDoc，只用 Knife4j？

**不能**。Knife4j 只是一个前端 UI，它**没有扫描代码生成 OpenAPI JSON 的能力**。必须搭配 SpringDoc（或者更早的 SpringFox）来生成数据。

### Q4：SpringFox 和 SpringDoc 是什么关系？

- **SpringFox**：老牌的 OpenAPI 生成器，主要支持 Spring Boot 1.x / 2.x，**已停止维护**
- **SpringDoc**：现代化的 OpenAPI 生成器，支持 Spring Boot 2.x / 3.x / 4.x，**活跃维护中**
- 新项目应该用 **SpringDoc**

### Q5：注解写 `@Operation` 还是 `@ApiOperation`？

- `@ApiOperation`：**Swagger 2.x** 时代的注解（来自 `io.swagger.annotations`）
- `@Operation`：**OpenAPI 3.x** 的新注解（来自 `io.swagger.v3.oas.annotations`）

**新项目统一用 `@Operation`**（OpenAPI 3.x），Spring Boot 3+ 都不支持旧的 Swagger 2 注解了。

---

## 📖 七、整体生态图（一图看懂）

```
                    ┌──────────────────┐
                    │   OpenAPI 规范    │  ← 行业标准
                    │  (JSON / YAML)   │
                    └────────┬─────────┘
                             │ 实现
            ┌────────────────┼────────────────┐
            │                │                │
            ▼                ▼                ▼
    ┌──────────────┐  ┌──────────┐  ┌────────────────┐
    │  Swagger     │  │ SpringDoc │  │    其他实现     │
    │  Annotations │  │ (生成器)  │  │ （Java 之外）   │
    │  (注解库)    │  │          │  │                │
    └──────┬───────┘  └─────┬────┘  └────────────────┘
           │                │
           │  你在 Controller│ 扫描代码生成
           │  上加注解        │ /v3/api-docs (JSON)
           │                │
           └─────────┬──────┘
                     ▼
              ┌─────────────┐
              │ OpenAPI JSON│ ← 中间数据
              └──────┬──────┘
                     │ 渲染成网页
            ┌────────┴────────┐
            ▼                 ▼
    ┌─────────────┐    ┌─────────────┐
    │ Swagger UI  │    │   Knife4j   │
    │ (官方原生)  │    │ (国人增强版)│
    │/swagger-ui  │    │  /doc.html  │
    └─────────────┘    └─────────────┘
```

---

## 🔗 相关笔记

- [[Spring Boot 4.0 + MyBatis-Plus + Knife4j 踩坑总结]]
- [[项目初始化改动总结-初学者版]]

## 📚 参考资料

- [Knife4j 官网](https://doc.xiaominfo.com)
- [SpringDoc 官网](https://springdoc.org)
- [OpenAPI 规范](https://www.openapis.org)
- [Swagger 官网](https://swagger.io)
