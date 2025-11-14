package com.atguigu.lease;

import com.atguigu.lease.common.minio.MinioConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class AppWebApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(AppWebApplication.class, args);
    }
}


