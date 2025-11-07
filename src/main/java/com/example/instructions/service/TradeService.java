package com.example.instructions.service;

import com.example.instructions.model.Trade;
import com.example.instructions.model.CanonicalTrade;
import com.example.instructions.util.TradeTransformer;
import com.example.instructions.store.InMemoryStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@Service
public class TradeService {

    private final InMemoryStore store;
    private final KafkaTemplate<String,CanonicalTrade> kafkaTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${kafka.topic.inbound}")
    private String outboundTopic;

    public TradeService(InMemoryStore store, KafkaTemplate<String,CanonicalTrade> kafkaTemplate) {
        this.store = store;
        this.kafkaTemplate = kafkaTemplate;
    }
    public int processData(MultipartFile file) throws Exception {
        int processed = 0;
        String fname = file.getOriginalFilename();
        try (InputStream in = file.getInputStream()) {
            if (fname.endsWith(".csv")) {
                try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                    CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().withTrim().parse(reader);
                    for (CSVRecord rec : parser) {
                        try {
                            CanonicalTrade ct = new CanonicalTrade();
                            Trade trade = new Trade();
                            trade.setAccountNumber(rec.get("accountNumber"));
                            trade.setSecurity(rec.get("securityId"));
                            trade.setType(rec.get("tradeType"));
                            trade.setAmount(new BigDecimal(rec.get("price")));
                            trade.setTimestamp(rec.get("timestamp"));
                            ct.setPlatformId(rec.get("platformId"));
                            ct.setTrade(trade);
                            store(ct);
                            publish(TradeTransformer.transform(ct));
                            processed++;
                        } catch (Exception e) {
                            throw new Exception("Failed to process file: " + e.getMessage(), e);
                        }
                    }
                }
            } else if (fname.endsWith(".json")) {
                CanonicalTrade ct = mapper.readValue(in, CanonicalTrade.class);
                    store(ct);
                    publish(TradeTransformer.transform(ct));
                    processed++;
            }
        }catch (Exception e) {
            throw new Exception("Failed to process file: " + e.getMessage(), e);
        }
        return processed;
    }

    public  void store(CanonicalTrade ct) {
        store.put(ct);
    }

    public void publish(CanonicalTrade pt) {
        try {
            Message<CanonicalTrade > message = org.springframework.messaging.support.MessageBuilder
                    .withPayload(pt)
                    .setHeader(TOPIC, outboundTopic)
                    .build();
             kafkaTemplate.send(message);
        } catch(Exception e) {
         throw new RuntimeException(e.getMessage());
        }
    }
}