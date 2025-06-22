package com.mobox.streamerorderconsumerservice.consumer;

import com.mobox.streamerorderconsumerservice.model.RestaurantOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GeneralOrderConsumer {

    @KafkaListener(topics = "general-Orders-processed", groupId = "general-notification-group")
    public void listenGeneralOrders(RestaurantOrder generalOrder) {
        log.info("General Consumer: Received general order: {}, Order Type: {}, Location: {}, Amount: {}", 
                generalOrder.orderId(),
                generalOrder.orderType(),
                generalOrder.locationId(),
                generalOrder.finalAmount());
        
        // Here, you would implement general order business logic:
        // - Send a notification to general logistics
        // - Update shipping systems
        // - Trigger fulfillment workflows
        // - Update general analytics, etc.
        
        log.debug("Processing general business logic for order: {}", generalOrder.orderId());
    }
}
