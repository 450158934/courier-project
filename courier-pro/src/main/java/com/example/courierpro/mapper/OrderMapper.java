package com.example.courierpro.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.courierpro.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}