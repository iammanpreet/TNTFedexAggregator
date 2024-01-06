package com.fedex.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class ExecutorServiceConfig {

    @Value("${thread.pool.size}")
    private int applicationThreadPoolSize;

    @Bean(destroyMethod = "shutdown")
    @Scope("singleton")
    ExecutorService executorService() {
        return Executors.newFixedThreadPool(applicationThreadPoolSize);
    }
}
