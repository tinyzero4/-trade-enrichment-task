package com.verygoodbank.tes;

import com.verygoodbank.tes.services.TradeConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({TradeConfiguration.class})
@SpringBootApplication
public class TradeEnrichmentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradeEnrichmentServiceApplication.class, args);
    }

}
