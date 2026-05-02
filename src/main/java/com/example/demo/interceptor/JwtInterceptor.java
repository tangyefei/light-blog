package com.example.demo.interceptor;

import com.example.demo.common.Result;
import com.example.demo.common.ResultCode;
import com.example.demo.context.UserContext;
import com.example.demo.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import tools.jackson.databind.ObjectMapper;

/**
 * JWT 拦截器，校验请求中的 Authorization 头
 */
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");

        // token 为空直接拒绝
        if (token == null || token.isBlank()) {
            writeUnauthorizedResponse(response);
            return false;
        }

        // 处理 Bearer 前缀（兼容标准 OAuth2 格式）
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 校验 token
        if (!jwtUtils.validateToken(token)) {
            writeUnauthorizedResponse(response);
            return false;
        }

        // 保存用户信息到线程上下文
        UserContext.setUserId(jwtUtils.getUserIdFromToken(token));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear();
    }

    /**
     * 写入 401 统一响应（复用 Result + ResultCode）
     */
    private void writeUnauthorizedResponse(HttpServletResponse response) throws Exception {
        response.setStatus(ResultCode.UNAUTHORIZED.getCode());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(Result.fail(ResultCode.UNAUTHORIZED)));
    }
}
