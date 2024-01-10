package com.fedex.service.impl;

import com.fedex.config.ApiBatchConfig;
import com.fedex.enumeration.ApiName;
import com.fedex.model.AggregationResponse;
import com.fedex.model.ApiRequest;
import com.fedex.service.ApiFunction;
import com.fedex.service.ApiQueueService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

@Service
public class ApiQueueServiceImpl implements ApiQueueService {

    private final Map<ApiName, Queue<ApiRequest<AggregationResponse>>> apiQueues;
    private final ApiBatchConfig apiBatchConfig;
    private final ExecutorService executorService;

    public ApiQueueServiceImpl(ApiBatchConfig apiBatchConfig, ExecutorService executorService) {
        this.apiBatchConfig = apiBatchConfig;
        this.executorService = executorService;
        this.apiQueues = new ConcurrentHashMap<>();
        initializeQueues();
    }

    private void initializeQueues() {
        for (ApiName apiName : ApiName.values()) {
            apiQueues.put(apiName, new ConcurrentLinkedQueue<>());
        }
    }

    public Object enqueueRequest(ApiName api, List<String> orderNumbers, ApiFunction<AggregationResponse> apiFunction) {
        return processApiRequests(api, orderNumbers, apiFunction);
    }

    private Object processApiRequests(ApiName api, List<String> orderNumbers, ApiFunction<AggregationResponse> apiFunction) {
        CompletableFuture<AggregationResponse> future = new CompletableFuture<>();

        // Get or create the API queue
        Queue<ApiRequest<AggregationResponse>> queue = apiQueues.computeIfAbsent(api, k -> new LinkedList<>());

        // Enqueue the current request with timestamp
        queue.add(new ApiRequest<>(orderNumbers, future));

        // Check if the cap is reached, then process the batch
        if (queue.size() >= apiBatchConfig.getBatchSize()) {
            processApiRequestQueue(api, queue, apiFunction);
        } else {
            return "Request Queued for Batching";
        }

        return future.join();
    }

    private void processApiRequestQueue(ApiName api, Queue<ApiRequest<AggregationResponse>> queue, ApiFunction<AggregationResponse> apiFunction) {
        // Extract order numbers and API function from the queued requests
        List<String> orderNumbers = new ArrayList<>();

        for (int i = 0; i < apiBatchConfig.getBatchSize() && !queue.isEmpty(); i++) {
            ApiRequest<AggregationResponse> apiRequest = queue.poll();
            orderNumbers.addAll(apiRequest.getOrderNumbers());
        }

        // Execute the batched API call
        CompletableFuture.supplyAsync(() -> apiFunction.apply(orderNumbers), executorService)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        // Handle errors
                        queue.forEach(request -> request.getFuture().completeExceptionally(throwable));
                    } else {
                        // Complete futures with API results
                        queue.forEach(request -> request.getFuture().complete(result));
                    }
                });
    }
}
