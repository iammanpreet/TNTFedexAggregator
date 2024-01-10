package com.fedex.controller;

import com.fedex.enumeration.ApiName;
import com.fedex.service.ApiFunction;
import com.fedex.service.ApiQueueService;
import com.fedex.service.impl.ShipmentsApiFunction;
import com.fedex.service.impl.TrackApiFunction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ShipmentsQueueApiController {
    private final ApiFunction apiFunction;
    private final ApiQueueService apiQueueService;

    public ShipmentsQueueApiController(ApiQueueService apiQueueService, ShipmentsApiFunction shipmentsApiFunction) {
        this.apiFunction = shipmentsApiFunction;
        this.apiQueueService = apiQueueService;
    }
    /**
     * This method is used to trigger the POST call to the Shipments api with the respective parameters of the shipments clients
     * This methods enqueues the request for the shipments queue, and acknowledge with the response.
     * */
    @PostMapping("/api/shipments/request")
    public ResponseEntity<Object> queueShipmentsApiRequest(@RequestBody List<String> orderNumbers) {
        return ResponseEntity.ok().body(apiQueueService.enqueueRequest(ApiName.SHIPMENTS, orderNumbers, apiFunction));
    }
}
