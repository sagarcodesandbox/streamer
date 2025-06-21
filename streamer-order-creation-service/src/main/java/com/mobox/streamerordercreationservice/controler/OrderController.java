package com.mobox.streamerordercreationservice.controler;


import com.mobox.streamerordercreationservice.model.Order;
import com.mobox.streamerordercreationservice.service.OrderEventProducer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderEventProducer orderEventProducer;

    public OrderController(OrderEventProducer orderEventProducer) {
        this.orderEventProducer = orderEventProducer;
    }

    @PostMapping("/create")
    public CompletableFuture<ResponseEntity<String>> createOrder(@RequestBody Order order) {
        return orderEventProducer.publishOrderCreated(order)
                .thenApply(orderCreated -> ResponseEntity.ok().body("Order created successfully: " + orderCreated.toString()))
                .exceptionally(throwable -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create order: " + throwable.getMessage()));

    }
}
