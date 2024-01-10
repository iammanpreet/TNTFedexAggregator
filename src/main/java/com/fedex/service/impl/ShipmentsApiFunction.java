package com.fedex.service.impl;

import com.fedex.model.AggregationResponse;
import com.fedex.service.AggregationService;
import com.fedex.service.ApiFunction;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ShipmentsApiFunction implements ApiFunction<AggregationResponse> {

    private final AggregationService aggregationService;

    public ShipmentsApiFunction(AggregationService aggregationService) {
        this.aggregationService = aggregationService;
    }
    /**
     * This method defines the api funtion to execute by enqueuing the shipments api call
     * */
    @Override
    public AggregationResponse apply(List<String> orderNumbers) {
        return aggregationService.getAggregatedData(Collections.emptyList(), Collections.emptyList(), orderNumbers);
    }
}
