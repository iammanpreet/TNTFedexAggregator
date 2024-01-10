package com.fedex.controller;

import com.fedex.enumeration.ApiName;
import com.fedex.service.ApiFunction;
import com.fedex.service.ApiQueueService;
import com.fedex.service.impl.TrackApiFunction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TrackQueueApiController {
    private final ApiFunction apiFunction;
    private final ApiQueueService apiQueueService;

    public TrackQueueApiController(ApiQueueService apiQueueService, TrackApiFunction trackApiFunction) {
        this.apiFunction = trackApiFunction;
        this.apiQueueService = apiQueueService;
    }
    /**
     *  This method is used to trigger the POST call to the Track api with the respective parameters of the Track clients
     * This methods enqueues the request for the Track queue, and acknowledge with the response.
     * */
    @PostMapping("/api/track/request")
    public ResponseEntity<Object> queueTrackApiRequest(@RequestBody List<String> orderNumbers) {
        return ResponseEntity.ok().body(apiQueueService.enqueueRequest(ApiName.TRACK, orderNumbers, apiFunction));
    }
}
