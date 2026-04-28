# POST /api/users/register

## 基本信息

- **方法**：POST
- **路径**：/api/users/register
- **描述**：用户注册
- **Controller**：com.example.demo.controller.UserController#register

## 请求参数（RequestBody JSON）

```json
{
  "username": "zhangsan",
  "password": "123456",
  "email": "zhangsan@example.com"
}
```

| 字段 | 类型 | 必填 | 校验规则 |
|------|------|------|---------|
| username | String | 是 | 长度 2-20，不能为空 |
| password | String | 是 | 长度 ≥ 6，不能为空 |
| email | String | 是 | 合法邮箱格式，不能为空 |

## 响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

## 错误响应

```json
{
  "code": 400,
  "message": "用户名已存在",
  "data": null
}
```

## DTO 类

- 请求：com.example.demo.dto.RegisterRequest
