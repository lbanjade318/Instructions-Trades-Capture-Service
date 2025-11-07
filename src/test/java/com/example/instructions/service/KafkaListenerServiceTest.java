package com.example.instructions.service;

import com.example.instructions.model.CanonicalTrade;
import com.example.instructions.model.Trade;
import com.example.instructions.util.TradeTransformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class KafkaListenerServiceTest {

    @Mock
    private TradeService tradeService;

    @InjectMocks
    private KafkaListenerService kafkaListenerService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        kafkaListenerService = new KafkaListenerService(tradeService);
    }

    @Test
    void testListen_Success() {
        CanonicalTrade canonicalTrade = new CanonicalTrade();
        Trade trade = new Trade();
        canonicalTrade.setTrade(trade);

        kafkaListenerService.listen(canonicalTrade);

        verify(tradeService, times(1)).store(any());
    }

    @Test
    void testListen_WhenTradeServiceThrowsException() {
        CanonicalTrade canonicalTrade = new CanonicalTrade();
        Trade trade = new Trade();
        canonicalTrade.setTrade(trade);

        doThrow(new RuntimeException("store failed")).when(tradeService).store(any());

        kafkaListenerService.listen(canonicalTrade);

        verify(tradeService, times(1)).store(any());
    }
}
