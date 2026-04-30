package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.context.UserContext;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户模块接口
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "用户模块", description = "用户注册、登录相关接口")
public class UserController {


    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "注册新用户，用户名和邮箱需唯一，密码长度不少于 6 位")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return Result.success();
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "支持用户名或邮箱登录，登录成功返回 UUID Token")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return Result.success(response);
    }

    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息", description = "获取当前用户信息")
    public Result<User> me() {
        Long userId = UserContext.getUserId();
        User user = userService.getById(userId);
        return Result.success(user);

    }
}
