package com.verygoodbank.tes.services;

import com.verygoodbank.tes.domain.TradeDefinition;
import com.verygoodbank.tes.services.enrich.EnrichmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

/**
 * Component responsible for handling application uses from adapters.
 */
@RequiredArgsConstructor
@Component
public class UseCases {

    private static final String RESPONSE_HEADER = "date,product_name,currency,price\n";

    private final EnrichmentService enrichmentService;

    /**
     * Handler for enrich command
     *
     * @param data - stream of chunk of trade data
     * @return - stream of enriched trade data
     */
    public Flux<String> enrich(Flux<DataBuffer> data) {
        final var header = Flux.fromIterable(List.of(RESPONSE_HEADER));
        final var enrichedTrades = extractLinesStreaming(data)
                .mapNotNull(t -> enrichmentService.enrich(t)
                        .map(this::toTradeResponse)
                        .orElse(null)
                );

        return Flux.concat(header, enrichedTrades);
    }

    private Flux<String> extractLinesStreaming(Flux<DataBuffer> data) {
        return Flux.create(sink -> {
            var accumulator = new StringBuilder();

            data.subscribe(
                    dataBuffer -> {
                        var content = dataBuffer.toString(UTF_8);
                        DataBufferUtils.release(dataBuffer);
                        for (char c : content.toCharArray()) {
                            if (c == '\n') {
                                sink.next(accumulator.toString());
                                accumulator.setLength(0);
                            } else {
                                accumulator.append(c);
                            }
                        }
                    },
                    sink::error,
                    () -> {
                        if (!accumulator.isEmpty()) sink.next(accumulator.toString());
                        sink.complete();
                    }
            );
        });
    }

    private String toTradeResponse(TradeDefinition definition) {
        var productName = defaultIfBlank(definition.getProductName(), "");
        return definition.getDate() + "," + productName + "," + definition.getCurrency() + "," + definition.getPrice() + "\n";
    }

}
