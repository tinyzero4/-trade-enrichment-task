package com.verygoodbank.tes.adapters.http;

import com.verygoodbank.tes.services.UseCases;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1")
public class TradeEnrichmentEndpoint {

    private final UseCases useCases;

    @PostMapping(value = "/enrich", consumes = "text/csv", produces = "text/csv")
    public Flux<String> enrichTrades(@RequestBody Flux<DataBuffer> trades) {
        return useCases.enrich(trades)
                .doOnError((e) -> log.warn("[http][enrich] {}", e))
                .onErrorResume(e -> Flux.empty());
    }

}
