package com.mobox.streamerorderconsumerservice.consumer;

import com.mobox.streamerorderconsumerservice.model.ProcessedOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProcessedOrderConsumer {

    @KafkaListener(topics = "${app.kafka.topics.orders-processed}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenProcessedOrders(ProcessedOrder processedOrder) {
        log.info("Consumer: Received processed order: {}, Total: {}, High Value: {}, Status: {}", 
                processedOrder.getOrderId(),
                processedOrder.getTotalAmount(),
                processedOrder.isHighValue(),
                processedOrder.getStatus());
        
        // Here, you would implement actual business logic:
        // - Send a notification to the customer
        // - Update a fulfillment system
        // - Ingest into a data warehouse/reporting system
        // - Trigger payment processing, etc.
        
        log.debug("Processing business logic for order: {}", processedOrder.getOrderId());
    }
}
