package com.ccfish.security.springmvc.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Controller;

/**
 * @Author: Ciaos
 * @Date: 2019/11/4 22:10
 */
@Configuration
@ComponentScan(basePackages = "com.ccfish.security.springmvc",
                excludeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Controller.class)})
public class ApplicationConfig {
    // 在此配置除了Controller的其他bean，比如：数据库连接池、事务管理器、业务bean等
}
