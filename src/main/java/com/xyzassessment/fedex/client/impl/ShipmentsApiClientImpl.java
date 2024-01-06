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
import com.xyzassessment.fedex.client.ShipmentsApiClient;

@Component
public class ShipmentsApiClientImpl extends ApiClientTemplate<Map<String, List<String>>> implements ShipmentsApiClient {

    private static final Logger logger = LoggerFactory.getLogger(ShipmentsApiClientImpl.class);

    public ShipmentsApiClientImpl(@Qualifier("commonRestTemplate") RestTemplate restTemplate, ApiClientConfig apiClientConfig) {
        super(restTemplate, apiClientConfig.getShipmentsApiUrl());
    }

    @Override
    protected Logger getLogger() {

        return logger;
    }

    @Override
    protected ParameterizedTypeReference<Map<String, List<String>>> getResponseType() {
        return new ParameterizedTypeReference<Map<String, List<String>>>() {
        };
    }

    @Override
    public Map<String, List<String>> getProducts(List<String> orderNumbers) {
        return callApi(orderNumbers);
    }
}
