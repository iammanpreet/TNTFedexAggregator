package com.xyzassessment.fedex.client.impl;

import java.util.List;
import java.util.Map;

import com.xyzassessment.fedex.config.ApiClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.xyzassessment.fedex.client.ApiClientTemplate;
import com.xyzassessment.fedex.client.PricingApiClient;

@Component
public class PricingApiClientImpl extends ApiClientTemplate<Map<String, Double>> implements PricingApiClient {

    private static final Logger logger = LoggerFactory.getLogger(PricingApiClientImpl.class);

    public PricingApiClientImpl(@Qualifier("commonRestTemplate") RestTemplate restTemplate, ApiClientConfig apiClientConfig) {
        super(restTemplate, apiClientConfig.getPricingApiUrl());
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected ParameterizedTypeReference<Map<String, Double>> getResponseType() {
        return new ParameterizedTypeReference<Map<String, Double>>() {
        };
    }

    @Override
    public Map<String, Double> getPricing(List<String> countries) {
        return callApi(countries);

    }
}
