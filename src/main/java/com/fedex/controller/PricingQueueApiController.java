package com.fedex.controller;

import com.fedex.enumeration.ApiName;
import com.fedex.model.AggregationResponse;
import com.fedex.service.ApiFunction;
import com.fedex.service.ApiQueueService;
import com.fedex.service.impl.PricingApiFunction;
import com.fedex.service.impl.TrackApiFunction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PricingQueueApiController {
    private final ApiFunction apiFunction;
    private final ApiQueueService apiQueueService;

    public PricingQueueApiController(ApiQueueService apiQueueService, PricingApiFunction pricingApiFunction) {
        this.apiFunction = pricingApiFunction;
        this.apiQueueService = apiQueueService;
    }

    @PostMapping("/api/pricing/request")
    public ResponseEntity<Object> queuePricingApiRequest(@RequestBody List<String> orderNumbers) {
        return ResponseEntity.ok().body(apiQueueService.enqueueRequest(ApiName.PRICING, orderNumbers, apiFunction));
        //return ResponseEntity.accepted().build();
    }
}
