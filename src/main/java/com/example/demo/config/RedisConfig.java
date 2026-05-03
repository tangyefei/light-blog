package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
public class RedisConfig {
    /**
     * 创建 RedisTemplate Bean。
     *
     * RedisConnectionFactory 由 Spring Boot 根据 application.properties
     * 中的 Redis 配置自动创建并注入，例如：
     *
     * spring.data.redis.host=localhost
     * spring.data.redis.port=6379
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        // 设置 Redis 连接工厂。
        // RedisTemplate 通过它连接 Redis 服务。
        template.setConnectionFactory(redisConnectionFactory);

        // key 使用字符串序列化。
        // 这样 Redis 中的 key 会是 article:detail:1 这种可读形式，
        // 而不是 JDK 序列化后的二进制内容。
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        // value 使用 JSON 序列化。
        JacksonJsonRedisSerializer<Object> jsonSerializer =
                new JacksonJsonRedisSerializer<>(Object.class);

        // 普通 key 的序列化方式。
        template.setKeySerializer(stringSerializer);

        // 普通 value 的序列化方式。
        template.setValueSerializer(jsonSerializer);

        // Hash 结构中的 field 名称序列化方式。
        // 如果以后使用 hash，例如 HSET article:1 title xxx，
        // field 名也会保持可读字符串。
        template.setHashKeySerializer(stringSerializer);

        // Hash 结构中的 value 序列化方式。
        template.setHashValueSerializer(jsonSerializer);

        // 初始化 RedisTemplate。
        // 设置完连接工厂和序列化器后调用，确保配置生效。
        template.afterPropertiesSet();

        return template;
    }
}
