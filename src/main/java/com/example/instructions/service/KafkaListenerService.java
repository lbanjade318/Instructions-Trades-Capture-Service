package com.example.instructions.service;

import com.example.instructions.model.Trade;
import com.example.instructions.model.CanonicalTrade;
import com.example.instructions.util.TradeTransformer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaListenerService {

    private final TradeService tradeService;
    private final ObjectMapper mapper = new ObjectMapper();


    public KafkaListenerService(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @KafkaListener(topics ={"${kafka.topic.inbound}"} , groupId = "instructions-group")
    public void listen(CanonicalTrade ct) {
        try {
            tradeService.store(TradeTransformer.transform(ct));
        } catch (Exception e) {
            System.err.println("Failed to process inbound: " + e.getMessage());
        }
    }
}
