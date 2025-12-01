package com.example.courierpro.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("sys_order")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String orderNo;    // 订单号
    private Long customerId;   // 发起人ID
    private Long runnerId;     // 接单人ID
    private String pickupAddr; // 取件地址
    private String pickupCode; // 取件码
    private String destAddr;   // 送达地址
    private BigDecimal price;  // 价格
    private Integer status;    // 0待接单, 1配送中...
    private LocalDateTime createTime;

    // 👇👇👇 之前缺的就是这两行，补上就好了！
    private Integer rating;    // 评分
    private String comment;    // 评价
}