package com.ecohome;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication(scanBasePackages = "com.ecohome")
@EnableR2dbcRepositories(basePackages = "com.ecohome.infrastructure.driven.postgres")
public class EcoHomeApplication {
    public static void main(String[] args) {
        SpringApplication.run(EcoHomeApplication.class, args);
    }
}
