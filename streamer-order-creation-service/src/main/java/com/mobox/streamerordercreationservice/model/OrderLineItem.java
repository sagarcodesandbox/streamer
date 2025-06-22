package com.mobox.streamerordercreationservice.model;

import java.math.BigDecimal;

public record OrderLineItem(
        String item,
        Integer count,
        BigDecimal amount
) {
}
