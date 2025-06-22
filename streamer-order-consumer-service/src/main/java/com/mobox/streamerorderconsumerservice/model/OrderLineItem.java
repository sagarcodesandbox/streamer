package com.mobox.streamerorderconsumerservice.model;

import java.math.BigDecimal;

public record OrderLineItem(
        String item,
        Integer count,
        BigDecimal amount
) {
}
