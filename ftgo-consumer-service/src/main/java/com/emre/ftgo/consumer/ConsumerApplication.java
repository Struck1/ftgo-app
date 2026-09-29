package com.emre.ftgo.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.emre.ftgo.common", "com.emre.ftgo.consumer"})
@EntityScan(basePackages = {"com.emre.ftgo.consumer", "com.emre.ftgo.common"})
@EnableJpaRepositories(basePackages = {"com.emre.ftgo.consumer", "com.emre.ftgo.common"})
@EnableScheduling
public class ConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }
}
