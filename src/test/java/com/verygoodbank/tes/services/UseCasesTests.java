package com.verygoodbank.tes.services;

import com.verygoodbank.tes.domain.TradeDefinition;
import com.verygoodbank.tes.services.enrich.EnrichmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UseCasesTests {

    @Mock
    private EnrichmentService enrichmentService;
    @InjectMocks
    private UseCases useCases;

    @Test
    public void shouldEnrichTrades() {
        var trades = """
                date,product_id,currency,price
                20160101,1,EUR,10.0
                20160101,2,EUR,20.1
                20160101,3,EUR,30.34
                20160101,11,EUR,35.34""";

        var tradesFlux = bufferFrom(trades);
        when(enrichmentService.enrich(any(String.class)))
                .thenAnswer(
                        (a) -> {
                            var definition = new String(a.getArgument(0).toString().getBytes(StandardCharsets.UTF_8));
                            var parts = definition.split(",");

                            try {
                                Long.parseLong(parts[1]);
                            } catch (Exception e) {
                                return Optional.empty();
                            }

                            return Optional.of(TradeDefinition.builder()
                                    .date(parts[0])
                                    .productId(parts[1])
                                    .productName("product_name_" + parts[1])
                                    .currency(parts[2])
                                    .price(parts[3])
                                    .build());
                        }
                );

        var result = useCases.enrich(tradesFlux);

        StepVerifier.create(result)
                .expectNext("date,product_name,currency,price\n")
                .expectNext("20160101,product_name_1,EUR,10.0\n")
                .expectNext("20160101,product_name_2,EUR,20.1\n")
                .expectNext("20160101,product_name_3,EUR,30.34\n")
                .expectNext("20160101,product_name_11,EUR,35.34\n")
                .verifyComplete();

    }

    private Flux<DataBuffer> bufferFrom(String value) {
        var bufferFactory = new DefaultDataBufferFactory();
        var bytes = value.getBytes(StandardCharsets.UTF_8);
        var buffer = bufferFactory.allocateBuffer(bytes.length);
        buffer.write(bytes);
        return Flux.just(buffer);
    }
}
