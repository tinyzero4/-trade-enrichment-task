package com.verygoodbank.tes.adapters.http;

import com.verygoodbank.tes.services.UseCases;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.TEXT_PLAIN;

@ExtendWith(SpringExtension.class)
@WebFluxTest(TradeEnrichmentEndpoint.class)
public class TradeEnrichmentEndpointTests {

    @MockBean
    private UseCases useCases;

    @Autowired
    private WebTestClient httpClient;

    @Test
    public void enrich_success() {
        var trades = """
                date,product_id,currency,price
                20160101,1,EUR,10.0
                20160101,2,EUR,20.1
                20160101,3,EUR,30.34
                20160101,11,EUR,35.34""";

        var trade = "1,2,3";
        var result = Flux.fromIterable(List.of(trade));
        when(useCases.enrich(any())).thenReturn(result);

        var response = httpClient.post()
                .uri("/api/v1/enrich")
                .contentType(new MediaType("text", "csv"))
                .body(BodyInserters.fromValue(trades))
                .accept(new MediaType("text", "csv"))
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(String.class)
                .getResponseBody()
                .blockFirst();

        assertEquals(trade, response);
    }

}
