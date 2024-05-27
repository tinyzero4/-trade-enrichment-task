package com.verygoodbank.tes.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TradeDefinitionTests {

    @Test
    public void shouldUpdateProductName() {
        var trade = TradeDefinition.builder()
                .productId("p1")
                .date("20000101")
                .currency("USD")
                .price("100")
                .build();
        trade.setProductName("p1Name");
        assertEquals("p1Name", trade.getProductName());
    }
}
