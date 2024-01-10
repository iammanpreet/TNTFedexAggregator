package com.fedex.service.impl;

import com.fedex.config.ApiBatchConfig;
import com.fedex.enumeration.ApiName;
import com.fedex.exception.ApiException;
import com.fedex.model.AggregationResponse;
import com.fedex.model.ApiRequest;
import com.fedex.service.AggregationService;
import com.fedex.service.ApiFunction;
import com.fedex.service.ApiQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

@Service
public class ApiQueueServiceImpl implements ApiQueueService {
    private static final Logger logger = LoggerFactory.getLogger(ApiQueueServiceImpl.class);

    private final Map<ApiName, Queue<ApiRequest<AggregationResponse>>> apiQueues;
    private final ApiBatchConfig apiBatchConfig;
    private final ExecutorService executorService;

    private final ScheduledExecutorService scheduler;

    private final AggregationService aggregationService;

    public ApiQueueServiceImpl(ApiBatchConfig apiBatchConfig, ExecutorService executorService, AggregationService aggregationService) {
        this.apiBatchConfig = apiBatchConfig;
        this.executorService = executorService;
        this.aggregationService = aggregationService;
        this.apiQueues = new ConcurrentHashMap<>();
        this.scheduler = Executors.newScheduledThreadPool(ApiName.values().length);
        initializeQueues();
        schedulePeriodicTasks();
    }
    /**
     * This method creates the queues for each api client.
     * */
    private void initializeQueues() {
        for (ApiName apiName : ApiName.values()) {
            apiQueues.put(apiName, new ConcurrentLinkedQueue<>());
        }
    }
    /**
     * This method creates a fixed rate scheduler for AS-3 implementation
     * */
    private void schedulePeriodicTasks() {
        for (ApiName apiName : ApiName.values()) {
            scheduler.scheduleAtFixedRate(() -> processApiRequestQueue(apiName, apiQueues.get(apiName), null), 0, 5, TimeUnit.SECONDS);
        }
    }
    /**
     * This method is triggered by the enqueue api, to enqueue the request in the respective queues
     * */
    public Object enqueueRequest(ApiName api, List<String> orderNumbers, ApiFunction<AggregationResponse> apiFunction) {
        return processApiRequests(api, orderNumbers, apiFunction);
    }
    /**
     * This method is used to process the enqueued requests from the respective queues
     * based on batch size or either scheduling the run
     * */
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
            scheduleProcessApiRequestQueue(api, queue, apiFunction);
            return "Request Queued for Batching";
        }

        return future;
    }
    /**
     * This method processes the enqueued requests based on the business logic in the stories
     * */
    public void processApiRequestQueue(ApiName api, Queue<ApiRequest<AggregationResponse>> queue, ApiFunction<AggregationResponse> apiFunction) {
        // Extract order numbers and API function from the queued requests
        List<String> orderNumbers = new ArrayList<>();
        int batchSize = apiBatchConfig.getBatchSize();
        // check if queue size is greater than 0 and oldest item in queue is 5 sec older then trigger request
        if (!queue.isEmpty() && queue.size() < batchSize && Instant.now().isAfter(queue.peek().getTimestamp().plusSeconds(5))) {
            batchSize = queue.size();
        }
        for (int i = 0; i < batchSize && !queue.isEmpty(); i++) {
            ApiRequest<AggregationResponse> apiRequest = queue.poll();
            orderNumbers.addAll(apiRequest.getOrderNumbers());
            if (apiFunction == null) {
                apiFunction = getApiFunction(api);
            }
        }

        // Execute the batched API call
        AggregationResponse response = apiFunction.apply(orderNumbers);
        queue.forEach(request -> request.getFuture().complete(response));
    }

    private void scheduleProcessApiRequestQueue(ApiName api, Queue<ApiRequest<AggregationResponse>> queue, ApiFunction<AggregationResponse> apiFunction) {
        scheduler.schedule(() -> processApiRequestQueue(api, queue, apiFunction), 5, TimeUnit.SECONDS);
    }

    /**
     * This method is used to identify the apifunction based on the apiName
     * */
    private ApiFunction getApiFunction(ApiName api) {
        switch (api) {
            case PRICING: {
                return new PricingApiFunction(aggregationService);
            }
            case SHIPMENTS: {
                return new ShipmentsApiFunction(aggregationService);
            }
            case TRACK: {
                return new TrackApiFunction(aggregationService);
            }
            default: {
                throw new ApiException("Invalid Api Name");
            }
        }
    }
}
