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
    /**
     *  This method is used to trigger the GET call to the aggregation api with the respective parameters of the external clients
     * This methods combines the reqponse of the external clients and return Aggregation Response to the caller.
     * */
    @GetMapping("/aggregation")
    public AggregationResponse aggregate(@RequestParam(required = false) List<String> pricing,
                                         @RequestParam(required = false) List<String> track, @RequestParam(required = false) List<String> shipments) throws TimeoutException {

        return aggregationService.getAggregatedData(pricing, track, shipments);
    }
}
