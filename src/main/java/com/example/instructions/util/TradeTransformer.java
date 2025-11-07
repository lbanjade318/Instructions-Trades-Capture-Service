package com.example.instructions.util;

import com.example.instructions.model.CanonicalTrade;
import com.example.instructions.model.Trade;

public class TradeTransformer {



    public  static CanonicalTrade transform(CanonicalTrade ct) {
        if (ct == null) throw new IllegalArgumentException("null input");


        Trade trade = new Trade();
        if(ct.getTrade() == null) throw new IllegalArgumentException("null trade in input");
        trade.setAccountNumber(mask(ct.getTrade().getAccountNumber()));
        trade.setSecurity(normalize(ct.getTrade().getSecurity()));
        trade.setType(normalizeSide(ct.getTrade().getType()));
        trade.setAmount(ct.getTrade().getAmount());
        trade.setTimestamp(ct.getTrade().getTimestamp());
        ct.setTrade(trade);
        return ct;
    }


    private static String mask(String acct) {
        if (acct == null) return null;
        if (acct.length() <=4) return acct;
        return acct.replaceAll(".(?=.{4})","*");


    }

    private static String normalize(String s) {
        if (s == null) return null;
        return s.trim().toUpperCase();
    }

    private static String normalizeSide(String t) {
        if (t == null) return "U";
        t = t.trim().toLowerCase();
        switch(t) {
            case "buy": case "b": return "B";
            case "sell": case "s": return "S";
            default: return "U";
        }
    }
}
