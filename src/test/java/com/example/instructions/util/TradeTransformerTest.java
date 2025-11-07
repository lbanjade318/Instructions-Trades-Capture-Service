package com.example.instructions.util;

import com.example.instructions.model.CanonicalTrade;
import com.example.instructions.model.Trade;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

class TradeTransformerTest {

    @Test
    void testTransform_Success() {
        Trade trade = new Trade();
        trade.setAccountNumber("1234567890");
        trade.setSecurity("abc123");
        trade.setType("buy");
        trade.setAmount(new BigDecimal("1000.50"));
        trade.setTimestamp("2025-11-07T10:00:00");

        CanonicalTrade ct = new CanonicalTrade();
        ct.setTrade(trade);

        CanonicalTrade result = TradeTransformer.transform(ct);

        assertNotNull(result);
        assertNotNull(result.getTrade());
        assertEquals("******7890", result.getTrade().getAccountNumber());
        assertEquals("ABC123", result.getTrade().getSecurity());
        assertEquals("B", result.getTrade().getType());
        assertEquals(new BigDecimal("1000.50"), result.getTrade().getAmount());
        assertEquals("2025-11-07T10:00:00", result.getTrade().getTimestamp());
    }

    @Test
    void testTransform_WithSellType() {
        Trade trade = new Trade();
        trade.setAccountNumber("987654321");
        trade.setSecurity(" tesla ");
        trade.setType("SELL");
        trade.setAmount(new BigDecimal("5000"));
        trade.setTimestamp("2025-11-07T12:00:00");

        CanonicalTrade ct = new CanonicalTrade();
        ct.setTrade(trade);

        CanonicalTrade result = TradeTransformer.transform(ct);

        assertEquals("S", result.getTrade().getType());
        assertEquals("TESLA", result.getTrade().getSecurity());
    }

    @Test
    void testTransform_UnknownTradeType() {
        Trade trade = new Trade();
        trade.setAccountNumber("9999");
        trade.setSecurity("bond");
        trade.setType("unknown");
        trade.setAmount(new BigDecimal("10"));
        trade.setTimestamp("2025-11-07T12:00:00");

        CanonicalTrade ct = new CanonicalTrade();
        ct.setTrade(trade);

        CanonicalTrade result = TradeTransformer.transform(ct);

        assertEquals("U", result.getTrade().getType());
    }

    @Test
    void testTransform_NullInput_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            TradeTransformer.transform(null);
        });
    }

    @Test
    void testTransform_NullTradeInside_ThrowsException() {
        CanonicalTrade ct = new CanonicalTrade();
        assertThrows(IllegalArgumentException.class, () -> {
            TradeTransformer.transform(ct);
        });
    }

    @Test
    void testMasking_ForShortAccountNumber() {
        Trade trade = new Trade();
        trade.setAccountNumber("1234");
        trade.setSecurity("AAPL");
        trade.setType("b");
        trade.setAmount(new BigDecimal("200"));
        trade.setTimestamp("2025-11-07T12:00:00");

        CanonicalTrade ct = new CanonicalTrade();
        ct.setTrade(trade);

        CanonicalTrade result = TradeTransformer.transform(ct);

        assertEquals("1234", result.getTrade().getAccountNumber());
    }

    @Test
    void testTransform_NullFields() {
        Trade trade = new Trade();
        trade.setAccountNumber(null);
        trade.setSecurity(null);
        trade.setType(null);
        trade.setAmount(null);
        trade.setTimestamp(null);

        CanonicalTrade ct = new CanonicalTrade();
        ct.setTrade(trade);

        CanonicalTrade result = TradeTransformer.transform(ct);

        assertNull(result.getTrade().getAccountNumber());
        assertNull(result.getTrade().getSecurity());
        assertEquals("U", result.getTrade().getType());
    }
}
