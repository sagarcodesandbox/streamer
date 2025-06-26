package com.mobox.streamerorderconsumerservice.consumer;

import com.mobox.streamerorderconsumerservice.model.ColorCount;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FavoriteColorConsumer {

    @KafkaListener(topics = "favorite-colors-processed", groupId = "${spring.kafka.consumer.group-id}")
    public void listenFavoriteColorCounts(ConsumerRecord<String, Long> record) {
        String color = record.key();
        Long count = record.value();
        
        log.info("Consumer: Received favorite color count update - Color: {}, Count: {}", color, count);
        
        // Create ColorCount object for consistent handling
        ColorCount colorCount = new ColorCount(color, count);
        
        // Here, you would implement actual business logic:
        // - Update a dashboard with real-time color preferences
        // - Send notifications when a color becomes popular
        // - Store aggregated counts in a database
        // - Trigger marketing campaigns based on color trends
        // - Update recommendation engines
        
        log.debug("Processing favorite color analytics for color: {} with count: {}", 
                colorCount.color(), colorCount.count());
        
        // Example: Check if this color has reached a milestone
        if (count > 0 && count % 10 == 0) {
            log.info("Color milestone reached! {} has {} fans", color, count);
        }
    }
}
