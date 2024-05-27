package com.verygoodbank.tes.domain;

import lombok.Builder;
import lombok.Data;

@Builder(toBuilder = true)
@Data
public final class TradeDefinition {

    private final String date;
    private final String productId;
    private final String currency;
    private final String price;
    private volatile String productName;

}
