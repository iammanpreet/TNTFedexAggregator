package com.xyzassessment.fedex.config;

import com.xyzassessment.fedex.aspect.AggregationSLAAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Scope;

@Configuration
@EnableAspectJAutoProxy
public class AggregationAspectConfig {

    @Bean
    @Scope("singleton")
    public AggregationSLAAspect slaAspect() {
        return new AggregationSLAAspect();
    }
}