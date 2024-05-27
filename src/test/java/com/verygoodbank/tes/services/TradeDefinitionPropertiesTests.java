package com.verygoodbank.tes.services;

import org.junit.jupiter.api.Test;

import static com.verygoodbank.tes.services.TradeConfiguration.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TradeDefinitionPropertiesTests {

    @Test
    public void testDefaultValues() {
        var props = new TradeConfiguration();
        assertAll(
                () -> assertEquals(DEF_DATE_PATTERN, props.getDatePattern()),
                () -> assertEquals(DEF_MISSING_PRODUCT_NANE, props.getMissingProductName()),
                () -> assertEquals(DEF_PRODUCT_MAPPING_FILE_PATH, props.getProductMappingFilePath())
        );
    }
}
