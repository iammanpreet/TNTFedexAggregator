package com.fedex.client;

import java.util.List;
import java.util.Map;

public interface PricingApiClient {

    Map<String, Double> getPricing(List<String> countries);

}
