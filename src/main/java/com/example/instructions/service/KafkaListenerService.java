package com.example.instructions.service;


import com.example.instructions.model.CanonicalTrade;
import com.example.instructions.util.TradeTransformer;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaListenerService {

    private final TradeService tradeService;



    public KafkaListenerService(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @KafkaListener(topics ={"${kafka.topic.inbound}"} , groupId = "instructions-group")
    public void listen(CanonicalTrade ct) {
        if(ct == null){
            System.err.println("Received null trade message");
            return;
        }
        try {
            var transformed = TradeTransformer.transform(ct);
            tradeService.store(transformed);
            //System.out.println("Trade successfully processed and stored: " + transformed.getPlatformId());

        } catch (IllegalArgumentException e) {
            System.err.println("Invalid trade data: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to process inbound: " + e.getMessage());
        }

    }
}
