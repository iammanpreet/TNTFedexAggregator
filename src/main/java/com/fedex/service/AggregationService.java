package com.fedex.service;


import com.fedex.model.AggregationResponse;

import java.util.List;

public interface AggregationService {

    AggregationResponse getAggregatedData(List<String> pricing, List<String> track, List<String> shipments);
}
