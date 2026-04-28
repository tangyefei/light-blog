package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.service.UserService;
import com.example.demo.vo.LoginParams;
import com.example.demo.vo.RegisterParams;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.demo.mapper.UserMapper;
import com.example.demo.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Override
    public String login(LoginParams params) {
        String account = params.getAccount();
        String password = params.getPassword();
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", account).or().eq("email", account);
        User user = userMapper.selectOne(wrapper);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        String token = UUID.randomUUID().toString();
        User updatedUser = new User();
        updatedUser.setToken(token);
        updatedUser.setId(user.getId());
        userMapper.updateById(updatedUser);
        return token;
    }

    @Override
    public void register(RegisterParams registerParams) {
        String username = registerParams.getUsername();
        String password = registerParams.getPassword();
        String email = registerParams.getEmail();
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        wrapper.or().eq("email", email);
        User user = userMapper.selectOne(wrapper);
        if (user != null) {
            throw new RuntimeException("用户名或邮箱已存在");
        }
        User registerUser = new User();
        registerUser.setEmail(email);
        registerUser.setUsername(username);
        registerUser.setPassword(passwordEncoder.encode(password));
        userMapper.insert(registerUser);
    }
}
