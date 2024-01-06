package com.xyzassessment.fedex.client;

import com.xyzassessment.fedex.BaseTest;
import com.xyzassessment.fedex.client.impl.PricingApiClientImpl;
import com.xyzassessment.fedex.config.ApiClientConfig;
import com.xyzassessment.fedex.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PricingApiClientImplTest extends BaseTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ApiClientConfig apiClientConfig;

    @Mock
    private Logger logger;

    @InjectMocks
    private PricingApiClientImpl pricingApiClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        pricingApiClient = new PricingApiClientImpl(
                restTemplate,
                apiClientConfig
        );
    }

    @Test
    public void testGetPricing() {
        List<String> countries = Arrays.asList("US", "CA");

        Map<String, Double> expectedResponse = Collections.singletonMap("NL", 25.0);/* Your expected response */
        ;
        ResponseEntity<Map<String, Double>> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        Map<String, Double> result = pricingApiClient.getPricing(countries);

        assertEquals(expectedResponse, result);

        verify(restTemplate, times(1)).exchange(
                eq(pricingApiClient.buildUrl(countries)),
                eq(pricingApiClient.getHttpMethod()),
                isNull(),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    public void testGetPricingApiException() {
        List<String> countries = Arrays.asList("US", "CA");
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(ParameterizedTypeReference.class)))
                .thenThrow(new ApiException("Mocked API exception"));

        assertThrows(ApiException.class, () -> pricingApiClient.getPricing(countries));

        verify(restTemplate, times(1)).exchange(
                anyString(),
                any(HttpMethod.class),
                any(),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    public void testGetPricingApi_ServiceUnavailableException() {
        List<String> countries = Arrays.asList("US", "CA");

        Map<String, String> expectedResponse = Collections.singletonMap("message", "service unavailable");

        ResponseEntity<Map<String, String>> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.SERVICE_UNAVAILABLE);
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        assertThrows(ApiException.class, () -> pricingApiClient.getPricing(countries));

        verify(restTemplate, times(1)).exchange(
                eq(pricingApiClient.buildUrl(countries)),
                eq(pricingApiClient.getHttpMethod()),
                isNull(),
                any(ParameterizedTypeReference.class)
        );
    }
}

