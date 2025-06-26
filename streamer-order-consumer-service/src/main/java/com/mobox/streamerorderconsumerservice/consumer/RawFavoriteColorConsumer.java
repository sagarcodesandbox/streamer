package com.mobox.streamerorderconsumerservice.consumer;

import com.mobox.streamerorderconsumerservice.model.PersonsFavoriteColor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RawFavoriteColorConsumer {

    @KafkaListener(topics = "favorite-colors-raw", groupId = "${spring.kafka.consumer.group-id}-raw")
    public void listenRawFavoriteColors(PersonsFavoriteColor favoriteColor) {
        log.info("Consumer: Received raw favorite color data: {}", favoriteColor.myFavoriteColor());
        
        // Here, you would implement business logic for raw data:
        // - Data validation and cleansing
        // - Store raw events for audit purposes
        // - Implement data quality checks
        // - Forward to data lake/warehouse for historical analysis
        // - Real-time alerting for data quality issues
        
        // Example: Validate the format
        String rawData = favoriteColor.myFavoriteColor();
        if (rawData != null && rawData.contains(",")) {
            String[] parts = rawData.split(",");
            if (parts.length == 2) {
                String person = parts[0].trim();
                String color = parts[1].trim();
                log.debug("Processed raw data - Person: {}, Color: {}", person, color);
                
                // Example: Data quality check
                if (person.isEmpty() || color.isEmpty()) {
                    log.warn("Data quality issue: Empty person or color in raw data: {}", rawData);
                }
            } else {
                log.warn("Data format issue: Expected 'person,color' format but got: {}", rawData);
            }
        } else {
            log.warn("Data format issue: Raw data doesn't contain comma separator: {}", rawData);
        }
    }
}
