package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.vo.RegisterParams;
import com.example.demo.vo.LoginParams;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public String login(@RequestBody LoginParams params) {
        String token =  userService.login(params);
        return token;
    }
        @PostMapping("/register")
    public String register(@RequestBody RegisterParams registerParams) {

        if (registerParams.getUsername() == null || registerParams.getEmail() == null) {
            return "username or email cannot be empty";
        }
        else if(registerParams.getPassword() == null) {
            return "password cannot be empty";
        }

        userService.register(registerParams);

        return "success";
    }
}
