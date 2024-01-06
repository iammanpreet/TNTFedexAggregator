package com.xyzassessment.fedex.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class ApiClientConfig {
    @Value("${clients.pricing.api.url}")
    private String pricingApiUrl;
    @Value("${clients.shipments.api.url}")
    private String shipmentsApiUrl;
    @Value("${clients.track.api.url}")
    private String trackApiUrl;

    @Value("${clients.pricing.timeout}")
    private long pricingTimeout;
    @Value("${clients.shipments.timeout}")
    private long shipmentsTimeout;
    @Value("${clients.track.timeout}")
    private long trackTimeout;
}
