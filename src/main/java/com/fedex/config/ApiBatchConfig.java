package com.fedex.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class ApiBatchConfig {
    @Value("${batch.size}")
    private int batchSize;
}
