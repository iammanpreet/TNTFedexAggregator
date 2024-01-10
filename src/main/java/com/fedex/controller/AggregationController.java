package com.fedex.controller;

import java.util.List;
import java.util.concurrent.TimeoutException;

import com.fedex.service.AggregationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fedex.model.AggregationResponse;

@RestController
public class AggregationController {

    private final AggregationService aggregationService;

    public AggregationController(AggregationService aggregationService) {
        this.aggregationService = aggregationService;
    }

    @GetMapping("/aggregation")
    public AggregationResponse aggregate(@RequestParam(required = false) List<String> pricing,
                                         @RequestParam(required = false) List<String> track, @RequestParam(required = false) List<String> shipments) throws TimeoutException {

        return aggregationService.getAggregatedData(track, shipments, pricing);
    }
}
