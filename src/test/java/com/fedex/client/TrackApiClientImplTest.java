package com.fedex.client;

import com.fedex.client.impl.TrackApiClientImpl;
import com.fedex.BaseTest;
import com.fedex.config.ApiClientConfig;
import com.fedex.exception.ApiException;
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

public class TrackApiClientImplTest extends BaseTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ApiClientConfig apiClientConfig;

    @Mock
    private Logger logger;

    @InjectMocks
    private TrackApiClientImpl trackApiClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        trackApiClient = new TrackApiClientImpl(
                restTemplate,
                apiClientConfig
        );
    }

    @Test
    public void testGetTrackingStatus() {
        List<String> trackOrderNumbers = Collections.singletonList("123");

        Map<String, String> expectedResponse = Collections.singletonMap("123", "In Transit");/* Your expected response */
        ;
        ResponseEntity<Map<String, String>> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        Map<String, String> result = trackApiClient.getTrackingStatus(trackOrderNumbers);

        assertEquals(expectedResponse, result);

        verify(restTemplate, times(1)).exchange(
                eq(trackApiClient.buildUrl(trackOrderNumbers)),
                eq(trackApiClient.getHttpMethod()),
                isNull(),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    public void testGetPricingApiException() {
        List<String> trackOrderNumbers = Collections.singletonList("123");
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(ParameterizedTypeReference.class)))
                .thenThrow(new ApiException("Mocked API exception"));

        assertThrows(ApiException.class, () -> trackApiClient.getTrackingStatus(trackOrderNumbers));

        verify(restTemplate, times(1)).exchange(
                anyString(),
                any(HttpMethod.class),
                any(),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    public void testGetPricingApi_ServiceUnavailableException() {
        List<String> trackOrderNumbers = Collections.singletonList("123");

        Map<String, String> expectedResponse = Collections.singletonMap("message", "service unavailable");

        ResponseEntity<Map<String, String>> responseEntity = new ResponseEntity<>(expectedResponse, HttpStatus.SERVICE_UNAVAILABLE);
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        assertThrows(ApiException.class, () -> trackApiClient.getTrackingStatus(trackOrderNumbers));

        verify(restTemplate, times(1)).exchange(
                eq(trackApiClient.buildUrl(trackOrderNumbers)),
                eq(trackApiClient.getHttpMethod()),
                isNull(),
                any(ParameterizedTypeReference.class)
        );

    }
}
