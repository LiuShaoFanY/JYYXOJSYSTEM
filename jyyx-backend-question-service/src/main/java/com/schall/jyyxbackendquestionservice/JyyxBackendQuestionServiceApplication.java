package com.schall.jyyxbackendquestionservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.schall.jyyxbackendquestionservice.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.schall")
@EnableFeignClients(basePackages = "com.schall.jyyxbackendserviceclient.service")
public class JyyxBackendQuestionServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(JyyxBackendQuestionServiceApplication.class, args);
    }

}
