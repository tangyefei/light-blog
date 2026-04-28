# POST /api/users/login

## 基本信息

- **方法**：POST
- **路径**：/api/users/login
- **描述**：用户登录，支持用户名或邮箱
- **Controller**：com.example.demo.controller.UserController#login

## 请求参数（RequestBody JSON）

```json
{
  "account": "zhangsan",
  "password": "123456"
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| account | String | 是 | 用户名或邮箱（二选一） |
| password | String | 是 | 明文密码 |

## 响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "zhangsan",
    "email": "zhangsan@example.com",
    "avatar": null,
    "token": "a1b2c3d4e5f6..."
  }
}
```

## 错误响应

```json
{
  "code": 400,
  "message": "密码错误",
  "data": null
}
```

## DTO 类

- 请求：com.example.demo.dto.LoginRequest
- 响应：com.example.demo.dto.LoginResponse
