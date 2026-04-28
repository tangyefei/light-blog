# Result-统一响应封装

## 文件位置

- `src/main/java/com/example/demo/common/Result.java`
- `src/main/java/com/example/demo/common/ResultCode.java`

## ResultCode 枚举（状态码集中管理）

```java
// SUCCESS(200) / BAD_REQUEST(400) / UNAUTHORIZED(401) / FORBIDDEN(403) / NOT_FOUND(404) / INTERNAL_ERROR(500)
Result.fail(ResultCode.UNAUTHORIZED);
Result.fail(ResultCode.NOT_FOUND, "用户不存在");
```

## Result<T> 工厂方法速查

| 方法 | 场景 |
|------|------|
| `Result.success(data)` | 查询/登录等有返回数据的成功响应 |
| `Result.success()` | 注册/删除等无返回数据的成功响应 |
| `Result.fail("用户名已存在")` | 业务校验失败，默认 400 |
| `Result.fail(ResultCode.UNAUTHORIZED)` | 枚举状态码 + 默认 message |
| `Result.fail(ResultCode.NOT_FOUND, "用户不存在")` | 枚举状态码 + 自定义 message |
| `Result.fail(500, "自定义错误")` | 完全自定义（兜底，少用） |

## 设计要点

- 使用 `@Getter` + `final` 字段，响应对象不可变（不生成 setter）
- 构造器私有，只能通过静态工厂方法创建
- `GlobalExceptionHandler` 中统一使用 `ResultCode` 枚举返回错误
