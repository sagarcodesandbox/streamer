package com.mobox.streamerordercreationservice.controler;

import com.mobox.streamerordercreationservice.model.RestaurantOrder;
import com.mobox.streamerordercreationservice.service.RestaurantOrderEventProducer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/restaurant-orders")
public class RestaurantOrderController {
    
    private final RestaurantOrderEventProducer restaurantOrderEventProducer;

    public RestaurantOrderController(RestaurantOrderEventProducer restaurantOrderEventProducer) {
        this.restaurantOrderEventProducer = restaurantOrderEventProducer;
    }

    @PostMapping("/create")
    public CompletableFuture<ResponseEntity<String>> createRestaurantOrder(@RequestBody RestaurantOrder order) {
        return restaurantOrderEventProducer.publishRestaurantOrderCreated(order)
                .thenApply(orderCreated -> ResponseEntity.ok().body("Restaurant order created successfully: " + orderCreated.toString()))
                .exceptionally(throwable -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create restaurant order: " + throwable.getMessage()));
    }
}
