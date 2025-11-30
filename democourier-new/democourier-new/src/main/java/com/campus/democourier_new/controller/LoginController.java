package com.campus.democourier_new.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.democourier_new.common.Result;
import com.campus.democourier_new.entity.User;
import com.campus.democourier_new.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class LoginController {

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/login")
    public Result<?> login(@RequestBody User loginUser) {
        System.out.println("收到登录请求: " + loginUser.getUsername());

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", loginUser.getUsername());
        wrapper.eq("password", loginUser.getPassword());

        User user = userMapper.selectOne(wrapper);

        if (user != null) {
            return Result.success(user);
        } else {
            return Result.error("账号或密码错误");
        }
    }
}