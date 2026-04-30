package com.example.demo;

import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.impl.UserServiceImpl;
import com.example.demo.utils.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private JwtUtils jwtUtils;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void register_NewUser_ReturnTrue() {
        // Mock：用户名和邮箱都不存在（selectCount 返回 0）
        when(userMapper.selectCount(any())).thenReturn(0L);
        // Mock：密码加密返回固定值
        when(Objects.requireNonNull(passwordEncoder.encode(anyString()))).thenReturn("$2a$10$encodedPassword");

        boolean result = userService.register(new RegisterRequest("alice", "123456", "alice@example.com"));

        assertTrue(result);
        verify(userMapper, times(1)).insert(any(User.class));
    }
}
