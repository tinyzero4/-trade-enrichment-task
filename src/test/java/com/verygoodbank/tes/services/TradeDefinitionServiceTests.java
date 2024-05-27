package com.verygoodbank.tes.services;

import com.verygoodbank.tes.domain.TradeDefinition;
import com.verygoodbank.tes.services.enrich.Enricher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TradeDefinitionServiceTests {

    private Enricher enricher;
    private TradeService service;

    @BeforeEach
    public void init() {
        enricher = Mockito.mock(Enricher.class);
        service = new TradeService(List.of(enricher), new TradeConfiguration());
    }

    @Test
    public void shouldValidateTradeDateStringDefinitionFormat() {
        assertAll(
                () -> assertThat(service.parse(null)).isEmpty(),
                () -> assertThat(service.parse(" ")).isEmpty(),
                () -> assertThat(service.parse("20160101,1,EUR")).isEmpty(),
                () -> assertThat(service.parse("2016010,1,EUR,10.0")).isEmpty()
        );
    }

    @Test
    public void shouldSetDefaultValueForProductName() {
        var tradeData = TradeDefinition.builder().productId("1").date("20160101").currency("EUR").price("10.0").build();
        assertAll(
                () -> assertThat(service.parse("20160101,1,EUR,10.0")).contains(tradeData)
        );
    }

    @Test
    public void shouldEnrichTradeData() {
        var productName = "Treasury Bills Domestic";

        var trade = TradeDefinition.builder().productId("1").date("20160101").currency("EUR").price("10.0").build();
        var enriched = trade.toBuilder().productName(productName).build();

        when(enricher.enrich(Mockito.eq(trade))).thenAnswer((a) -> {
            var _trade = (TradeDefinition) a.getArgument(0);
            _trade.setProductName(productName);
            return _trade;
        });

        assertAll(
                () -> assertThat(service.enrich(trade)).contains(enriched)
        );

        verify(enricher).enrich(trade);
    }
}
