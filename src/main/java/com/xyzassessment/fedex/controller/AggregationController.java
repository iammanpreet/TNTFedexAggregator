package com.xyzassessment.fedex.controller;

import java.util.List;
import java.util.concurrent.TimeoutException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xyzassessment.fedex.model.AggregationResponse;
import com.xyzassessment.fedex.service.AggregationService;

@RestController
public class AggregationController {

    private AggregationService aggregationService;

    public AggregationController(AggregationService aggregationService) {
        this.aggregationService = aggregationService;
    }

    @GetMapping("/aggregation")
    public AggregationResponse aggregate(@RequestParam(required = false) List<String> pricing,
                                         @RequestParam(required = false) List<String> track, @RequestParam(required = false) List<String> shipments) throws TimeoutException {

        return aggregationService.getAggregatedData(track, shipments, pricing);
    }
}
