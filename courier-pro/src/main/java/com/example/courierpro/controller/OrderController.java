package com.example.courierpro.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.courierpro.common.Result;
import com.example.courierpro.entity.Order;
import com.example.courierpro.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderMapper orderMapper;

    @GetMapping("/list")
    public Result<?> getOrderList(@RequestParam(required = false) String keyword) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 0);
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like("pickup_addr", keyword).or().like("dest_addr", keyword));
        }
        wrapper.orderByDesc("id");
        return Result.success(orderMapper.selectList(wrapper));
    }

    @PostMapping("/add")
    public Result<?> addOrder(@RequestBody Order order) {
        order.setOrderNo("ORD" + System.currentTimeMillis());
        order.setStatus(0);
        // ⚠️ 注意：这里不再写死 ID，而是直接用前端传来的 customerId
        orderMapper.insert(order);
        return Result.success("发布成功");
    }

    @PostMapping("/take")
    public Result<?> takeOrder(@RequestBody Order order) {
        Order dbOrder = orderMapper.selectById(order.getId());
        if (dbOrder == null) return Result.error("订单不存在");
        if (dbOrder.getStatus() != 0) return Result.error("手慢了");

        dbOrder.setStatus(1);
        // ⚠️ 注意：这里用前端传来的 runnerId
        dbOrder.setRunnerId(order.getRunnerId());
        orderMapper.updateById(dbOrder);
        return Result.success("抢单成功");
    }

    @GetMapping("/my-orders")
    public Result<?> getMyOrders(@RequestParam Integer type, @RequestParam Long userId) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");
        // ⚠️ 动态查询 userId
        if (type == 1) wrapper.eq("customer_id", userId);
        else if (type == 2) wrapper.eq("runner_id", userId);
        return Result.success(orderMapper.selectList(wrapper));
    }

    @PostMapping("/complete")
    public Result<?> completeOrder(@RequestBody Order order) {
        Order dbOrder = orderMapper.selectById(order.getId());
        dbOrder.setStatus(2);
        orderMapper.updateById(dbOrder);
        return Result.success("订单已送达");
    }

    @PostMapping("/cancel")
    public Result<?> cancelOrder(@RequestBody Order order) {
        Order dbOrder = orderMapper.selectById(order.getId());
        if (dbOrder.getStatus() != 0) return Result.error("无法取消");
        orderMapper.deleteById(order.getId());
        return Result.success("订单已取消");
    }

    // ✅ 新增：评价接口
    @PostMapping("/rate")
    public Result<?> rateOrder(@RequestBody Order order) {
        Order dbOrder = orderMapper.selectById(order.getId());
        dbOrder.setRating(order.getRating());
        dbOrder.setComment(order.getComment());
        orderMapper.updateById(dbOrder);
        return Result.success("评价成功");
    }
}