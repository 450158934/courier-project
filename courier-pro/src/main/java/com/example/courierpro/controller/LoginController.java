package com.example.courierpro.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.courierpro.common.Result;
import com.example.courierpro.entity.User;
import com.example.courierpro.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class LoginController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/hello")
    public String hello() { return "后端正常运行中..."; }

    // 登录
    @PostMapping("/login")
    public Result<?> login(@RequestBody User loginUser) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", loginUser.getUsername());
        wrapper.eq("password", loginUser.getPassword());
        User user = userMapper.selectOne(wrapper);
        if (user != null) return Result.success(user);
        return Result.error("账号或密码错误");
    }

    // ✅ 新增：注册接口
    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        // 1. 检查账号是否重复
        QueryWrapper<User> check = new QueryWrapper<>();
        check.eq("username", user.getUsername());
        if (userMapper.selectCount(check) > 0) {
            return Result.error("该账号已存在");
        }
        // 2. 插入数据库 (默认余额0)
        user.setBalance(new java.math.BigDecimal("0.00"));
        userMapper.insert(user);
        return Result.success("注册成功");
    }
}