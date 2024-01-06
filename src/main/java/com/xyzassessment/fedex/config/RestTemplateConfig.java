package com.xyzassessment.fedex.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    /**
     * Common Rest Template with default settings
     * Currently there is no need for separate rest templates for each required,|
     * if required, we can use message converters on this commonRestTemplate
     * and separate client configuration for each client
     * Managing separate rest template is a manual overhead to consider for resource available,
     * hence implement only if this is required
     */
    @Bean("commonRestTemplate")
    RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                /**
                 * Using combination of Rest Connection Establish Timeout
                 * And read Timeout after connection established at a common rest template
                 * to avoid SLA breach
                 * In story 1, we need to control at rest template template level
                 * hence, controlling at completable future level in service layer.
                 * If required, the combination of completable future and rest template builder timeouts can be used
                 * */
                //.setConnectTimeout(Duration.ofSeconds(2))
                //.setReadTimeout(Duration.ofSeconds(3))
                .build();

    }
}