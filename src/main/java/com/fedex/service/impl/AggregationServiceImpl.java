package com.fedex.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.fedex.client.PricingApiClient;
import com.fedex.client.ShipmentsApiClient;
import com.fedex.client.TrackApiClient;
import com.fedex.service.AggregationService;
import com.fedex.annotations.SlaEnforcement;
import com.fedex.config.ApiClientConfig;
import com.fedex.exception.AggregationTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fedex.logging.LoggerUtils;
import com.fedex.model.AggregationResponse;

@Service
public class AggregationServiceImpl implements AggregationService {

    private static final Logger logger = LoggerFactory.getLogger(AggregationServiceImpl.class);

    private final ExecutorService executorService;
    private final TrackApiClient trackApiClient;
    private final ShipmentsApiClient shipmentsApiClient;
    private final PricingApiClient pricingApiClient;
    private final ApiClientConfig apiClientConfig;

    public AggregationServiceImpl(ExecutorService executorService, TrackApiClient trackApiClient, ShipmentsApiClient shipmentsApiClient, PricingApiClient pricingApiClient, ApiClientConfig apiClientConfig) {
        this.executorService = executorService;
        this.trackApiClient = trackApiClient;
        this.shipmentsApiClient = shipmentsApiClient;
        this.pricingApiClient = pricingApiClient;
        this.apiClientConfig = apiClientConfig;
    }

    @SlaEnforcement(threshold = 10000)
    public AggregationResponse getAggregatedData(List<String> pricingCountryCodes, List<String> trackOrderNumbers,
                                                 List<String> shipmentOrderNumbers) {
        AggregationResponse response = new AggregationResponse();

        CompletableFuture<Map<String, String>> trackFuture = getTrackCompletableFuture(trackOrderNumbers);

        CompletableFuture<Map<String, List<String>>> shipmentsFuture = getShipmentsCompletableFuture(shipmentOrderNumbers);

        CompletableFuture<Map<String, Double>> pricingFuture = getPricingCompletableFuture(pricingCountryCodes);

        try {
            logger.info("Waiting for trackFuture");
            response.setTrack(trackFuture.join());

            logger.info("Waiting for shipmentsFuture");
            response.setShipments(shipmentsFuture.join());

            logger.info("Waiting for pricingFuture");
            response.setPricing(pricingFuture.join());
        } catch (AggregationTimeoutException aggregationTimeoutException) {
            LoggerUtils.logError(logger, "Aggregation Service timed out", aggregationTimeoutException);
            throw aggregationTimeoutException;
        } catch (Exception e) {
            LoggerUtils.logError(logger, "Exception while fetching aggregated details.", e);
            throw e;
        }
        return response;
    }

    private CompletableFuture<Map<String, String>> getTrackCompletableFuture(List<String> trackOrderNumbers) {
        return (trackOrderNumbers != null && !trackOrderNumbers.isEmpty()) ?
                CompletableFuture
                        .supplyAsync(() -> {
                            return trackApiClient.getTrackingStatus(trackOrderNumbers);
                        }, executorService)
                        .completeOnTimeout(Collections.emptyMap(), apiClientConfig.getTrackTimeout(), TimeUnit.SECONDS)
                        .exceptionally(ex -> {
                            return Collections.emptyMap();
                        }) :
                CompletableFuture.completedFuture(Collections.emptyMap());
    }

    private CompletableFuture<Map<String, List<String>>> getShipmentsCompletableFuture(List<String> shipmentOrderNumbers) {
        return (shipmentOrderNumbers != null && !shipmentOrderNumbers.isEmpty()) ?
                CompletableFuture
                        .supplyAsync(() -> shipmentsApiClient.getProducts(shipmentOrderNumbers), executorService)
                        .completeOnTimeout(Collections.emptyMap(), apiClientConfig.getShipmentsTimeout(), TimeUnit.SECONDS)
                        .exceptionally(ex -> {
                            LoggerUtils.logError(logger, "Exception while fetching shipments details.", ex);
                            return Collections.emptyMap();
                        }) : CompletableFuture.completedFuture(Collections.emptyMap());
    }

    private CompletableFuture<Map<String, Double>> getPricingCompletableFuture(List<String> pricingCountryCodes) {
        return (pricingCountryCodes != null && !pricingCountryCodes.isEmpty()) ?
                CompletableFuture
                        .supplyAsync(() -> pricingApiClient.getPricing(pricingCountryCodes), executorService)
                        .completeOnTimeout(Collections.emptyMap(), apiClientConfig.getPricingTimeout(), TimeUnit.SECONDS)
                        .exceptionally(ex -> {
                            LoggerUtils.logError(logger, "Exception while fetching pricing details.", ex);
                            return Collections.emptyMap();
                        }) :
                CompletableFuture.completedFuture(Collections.emptyMap());
    }
}
