package com.mobox.streamerorderprocessingstreamsservice.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobox.streamerorderprocessingstreamsservice.model.OrderType;
import com.mobox.streamerorderprocessingstreamsservice.model.RestaurantOrder;
import com.mobox.streamerorderprocessingstreamsservice.serde.JsonSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@Slf4j
@Configuration
@EnableKafkaStreams
public class KafkaOrderSplitStreamsConfig {

    private final ObjectMapper objectMapper;

    public static final String RESTAURANT_ORDERS_RAW_TOPIC = "restaurant-orders-raw";

    public static final String RESTAURANT_ORDERS_TOPIC = "restaurant-Orders-processed";
    public static final String GENERAL_ORDERS_TOPIC = "general-Orders-processed";

    public KafkaOrderSplitStreamsConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private JsonSerde<RestaurantOrder> restaurantOrderJsonSerde(){ return JsonSerde.forType(RestaurantOrder.class, objectMapper); }

    @Bean
    public KStream<String, RestaurantOrder> splitAndProcessRestaurantOrdersByOrderType(StreamsBuilder kStreamBuilder){

            Predicate<String,RestaurantOrder> generalPredicate = (key, order) -> order.orderType().equals(OrderType.GENERAL);
            Predicate<String,RestaurantOrder> restaurantPredicate = (key, order) -> order.orderType().equals(OrderType.RESTAURANT);

            KStream<String,RestaurantOrder> restaurantRawOrdersStream = kStreamBuilder.stream(
                    RESTAURANT_ORDERS_RAW_TOPIC,
                    Consumed.with(Serdes.String(), restaurantOrderJsonSerde())
            );
            restaurantRawOrdersStream.print(Printed.<String, RestaurantOrder>toSysOut().withLabel("RAW-RESTAURANT-ORDERS"));

            restaurantRawOrdersStream.split(Named.as("General-restaurant-stream"))
                    .branch(generalPredicate,
                            Branched.withConsumer(generalOrderStream -> {
                                generalOrderStream.print(Printed.<String,RestaurantOrder>toSysOut().withLabel("GENERAL_STREAM"));
                                generalOrderStream.to(GENERAL_ORDERS_TOPIC, Produced.with(Serdes.String(), restaurantOrderJsonSerde()));
                            })
                    )
                    .branch(restaurantPredicate,
                            Branched.withConsumer(restaurantOrderStream -> {
                                restaurantOrderStream.print(Printed.<String,RestaurantOrder>toSysOut().withLabel("RESTAURANT_STREAM"));
                                restaurantOrderStream.to(RESTAURANT_ORDERS_TOPIC, Produced.with(Serdes.String(), restaurantOrderJsonSerde()));
                            })
            );
            return  restaurantRawOrdersStream;
    }


}
