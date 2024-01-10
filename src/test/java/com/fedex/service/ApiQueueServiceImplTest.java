package com.fedex.service;
import com.fedex.BaseTest;
import com.fedex.config.ApiBatchConfig;
import com.fedex.enumeration.ApiName;
import com.fedex.exception.ApiException;
import com.fedex.model.AggregationResponse;
import com.fedex.service.impl.ApiQueueServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ApiQueueServiceImplTest extends BaseTest {


    @Mock
    private ApiFunction<AggregationResponse> apiFunction;
    @Mock
    private ApiBatchConfig apiBatchConfig;
    @InjectMocks
    private ApiQueueServiceImpl apiQueueService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        apiQueueService = new ApiQueueServiceImpl(apiBatchConfig, executorService);
     }
    @Test
    void testEnqueueRequestWithBatchProcessingWithRequestQueued() {
        List<String> orderNumbers = Arrays.asList("123", "456");
        when(apiBatchConfig.getBatchSize()).thenReturn(5);
        Object result = apiQueueService.enqueueRequest(ApiName.PRICING, orderNumbers, apiFunction);

        assertEquals("Request Queued for Batching", result);
    }
    @Test
    void testEnqueueRequestWithBatchProcessing() {
        List<String> orderNumbers = Arrays.asList("123", "456");
        when(apiBatchConfig.getBatchSize()).thenReturn(1);
        AggregationResponse aggregationResponse = new AggregationResponse();
        Map<String,Double> resultMap = Collections.singletonMap("NL", 25.0);
        aggregationResponse.setPricing(resultMap);
        when(apiFunction.apply(anyList())).thenReturn(aggregationResponse);
        apiQueueService.enqueueRequest(ApiName.PRICING, orderNumbers, apiFunction);
        verify(apiFunction, times(1)).apply(orderNumbers);
    }
}
