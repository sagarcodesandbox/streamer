package com.mobox.streamerorderprocessingstreamsservice.model;



import java.math.BigDecimal;

public record OrderLineItem(
        String item,
        Integer count,
        BigDecimal amount) {
}
