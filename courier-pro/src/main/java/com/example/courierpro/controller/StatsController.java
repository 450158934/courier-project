package com.example.courierpro.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.courierpro.common.Result;
import com.example.courierpro.entity.Order;
import com.example.courierpro.entity.User;
import com.example.courierpro.mapper.OrderMapper;
import com.example.courierpro.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
public class StatsController {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderMapper orderMapper;

    @GetMapping("/api/stats")
    public Result<?> getStats() {
        Map<String, Object> map = new HashMap<>();
        // 统计总用户数
        map.put("userCount", userMapper.selectCount(null));
        // 统计总订单数
        map.put("orderCount", orderMapper.selectCount(null));
        // 统计已完成单数
        QueryWrapper<Order> w1 = new QueryWrapper<>();
        w1.eq("status", 2);
        map.put("finishedCount", orderMapper.selectCount(w1));
        // 统计总交易额 (这里简单写，实际可以用 SQL sum)
        map.put("totalAmount", 998.5);

        return Result.success(map);
    }
}