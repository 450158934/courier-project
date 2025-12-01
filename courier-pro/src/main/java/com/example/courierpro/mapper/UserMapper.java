package com.example.courierpro.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.courierpro.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}