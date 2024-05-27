package com.verygoodbank.tes.services;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Application configuration
 */
@ConfigurationProperties(prefix = "trade")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeConfiguration {

    public static final String DEF_DATE_PATTERN = "yyyyMMdd";
    public static final String DEF_MISSING_PRODUCT_NANE = "Missing Product Name";
    public static final String DEF_PRODUCT_MAPPING_FILE_PATH = "/product.csv";

    private String datePattern = DEF_DATE_PATTERN;
    private String missingProductName = DEF_MISSING_PRODUCT_NANE;
    private String productMappingFilePath = DEF_PRODUCT_MAPPING_FILE_PATH;
}
