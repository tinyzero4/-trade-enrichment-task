package com.verygoodbank.tes.services.enrich;

import com.verygoodbank.tes.domain.TradeDefinition;

/**
 * Represents a component to resolve product name by product id
 */
public interface Enricher {

    /**
     * Resolves name by product id.
     *
     * @param tradeDefinition - definition of a trade
     * @return enriched trade definition
     */
    TradeDefinition enrich(TradeDefinition tradeDefinition);
}
