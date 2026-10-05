package com.emre.ftgo.accounting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EntityScan(basePackages = {"com.emre.ftgo.accounting", "com.emre.ftgo.common"})
@EnableJpaRepositories(basePackages = {"com.emre.ftgo.accounting", "com.emre.ftgo.common"})
@EnableScheduling
@SpringBootApplication
public class AccountingApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccountingApplication.class, args);
    }
}
