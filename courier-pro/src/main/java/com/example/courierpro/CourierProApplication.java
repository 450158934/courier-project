package com.example.courierpro;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.courierpro.mapper") // 扫描刚才建的 mapper
public class CourierProApplication {
    public static void main(String[] args) {
        SpringApplication.run(CourierProApplication.class, args);
    }
}