package com.fedex.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Data
@Getter
@Setter
public class ApiRequest<T> {
    private final List<String> orderNumbers;
    private final CompletableFuture<AggregationResponse> future;
    private final Instant timestamp;

    public ApiRequest(List<String> orderNumbers, CompletableFuture<AggregationResponse> future) {
        this.orderNumbers = orderNumbers;
        this.future = future;
        this.timestamp = Instant.now();
    }

    public CompletableFuture<AggregationResponse> getFuture() {
        return future;
    }

}