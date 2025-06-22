package com.mobox.streamerorderprocessingstreamsservice.model;



import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record RestaurantOrder(Integer orderId,
                    String locationId,
                    BigDecimal finalAmount,
                    OrderType orderType,
                    List<OrderLineItem> orderLineItems,
                    LocalDateTime orderedDateTime) {
}
