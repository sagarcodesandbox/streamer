package com.mobox.streamerordercreationservice.service;

import com.mobox.streamerordercreationservice.model.RestaurantOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class RestaurantOrderEventProducer {

    private final KafkaTemplate<String, RestaurantOrder> restaurantKafkaTemplate;

    @Value("${app.kafka.topics.restaurant-orders-raw:restaurant-orders-raw}")
    private String restaurantOrdersRawTopic;

    public RestaurantOrderEventProducer(KafkaTemplate<String, RestaurantOrder> restaurantKafkaTemplate) {
        this.restaurantKafkaTemplate = restaurantKafkaTemplate;
    }

    public CompletableFuture<RestaurantOrder> publishRestaurantOrderCreated(RestaurantOrder order) {
        // Ensure orderId and timestamp are set if not already
        final RestaurantOrder finalOrder;
        if (order.orderId() == null) {
            finalOrder = new RestaurantOrder(
                new Random().nextInt(100000),
                order.locationId(),
                order.finalAmount(),
                order.orderType(),
                order.orderLineItems(),
                order.orderedDateTime() != null ? order.orderedDateTime() : LocalDateTime.now()
            );
        } else {
            finalOrder = order;
        }

        log.info("Publishing restaurant order: {} to topic: {}", finalOrder.orderId(), restaurantOrdersRawTopic);
        
        return restaurantKafkaTemplate.send(restaurantOrdersRawTopic, finalOrder.orderId().toString(), finalOrder)
                .thenApply(sendResult -> {
                    log.info("Successfully published Restaurant Order: {} to topic: {} at offset: {}", 
                            finalOrder.orderId(), restaurantOrdersRawTopic, sendResult.getRecordMetadata().offset());
                    return finalOrder;
                })
                .exceptionally(throwable -> {
                    log.error("Failed to publish restaurant order: {} to topic: {}. Error: {}", 
                            finalOrder.orderId(), restaurantOrdersRawTopic, throwable.getMessage());
                    throw new RuntimeException(throwable);
                });
    }
}
