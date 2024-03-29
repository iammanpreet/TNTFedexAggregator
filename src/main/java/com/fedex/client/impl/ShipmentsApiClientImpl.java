package com.fedex.client.impl;

import java.util.List;
import java.util.Map;

import com.fedex.config.ApiClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fedex.client.ApiClientTemplate;
import com.fedex.client.ShipmentsApiClient;

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
    /**
     * This method defines the response type of shipments client
     * */
    @Override
    protected ParameterizedTypeReference<Map<String, List<String>>> getResponseType() {
        return new ParameterizedTypeReference<Map<String, List<String>>>() {
        };
    }
    /**
     * This method is to invoke the shipments api via the template
     * */
    @Override
    public Map<String, List<String>> getProducts(List<String> orderNumbers) {
        return callApi(orderNumbers);
    }
}
