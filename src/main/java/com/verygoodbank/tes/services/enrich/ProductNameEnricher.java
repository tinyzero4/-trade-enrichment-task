package com.verygoodbank.tes.services.enrich;

import com.verygoodbank.tes.domain.TradeDefinition;
import com.verygoodbank.tes.services.TradeConfiguration;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Product name enricher.
 * Caches productId->productName mapping for faster lookups
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class ProductNameEnricher implements Enricher {

    private static final String CSV_FIELD_PRODUCT_ID = "product_id";
    private static final String CSV_FIELD_PRODUCT_NAME = "product_name";

    private final TradeConfiguration properties;

    private volatile Map<String, String> productNameById;

    @PostConstruct
    public void init() {
        loadProductMapping();
    }

    @Override
    public TradeDefinition enrich(TradeDefinition tradeDefinition) {
        var name = productNameById.getOrDefault(tradeDefinition.getProductId(), properties.getMissingProductName());
        if (isNotBlank(name)) tradeDefinition.setProductName(name);
        return tradeDefinition;
    }

    private void loadProductMapping() {
        try {
            var stream = ProductNameEnricher.class.getResourceAsStream(properties.getProductMappingFilePath());
            if (stream == null) {
                log.error("[product-mapping] no product mapping file exists");
                return;
            }

            var format = CSVFormat.DEFAULT.builder()
                    .setHeader(CSV_FIELD_PRODUCT_ID, CSV_FIELD_PRODUCT_NAME)
                    .setSkipHeaderRecord(true)
                    .build();

            productNameById = format.parse(new BufferedReader(new InputStreamReader(stream)))
                    .stream()
                    .map(r -> {
                        var productId = r.get(CSV_FIELD_PRODUCT_ID);
                        var productName = r.get(CSV_FIELD_PRODUCT_NAME);
                        if (isBlank(productId) || isBlank(productName)) return null;
                        return new AbstractMap.SimpleImmutableEntry<>(productId, productName);
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } catch (Exception e) {
            log.error("[product-mapping] issue while building product mapping", e);
        }
    }
}
