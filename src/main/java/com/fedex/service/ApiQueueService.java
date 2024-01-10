package com.fedex.service;

import com.fedex.enumeration.ApiName;
import com.fedex.model.AggregationResponse;

import java.util.List;

public interface ApiQueueService {
    Object enqueueRequest(ApiName apiName, List<String> orderNumbers, ApiFunction<AggregationResponse> apiFunction);
}
