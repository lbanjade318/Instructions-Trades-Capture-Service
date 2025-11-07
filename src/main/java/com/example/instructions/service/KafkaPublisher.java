package com.example.instructions.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPublisher {
    private final KafkaTemplate<String,String> kafkaTemplate;

    public KafkaPublisher(KafkaTemplate<String,String> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(String topic, String key, String message) {

        kafkaTemplate.send(topic, key, message);
    }
}
