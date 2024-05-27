package com.verygoodbank.tes.services.enrich;

import com.verygoodbank.tes.domain.TradeDefinition;

import java.util.Optional;

/**
 * Represents a component to enrich trade data
 */
public interface EnrichmentService {

    /**
     * Resolves trade definition from string representation and does enrichment of it.
     *
     * @param tradeDefinition - string representation of trade definition
     * @return - opt of {@link TradeDefinition}
     */
    Optional<TradeDefinition> enrich(String tradeDefinition);

}
