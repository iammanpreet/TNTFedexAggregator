package com.xyzassessment.fedex.service;


import com.xyzassessment.fedex.model.AggregationResponse;

import java.util.List;
import java.util.concurrent.TimeoutException;

public interface AggregationService {

    AggregationResponse getAggregatedData(List<String> pricing, List<String> track, List<String> shipments) throws TimeoutException;
}
