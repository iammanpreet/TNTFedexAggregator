package com.xyzassessment.fedex.client;

import com.xyzassessment.fedex.BaseTest;
import com.xyzassessment.fedex.client.impl.ShipmentsApiClientImpl;
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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

public class ShipmentsApiClientImplTest extends BaseTest {
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ApiClientConfig apiClientConfig;

    @Mock
    private Logger logger;

    @InjectMocks
    private ShipmentsApiClientImpl shipmentsApiClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        shipmentsApiClient = new ShipmentsApiClientImpl(
                restTemplate,
                apiClientConfig
        );
    }

    @Test
    public void testGetProducts() {
        List<String> shipmentOrderNumbers = Collections.singletonList("456");

        Map<String, List<String>> expectedResponse = Collections.singletonMap("456", List.of("Product1", "Product2"));
        ResponseEntity<Map<String, List<String>>> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        Map<String, List<String>> result = shipmentsApiClient.getProducts(shipmentOrderNumbers);

        assertEquals(expectedResponse, result);

        verify(restTemplate, times(1)).exchange(
                eq(shipmentsApiClient.buildUrl(shipmentOrderNumbers)),
                eq(shipmentsApiClient.getHttpMethod()),
                isNull(),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    public void testGetProductsApiException() {
        List<String> shipmentOrderNumbers = Collections.singletonList("456");

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(ParameterizedTypeReference.class)))
                .thenThrow(new ApiException("Mocked API exception"));

        assertThrows(ApiException.class, () -> shipmentsApiClient.getProducts(shipmentOrderNumbers));

        verify(restTemplate, times(1)).exchange(
                anyString(),
                any(HttpMethod.class),
                any(),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    public void testGetPricingApi_ServiceUnavailableException() {
        List<String> shipmentOrderNumbers = Collections.singletonList("456");

        Map<String, String> expectedResponse = Collections.singletonMap("message", "service unavailable");

        ResponseEntity<Map<String, String>> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.SERVICE_UNAVAILABLE);
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        assertThrows(ApiException.class, () -> shipmentsApiClient.getProducts(shipmentOrderNumbers));

        verify(restTemplate, times(1)).exchange(
                eq(shipmentsApiClient.buildUrl(shipmentOrderNumbers)),
                eq(shipmentsApiClient.getHttpMethod()),
                isNull(),
                any(ParameterizedTypeReference.class)
        );
    }
}

