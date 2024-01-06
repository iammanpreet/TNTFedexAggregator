package com.xyzassessment.fedex.client;

import java.util.List;
import java.util.Map;

public interface TrackApiClient {

	Map<String,String> getTrackingStatus(List<String> orderNumbers);

}
