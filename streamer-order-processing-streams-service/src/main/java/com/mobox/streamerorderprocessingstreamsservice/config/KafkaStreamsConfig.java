package com.mobox.streamerorderprocessingstreamsservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobox.streamerorderprocessingstreamsservice.model.Order;
import com.mobox.streamerorderprocessingstreamsservice.model.ProcessedOrder;
import com.mobox.streamerorderprocessingstreamsservice.serde.JsonSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

import java.util.Arrays;
import java.util.Random;

@Slf4j
@Configuration
@EnableKafkaStreams
public class KafkaStreamsConfig {

    private final ObjectMapper objectMapper;

    public KafkaStreamsConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private JsonSerde<Order> orderJsonSerde(){
        return JsonSerde.forType(Order.class,  objectMapper);
    };

    private JsonSerde<ProcessedOrder> processedOrderJsonSerde(){
        return JsonSerde.forType(ProcessedOrder.class,  objectMapper);
    }

    @Bean
    public KStream<String, Order> processOrders(StreamsBuilder kStreamBuilder) {

        KStream<String, Order> ordersRawStream = kStreamBuilder.stream(
                "orders-raw",
                Consumed.with(Serdes.String(), orderJsonSerde())
        );

        KStream<String, Order> newOrdersStream = ordersRawStream
                .filter((key, order) -> order != null && "NEW".equalsIgnoreCase(order.getStatus())) // Add null check
                .peek((key, order) -> log.info("Streams: Processing NEW order: {}", order.getOrderId()));


        KStream<String, ProcessedOrder> processedOrdersStream = newOrdersStream
                .mapValues(order -> {
                    double calculatedTotalAmount = calculateTotalAmount(order.getProductIds());
                    boolean isHighValue = calculatedTotalAmount > 1000.0;

                    return new ProcessedOrder(
                            order.getOrderId(),
                            "PROCESSED",
                            order.getCustomerId(),
                            order.getAmount(),
                            calculatedTotalAmount,
                            isHighValue,
                            order.getTimestamp()
                    );
                })
                .peek((key, processedOrder) -> log.info(
                        "Streams: Enriched order: {}, Total: {}, High Value: {}", 
                        processedOrder.getOrderId(),
                        processedOrder.getTotalAmount(),
                        processedOrder.isHighValue()
                ));

        processedOrdersStream.to(
                "orders-processed",
                Produced.with(Serdes.String(), processedOrderJsonSerde())
        );

        return ordersRawStream;

    }

    private double calculateTotalAmount(String[] productIds) {
        if (productIds == null || productIds.length == 0) {
            return 0.0;
        }
        Random random = new Random();
        return Arrays.stream(productIds)
                .mapToDouble(id -> {
                    try {
                        int productIdNum = Integer.parseInt(id.replace("P", ""));
                        return 100.0 * productIdNum + (random.nextDouble() * 50);
                    } catch (NumberFormatException e) {
                        return 50.0 + (random.nextDouble() * 20);
                    }
                })
                .sum();
    }

}
