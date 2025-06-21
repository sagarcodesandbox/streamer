package com.mobox.streamerorderprocessingstreamsservice.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

public class JsonSerde<T> implements Serde<T> {

    private final JsonSerializer<T> serializer;
    private final JsonDeserializer<T> deserializer;

    public JsonSerde(Class<T> targetType, ObjectMapper objectMapper) {
        this.serializer = new JsonSerializer<>(objectMapper);
        this.deserializer = new JsonDeserializer<>(targetType, objectMapper);
        // Configure the deserializer to trust all packages
        this.deserializer.addTrustedPackages("*");
        this.deserializer.setUseTypeHeaders(false);
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        serializer.configure(configs, isKey);
        deserializer.configure(configs, isKey);
    }

    @Override
    public void close() {
        serializer.close();
        deserializer.close();
    }

    @Override
    public Serializer<T> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<T> deserializer() {
        return deserializer;
    }

    // Static factory method for convenience
    public static <T> JsonSerde<T> forType(Class<T> targetType, ObjectMapper objectMapper) {
        return new JsonSerde<>(targetType, objectMapper);
    }
}
