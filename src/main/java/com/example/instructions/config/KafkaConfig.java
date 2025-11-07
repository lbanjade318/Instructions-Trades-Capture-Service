package com.example.instructions.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic tradesTopic() {
        return TopicBuilder.name("instructions-topic").build();
    }

}
