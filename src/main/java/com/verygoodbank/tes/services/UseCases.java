package com.verygoodbank.tes.services;

import com.verygoodbank.tes.domain.TradeDefinition;
import com.verygoodbank.tes.services.enrich.EnrichmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.function.Function.identity;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.isBlank;

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
        final var context = new EnrichContext();

        final var header = Flux.fromIterable(List.of(RESPONSE_HEADER));
        final var enrichedTrades = data.map(buf -> extractLines(buf.toString(UTF_8), context))
                .mapNotNull(this::processTrades)
                .flatMapIterable(identity());

        return Flux.concat(header, enrichedTrades);
    }

    private List<String> processTrades(List<String> trades) {
        return trades.stream()
                .parallel()
                .map(enrichmentService::enrich)
                .flatMap(Optional::stream)
                .map(this::toTradeResponse)
                .toList();
    }

    private List<String> extractLines(String rawChunk, EnrichContext context) {
        if (isBlank(rawChunk)) return List.of();

        final var chunk = context.usingIncomplete(rawChunk);
        context.clearIncomplete();

        final var lines = chunk.trim().split("\n", -1);
        final var whole = chunk.endsWith("\n");

        var list = Arrays.stream(lines)
                .skip(!context.isHeaderSkipped() ? 1 : 0)
                .limit(whole ? lines.length : lines.length - 1)
                .filter(Objects::nonNull)
                .toList();

        context.headerSkipped();

        if (!whole) context.appendIncomplete(lines[lines.length - 1]);

        return list;
    }

    private String toTradeResponse(TradeDefinition definition) {
        var productName = defaultIfBlank(definition.getProductName(), "");
        return definition.getDate() + "," + productName + "," + definition.getCurrency() + "," + definition.getPrice() + "\n";
    }

    private final static class EnrichContext {
        private final StringBuilder incomplete = new StringBuilder();
        private final AtomicBoolean headerSkipped = new AtomicBoolean(false);

        public void headerSkipped() {
            headerSkipped.set(true);
        }

        public boolean isHeaderSkipped() {
            return headerSkipped.get();
        }

        public void clearIncomplete() {
            incomplete.setLength(0);
        }

        public void appendIncomplete(String data) {
            incomplete.append(data);
        }

        public String usingIncomplete(String data) {
            if (incomplete.isEmpty()) return data;
            return incomplete.append(data).toString();
        }
    }

}
