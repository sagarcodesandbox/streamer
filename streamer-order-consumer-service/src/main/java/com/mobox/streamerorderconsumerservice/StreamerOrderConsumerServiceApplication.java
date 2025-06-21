package com.mobox.streamerorderconsumerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class StreamerOrderConsumerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StreamerOrderConsumerServiceApplication.class, args);
    }

}
