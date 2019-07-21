package com.amway.acti;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@MapperScan("com.amway.acti.dao")
@ComponentScan("com.amway.acti")
@EnableCaching
@EnableTransactionManagement
@EnableSwagger2
public class CloudschoolApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudschoolApplication.class, args);
    }
}
