package com.emre.ftgo.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.emre.ftgo.order", "com.emre.ftgo.common"})
@EntityScan(basePackages = {"com.emre.ftgo.order", "com.emre.ftgo.common"})
@EnableJpaRepositories(basePackages = {"com.emre.ftgo.order", "com.emre.ftgo.common"})
@EnableScheduling
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
