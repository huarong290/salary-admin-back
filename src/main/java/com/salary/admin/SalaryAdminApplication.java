package com.salary.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 项目启动类
 */
@SpringBootApplication
// 开启全局异步支持
@EnableAsync
public class SalaryAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalaryAdminApplication.class,args);
    }
}
