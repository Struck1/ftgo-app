package com.emre.ftgo.kitchen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.emre.ftgo.kitchen", "com.emre.ftgo.common"})
@EntityScan(basePackages = {"com.emre.ftgo.kitchen", "com.emre.ftgo.common"})
@EnableJpaRepositories(basePackages = {"com.emre.ftgo.kitchen", "com.emre.ftgo.common"})
public class KitchenApplication {
    public static void main(String[] args) {
        SpringApplication.run(KitchenApplication.class, args);
    }
}
