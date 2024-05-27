package com.verygoodbank.tes.services;

import com.verygoodbank.tes.domain.TradeDefinition;
import com.verygoodbank.tes.services.enrich.ProductNameEnricher;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductNameEnricherTests {

    @Test
    public void shouldLoadProductMappingAndResolveName() {
        var resolver = new ProductNameEnricher(new TradeConfiguration());
        resolver.init();

        assertAll(
                () -> assertEquals("Treasury Bills Domestic", resolver.enrich(ofProductId("1")).getProductName()),
                () -> assertEquals("Corporate Bonds Domestic", resolver.enrich(ofProductId("2")).getProductName()),
                () -> assertEquals("766B_CORP BD", resolver.enrich(ofProductId("10")).getProductName()),
                () -> assertEquals(TradeConfiguration.DEF_MISSING_PRODUCT_NANE, resolver.enrich(ofProductId("1_000_000")).getProductName())
        );
    }

    private TradeDefinition ofProductId(String productId) {
        return TradeDefinition.builder().productId(productId).build();
    }
}
