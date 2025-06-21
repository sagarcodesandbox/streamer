package com.mobox.streamerordercreationservice.service;


import com.mobox.streamerordercreationservice.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class OrderEventProducer {

    private final KafkaTemplate<String, Order> kafkaTemplate;

    @Value("${app.kafka.topics.orders-raw}")
    private String ordersRawTopic;

    public OrderEventProducer(KafkaTemplate<String, Order> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public CompletableFuture<Order> publishOrderCreated(Order order) {
        // Ensure orderId and timestamp are set if not already
        if (order.getOrderId() == null) {
            order.setOrderId(UUID.randomUUID().toString());
        }
        if (order.getTimestamp() == 0) {
            order.setTimestamp(Instant.now().toEpochMilli());
        }
        order.setStatus("NEW"); // Always publish new orders as "NEW" status

        log.info("Publishing order: {} to topic: {}", order.getOrderId(), ordersRawTopic);
        
        return kafkaTemplate.send(ordersRawTopic, order.getOrderId(), order)
                .thenApply(sendResult -> {
                    log.info("Successfully published Order: {} to topic: {} at offset: {}", 
                            order.getOrderId(), ordersRawTopic, sendResult.getRecordMetadata().offset());
                    return order;
                })
                .exceptionally(throwable -> {
                    log.error("Failed to publish order: {} to topic: {}. Error: {}", 
                            order.getOrderId(), ordersRawTopic, throwable.getMessage());
                    throw new RuntimeException(throwable);
                });

    }
}
