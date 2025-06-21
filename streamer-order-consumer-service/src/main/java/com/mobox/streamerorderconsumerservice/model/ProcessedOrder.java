package com.mobox.streamerorderconsumerservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessedOrder {
    private String orderId;
    private String status; // e.g., "NEW", "PENDING_PAYMENT", "COMPLETED", "CANCELLED"
    private String customerId;
    private double originalAmount;
    private double totalAmount; // Enriched value
    private boolean isHighValue; // Enriched value
    private long timestamp;
}