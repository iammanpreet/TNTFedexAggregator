package com.fedex.service;

import com.fedex.model.AggregationResponse;

import java.util.List;

@FunctionalInterface
public interface ApiFunction<T> {
    AggregationResponse apply(List<String> orderNumbers);
}
