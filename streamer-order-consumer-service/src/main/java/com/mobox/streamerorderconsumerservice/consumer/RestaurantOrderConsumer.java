package com.mobox.streamerorderconsumerservice.consumer;

import com.mobox.streamerorderconsumerservice.model.RestaurantOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RestaurantOrderConsumer {

    @KafkaListener(topics = "restaurant-Orders-processed", groupId = "restaurant-notification-group")
    public void listenRestaurantOrders(RestaurantOrder restaurantOrder) {
        log.info("Restaurant Consumer: Received restaurant order: {}, Order Type: {}, Location: {}, Amount: {}", 
                restaurantOrder.orderId(),
                restaurantOrder.orderType(),
                restaurantOrder.locationId(),
                restaurantOrder.finalAmount());
        
        // Here, you would implement restaurant-specific business logic:
        // - Send a notification to the restaurant
        // - Update kitchen display systems
        // - Trigger inventory management
        // - Update restaurant analytics, etc.
        
        log.debug("Processing restaurant business logic for order: {}", restaurantOrder.orderId());
    }
}
