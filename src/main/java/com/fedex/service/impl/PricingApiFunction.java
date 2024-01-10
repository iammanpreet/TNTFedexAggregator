package com.fedex.service.impl;

import com.fedex.model.AggregationResponse;
import com.fedex.service.AggregationService;
import com.fedex.service.ApiFunction;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PricingApiFunction implements ApiFunction<AggregationResponse> {

    private final AggregationService aggregationService;

    public PricingApiFunction(AggregationService aggregationService) {
        this.aggregationService = aggregationService;
    }

    @Override
    public AggregationResponse apply(List<String> orderNumbers) {
        return aggregationService.getAggregatedData(orderNumbers, Collections.emptyList(), Collections.emptyList());
    }
}
