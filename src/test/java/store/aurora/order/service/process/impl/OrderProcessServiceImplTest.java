package store.aurora.order.service.process.impl;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderProcessServiceImplTest {

    @Test
    void getOrderUuidWithOrderDetailsLength() {
        long exampleSumBookIds = 1024L;

        String returnValue = UUID.randomUUID().toString().replace("-", "") + exampleSumBookIds;

        System.out.println(returnValue);
        assertEquals(36, returnValue.length());
    }
}