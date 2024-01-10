package com.fedex.service.impl;

import com.fedex.model.AggregationResponse;
import com.fedex.service.AggregationService;
import com.fedex.service.ApiFunction;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class TrackApiFunction implements ApiFunction<AggregationResponse> {

    private final AggregationService aggregationService;

    public TrackApiFunction(AggregationService aggregationService) {
        this.aggregationService = aggregationService;
    }
    /**
     * This method defines the api funtion to execute by enqueuing the track api call
     * */
    @Override
    public AggregationResponse apply(List<String> orderNumbers) {
        return aggregationService.getAggregatedData(Collections.emptyList(), orderNumbers, Collections.emptyList());
    }
}
