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
import com.fedex.client.TrackApiClient;

@Component
public class TrackApiClientImpl extends ApiClientTemplate<Map<String, String>> implements TrackApiClient {

    private static final Logger logger = LoggerFactory.getLogger(TrackApiClientImpl.class);

    public TrackApiClientImpl(@Qualifier("commonRestTemplate") RestTemplate restTemplate, ApiClientConfig apiClientConfig) {
        super(restTemplate, apiClientConfig.getTrackApiUrl());
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected ParameterizedTypeReference<Map<String, String>> getResponseType() {
        return new ParameterizedTypeReference<Map<String, String>>() {
        };
    }

    @Override
    public Map<String, String> getTrackingStatus(List<String> orderNumbers) {
        return callApi(orderNumbers);
    }
}
