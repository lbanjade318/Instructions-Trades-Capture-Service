package com.example.instructions.service;

import com.example.instructions.model.CanonicalTrade;
import com.example.instructions.model.Trade;
import com.example.instructions.store.InMemoryStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.messaging.Message;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TradeServiceTest {

    @Mock
    private InMemoryStore store;

    @Mock
    private KafkaTemplate<String, CanonicalTrade> kafkaTemplate;

    @InjectMocks
    private TradeService tradeService;

    @Captor
    private ArgumentCaptor<CanonicalTrade> tradeCaptor;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        tradeService = new TradeService(store, kafkaTemplate);
    }

    @Test
    void testProcessData_withCsvFile_success() throws Exception {
        String csvData = "accountNumber,securityId,tradeType,price,timestamp,platformId\n"
                + "ACC123,SEC001,BUY,1000.50,2025-11-07T10:00:00,PLAT001";

        MockMultipartFile csvFile = new MockMultipartFile(
                "file",
                "trades.csv",
                "text/csv",
                csvData.getBytes()
        );

        tradeService.processData(csvFile);

        verify(store, times(1)).put(any(CanonicalTrade.class));
        verify(kafkaTemplate, times(1)).send(any(Message.class));
    }

    @Test
    void testProcessData_withJsonFile_success() throws Exception {
        CanonicalTrade ct = new CanonicalTrade();
        Trade trade = new Trade();
        trade.setAccountNumber("ACC999");
        trade.setSecurity("SEC999");
        trade.setType("SELL");
        trade.setAmount(new BigDecimal("500.75"));
        trade.setTimestamp("2025-11-07T12:00:00");
        ct.setPlatformId("PLAT999");
        ct.setTrade(trade);

        ObjectMapper mapper = new ObjectMapper();
        byte[] jsonData = mapper.writeValueAsBytes(ct);

        MockMultipartFile jsonFile = new MockMultipartFile(
                "file",
                "trade.json",
                "application/json",
                jsonData
        );

        tradeService.processData(jsonFile);

        verify(store, times(1)).put(any(CanonicalTrade.class));
        verify(kafkaTemplate, times(1)).send(any(Message.class));
    }

    @Test
    void testProcessData_withInvalidCsv_throwsException() throws Exception {
        String invalidCsv = "wrongHeader,wrongData\nabc,def";
        MockMultipartFile csvFile = new MockMultipartFile(
                "file",
                "invalid.csv",
                "text/csv",
                invalidCsv.getBytes()
        );

        org.junit.jupiter.api.Assertions.assertThrows(Exception.class, () -> {
            tradeService.processData(csvFile);
        });
    }

    @Test
    void testPublish_success() {
        CanonicalTrade ct = new CanonicalTrade();
        ct.setPlatformId("PLAT001");

        tradeService.publish(ct);

        verify(kafkaTemplate, times(1)).send(any(Message.class));
    }

    @Test
    void testStore_success() {
        CanonicalTrade ct = new CanonicalTrade();
        tradeService.store(ct);

        verify(store, times(1)).put(ct);
    }
}
