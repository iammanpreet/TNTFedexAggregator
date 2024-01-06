package com.xyzassessment.fedex.client;

import java.util.List;
import java.util.Map;

public interface ShipmentsApiClient {

	Map<String,List<String>> getProducts(List<String> orderNumbers);

}
