package com.campus.democourier_new.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.democourier_new.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}