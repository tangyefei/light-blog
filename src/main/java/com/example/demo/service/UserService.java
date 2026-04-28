package com.example.demo.service;
import com.example.demo.mapper.UserMapper;
import com.example.demo.vo.LoginParams;
import com.example.demo.vo.RegisterParams;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.demo.entity.User;
import org.springframework.stereotype.Service;

public interface UserService {

    public void register(RegisterParams registerParams);

    public String login(LoginParams params);
}
