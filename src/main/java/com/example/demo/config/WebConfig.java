package com.example.demo.config;

import com.example.demo.interceptor.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置：注册拦截器 + 放行白名单
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 对所有后端接口启用跨域配置。
        registry.addMapping("/**")
                // 允许本地前端开发服务访问后端接口，兼容 3000 被占用后 Next.js 自动切换端口的情况。
                .allowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*")
                // 允许前端使用常见的接口请求方法，并允许浏览器预检请求。
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允许前端携带任意请求头，例如 Content-Type 和 Authorization。
                .allowedHeaders("*")
                // 允许请求携带凭证信息，例如 Cookie 或认证相关请求头。
                .allowCredentials(true)
                // 预检请求结果缓存 3600 秒，减少浏览器重复发送 OPTIONS 请求。
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        // 登录/注册接口
                        "/api/users/login",
                        "/api/users/register",
                        // Knife4j / Swagger 文档
                        "/doc.html",
                        "/favicon.ico",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**"
                );
    }
}
