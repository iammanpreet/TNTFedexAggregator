package com.fedex.model;

import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Data
public class AggregationResponse {
    private Map<String, Double> pricing;
    private Map<String, String> track;
    private Map<String, List<String>> shipments;
}
