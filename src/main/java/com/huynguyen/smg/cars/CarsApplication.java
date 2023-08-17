package com.huynguyen.smg.cars;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "com.huynguyen.smg.cars.repository")
@EnableCaching
@Slf4j
public class CarsApplication {
    public static void main(String[] args) {
        SpringApplication.run(CarsApplication.class, args);
    }

}
