package com.verygoodbank.tes.services;

import com.verygoodbank.tes.domain.TradeDefinition;
import com.verygoodbank.tes.services.enrich.Enricher;
import com.verygoodbank.tes.services.enrich.EnrichmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
public class TradeService implements EnrichmentService {

    private final List<Enricher> enrichers;
    private final DateTimeFormatter dateFormatter;

    public TradeService(List<Enricher> enrichers, TradeConfiguration properties) {
        this.enrichers = enrichers;
        if (isBlank(properties.getDatePattern())) throw new IllegalArgumentException("invalid date pattern");
        dateFormatter = DateTimeFormatter.ofPattern(properties.getDatePattern());
    }

    public Optional<TradeDefinition> enrich(String definition) {
        return parse(definition).flatMap(this::enrich);
    }

    public Optional<TradeDefinition> parse(String definition) {
        if (isBlank(definition)) {
            log.warn("[newTradeData] no definition");
            return Optional.empty();
        }

        var parts = definition.split(",");
        if (!isValidTradeDefinition(parts)) return Optional.empty();

        var date = parts[0];
        var productId = parts[1];
        var currency = parts[2];
        var price = parts[3];

        try {
            Long.parseLong(productId);
        } catch (Exception e) {
            return Optional.empty();
        }

        var tradeDefinition = TradeDefinition.builder()
                .productId(productId)
                .date(date)
                .currency(currency)
                .price(price)
                .build();
        return Optional.of(tradeDefinition);
    }

    public Optional<TradeDefinition> enrich(TradeDefinition tradeDefinition) {
        if (tradeDefinition == null) return Optional.empty();

        for (var enricher : enrichers) {
            try {
                tradeDefinition = enricher.enrich(tradeDefinition);
            } catch (Exception e) {
                log.warn("[enrich] issue {}", tradeDefinition, e);
            }
        }

        return Optional.of(tradeDefinition);
    }

    private boolean isValidTradeDefinition(String[] parts) {
        if (parts.length != 4) {
            log.warn("[parse] invalid definition pattern {}", (Object) parts);
            return false;
        }

        var dateDefinition = parts[0];
        try {
            LocalDate.parse(dateDefinition, dateFormatter);
        } catch (DateTimeParseException e) {
            log.error("[newTradeData] invalid date format");
            return false;
        }

        return true;
    }
}
