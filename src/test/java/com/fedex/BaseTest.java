package com.fedex;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "thread.pool.size=3",
        "clients.pricing.timeout=5",
        "clients.shipments.timeout=5",
        "clients.track.timeout=5",
        "slaEnforcement.defaultThreshold=100",
        "batch.size=5"
})
@ExtendWith(MockitoExtension.class)
public class BaseTest {
    @Test
    void test() {

    }
}
