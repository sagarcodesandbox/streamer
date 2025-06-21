package com.mobox.streamerordercreationservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private String orderId;
    private String status; // e.g., "NEW", "PENDING_PAYMENT", "COMPLETED", "CANCELLED"
    private String customerId;
    private double amount; // Initial amount
    private String[] productIds; // For simplicity, just IDs
    private long timestamp; // Event time
}
