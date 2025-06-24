package com.mobox.streamerorderprocessingstreamsservice.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mobox.streamerorderprocessingstreamsservice.model.PersonsFavoriteColor;
import com.mobox.streamerorderprocessingstreamsservice.serde.JsonSerde;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

import java.util.Arrays;

@Slf4j
@Configuration
@EnableKafkaStreams
public class FavoriteColorsConfig {

    private final ObjectMapper objectMapper;

    public static final String FAVORITE_COLORS_TOPIC = "favorite-colors-raw";

    public static final String FAVORITE_COLORS_PROCESSED_TOPIC = "favorite-colors-processed";

    public FavoriteColorsConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private JsonSerde<PersonsFavoriteColor> favoriteColorsJsonSerde(){return JsonSerde.forType(PersonsFavoriteColor.class, objectMapper);}

    @Bean
    public KStream<String, Long> processFavoriteColors(StreamsBuilder kStreamBuilder){

        KStream<String, PersonsFavoriteColor> rawFavoriteColorsStream = kStreamBuilder.stream(
                FAVORITE_COLORS_TOPIC,
                Consumed.with(Serdes.String(), favoriteColorsJsonSerde())
        );
        KStream<String,String> personsAndColors = rawFavoriteColorsStream
                .filter((key,value)-> value.myFavoriteColor().contains(","))
                .selectKey((key,value)-> value.myFavoriteColor().split(",")[0].toLowerCase())
                .mapValues(value -> value.myFavoriteColor().split(",")[1].toLowerCase())
                .filter((user,color)-> Arrays.asList("green","blue","red").contains(color));

        personsAndColors.to("persons-and-colors-topic");
        KTable<String,String> personsAndColorsTable = kStreamBuilder.table("persons-and-colors-topic");

        KTable<String,Long> favoriteColors = personsAndColorsTable.groupBy((person,color)-> new KeyValue<>(color,color))
                .count(Named.as("CountsByColors"));
        favoriteColors.toStream().to(FAVORITE_COLORS_PROCESSED_TOPIC, Produced.with(Serdes.String(), Serdes.Long()));

        return favoriteColors.toStream();


    }

}
