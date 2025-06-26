package com.mobox.streamerordercreationservice.service;

import com.mobox.streamerordercreationservice.model.PersonsFavoriteColor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class FavoriteColorEventProducer {
    private final KafkaTemplate<String, PersonsFavoriteColor> kafkaTemplate;

    @Value("favorite-colors-raw")
    private String favoriteColorsRawTopic;

    public FavoriteColorEventProducer(KafkaTemplate<String, PersonsFavoriteColor> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public CompletableFuture<PersonsFavoriteColor> publishFavoriteColor(PersonsFavoriteColor color) {
        // 1. Input validation - check for null
        if (color == null || color.myFavoriteColor() == null || color.myFavoriteColor().trim().isEmpty()) {
            log.error("Invalid input: PersonsFavoriteColor or myFavoriteColor is null/empty");
            throw new IllegalArgumentException("PersonsFavoriteColor and myFavoriteColor cannot be null or empty");
        }

        // 2. Format validation - ensure it contains comma
        String favoriteColorData = color.myFavoriteColor().trim();
        if (!favoriteColorData.contains(",")) {
            log.error("Invalid format: myFavoriteColor must contain comma. Received: {}", favoriteColorData);
            throw new IllegalArgumentException("myFavoriteColor must be in format 'person,color'. Example: 'alice,blue'");
        }

        // 3. Parse the person and color
        String[] parts = favoriteColorData.split(",");
        if (parts.length != 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            log.error("Invalid format: myFavoriteColor must have exactly 2 parts separated by comma. Received: {}", favoriteColorData);
            throw new IllegalArgumentException("myFavoriteColor must be in format 'person,color' with both parts non-empty");
        }

        String personName = parts[0].trim().toLowerCase();
        String colorName = parts[1].trim().toLowerCase();

        // 4. Optional: Validate color (though your stream also filters this)
        if (!Arrays.asList("green", "blue", "red").contains(colorName)) {
            log.warn("Color '{}' is not in the accepted list (green, blue, red). Stream will filter it out.", colorName);
            // Note: We don't throw exception here, let the stream handle filtering
        }

        // 5. Create the final object (ensure consistent format)
        final PersonsFavoriteColor finalColor = new PersonsFavoriteColor(
                personName + "," + colorName
        );

        // 6. Log publishing intent
        log.info("Publishing favorite color: person='{}', color='{}' to topic: {}",
                personName, colorName, favoriteColorsRawTopic);

        // 7. Send to Kafka using person name as key
        return kafkaTemplate.send(favoriteColorsRawTopic, personName, finalColor)
                .thenApply(sendResult -> {
                    // 8. Success handler
                    log.info("Successfully published favorite color for person '{}' to topic '{}' at offset: {}",
                            personName, favoriteColorsRawTopic, sendResult.getRecordMetadata().offset());
                    return finalColor;
                })
                .exceptionally(throwable -> {
                    // 9. Error handler
                    log.error("Failed to publish favorite color for person '{}' to topic '{}'. Error: {}",
                            personName, favoriteColorsRawTopic, throwable.getMessage(), throwable);
                    throw new RuntimeException("Failed to publish favorite color data", throwable);
                });
    }
}
