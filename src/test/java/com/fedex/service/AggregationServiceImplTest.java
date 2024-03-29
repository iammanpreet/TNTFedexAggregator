package com.fedex.service;

import com.fedex.BaseTest;
import com.fedex.client.PricingApiClient;
import com.fedex.client.ShipmentsApiClient;
import com.fedex.client.TrackApiClient;
import com.fedex.config.ApiClientConfig;
import com.fedex.service.impl.AggregationServiceImpl;
import com.fedex.model.AggregationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AggregationServiceImplTest extends BaseTest {

    @Mock
    private TrackApiClient trackApiClient;

    @Mock
    private ShipmentsApiClient shipmentsApiClient;

    @Mock
    private PricingApiClient pricingApiClient;

    @Mock
    private ApiClientConfig apiClientConfig;

    private ExecutorService executorService = Executors.newFixedThreadPool(3);
    @InjectMocks
    private AggregationServiceImpl aggregationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        aggregationService = new AggregationServiceImpl(
                executorService,
                trackApiClient,
                shipmentsApiClient,
                pricingApiClient,
                apiClientConfig
        );
    }

    @Test
    public void testAggregatedData_Success() {
        List<String> trackOrderNumbers = Collections.singletonList("123");
        List<String> shipmentOrderNumbers = Collections.singletonList("456");
        List<String> pricingCountryCodes = Collections.singletonList("NL");

        when(apiClientConfig.getTrackTimeout()).thenReturn(5l);
        when(apiClientConfig.getPricingTimeout()).thenReturn(5l);
        when(apiClientConfig.getShipmentsTimeout()).thenReturn(5l);

        CompletableFuture<Map<String, String>> trackFuture = CompletableFuture.supplyAsync(() -> {
            return Collections.singletonMap("123", "In Transit");
        });
        CompletableFuture<Map<String, List<String>>> shipmentsFuture = CompletableFuture.supplyAsync(() -> {
            return Collections.singletonMap("456", List.of("Product1", "Product2"));
        });
        CompletableFuture<Map<String, Double>> pricingFuture = CompletableFuture.supplyAsync(() -> {
            return Collections.singletonMap("NL", 25.0);
        });
        when(trackApiClient.getTrackingStatus(anyList())).thenReturn(trackFuture.join());
        when(shipmentsApiClient.getProducts(anyList())).thenReturn(shipmentsFuture.join());
        when(pricingApiClient.getPricing(anyList())).thenReturn(pricingFuture.join());

        AggregationResponse result = aggregationService.getAggregatedData(trackOrderNumbers, shipmentOrderNumbers, pricingCountryCodes);
        assertEquals(trackFuture.join(), result.getTrack());
        assertEquals(shipmentsFuture.join(), result.getShipments());
        assertEquals(pricingFuture.join(), result.getPricing());
    }

    @Test
    public void testAggregatedData_TrackApiTimeout() {
        List<String> trackOrderNumbers = List.of("123");
        List<String> shipmentOrderNumbers = List.of("456");
        List<String> pricingCountryCodes = List.of("US");
        when(apiClientConfig.getTrackTimeout()).thenReturn(1l);
        when(apiClientConfig.getPricingTimeout()).thenReturn(1l);
        when(apiClientConfig.getShipmentsTimeout()).thenReturn(1l);

        when(trackApiClient.getTrackingStatus(anyList())).thenAnswer(invocation -> {
            Thread.sleep(2000);
            return Collections.singletonMap("123", "In Transit");
        });

        CompletableFuture<Map<String, List<String>>> shipmentsFuture = CompletableFuture.completedFuture(Collections.singletonMap("456", List.of("Product1", "Product2")));
        CompletableFuture<Map<String, Double>> pricingFuture = CompletableFuture.completedFuture(Collections.singletonMap("US", 25.0));

        when(shipmentsApiClient.getProducts(anyList())).thenReturn(shipmentsFuture.join());
        when(pricingApiClient.getPricing(anyList())).thenReturn(pricingFuture.join());

        AggregationResponse result = aggregationService.getAggregatedData(trackOrderNumbers, shipmentOrderNumbers, pricingCountryCodes);
        assertEquals(Collections.emptyMap(), result.getTrack());
        assertEquals(shipmentsFuture.join(), result.getShipments());
        assertEquals(pricingFuture.join(), result.getPricing());
    }

    @Test
    public void testAggregatedData_ShipmentApiTimeout() {
        List<String> trackOrderNumbers = List.of("123");
        List<String> shipmentOrderNumbers = List.of("456");
        List<String> pricingCountryCodes = List.of("US");
        when(apiClientConfig.getTrackTimeout()).thenReturn(1l);
        when(apiClientConfig.getPricingTimeout()).thenReturn(1l);
        when(apiClientConfig.getShipmentsTimeout()).thenReturn(1l);
        CompletableFuture<Map<String, String>> trackFuture = CompletableFuture.supplyAsync(() -> {
            return Collections.singletonMap("123", "In Transit");
        });
        when(trackApiClient.getTrackingStatus(anyList())).thenReturn(trackFuture.join());

        CompletableFuture<Map<String, Double>> pricingFuture = CompletableFuture.completedFuture(Collections.singletonMap("US", 25.0));

        when(shipmentsApiClient.getProducts(anyList())).thenAnswer(invocation -> {
            Thread.sleep(2000);
            return Collections.singletonMap("456", List.of("Product1", "Product2"));
        });
        when(pricingApiClient.getPricing(anyList())).thenReturn(pricingFuture.join());

        AggregationResponse result = aggregationService.getAggregatedData(trackOrderNumbers, shipmentOrderNumbers, pricingCountryCodes);
        assertEquals(trackFuture.join(), result.getTrack());
        assertEquals(Collections.emptyMap(), result.getShipments());
        assertEquals(pricingFuture.join(), result.getPricing());
    }

    @Test
    public void testAggregatedData_PricingApiTimeout() {
        List<String> trackOrderNumbers = List.of("123");
        List<String> shipmentOrderNumbers = List.of("456");
        List<String> pricingCountryCodes = List.of("US");
        when(apiClientConfig.getTrackTimeout()).thenReturn(1l);
        when(apiClientConfig.getPricingTimeout()).thenReturn(1l);
        when(apiClientConfig.getShipmentsTimeout()).thenReturn(1l);
        CompletableFuture<Map<String, String>> trackFuture = CompletableFuture.supplyAsync(() -> {
            return Collections.singletonMap("123", "In Transit");
        });
        when(trackApiClient.getTrackingStatus(anyList())).thenReturn(trackFuture.join());

        CompletableFuture<Map<String, List<String>>> shipmentsFuture = CompletableFuture.completedFuture(Collections.singletonMap("456", List.of("Product1", "Product2")));

        when(shipmentsApiClient.getProducts(anyList())).thenReturn(shipmentsFuture.join());
        when(pricingApiClient.getPricing(anyList())).thenAnswer(invocation -> {
            Thread.sleep(2000);
            return Collections.singletonMap("US", 25.0);
        });
        AggregationResponse result = aggregationService.getAggregatedData(trackOrderNumbers, shipmentOrderNumbers, pricingCountryCodes);
        assertEquals(trackFuture.join(), result.getTrack());
        assertEquals(shipmentsFuture.join(), result.getShipments());
        assertEquals(Collections.emptyMap(), result.getPricing());
    }
}


