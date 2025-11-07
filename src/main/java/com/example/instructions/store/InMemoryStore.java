package com.example.instructions.store;

import com.example.instructions.model.CanonicalTrade;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collection;

@Component
public class InMemoryStore {
    private final ConcurrentHashMap<String, CanonicalTrade> map = new ConcurrentHashMap<>();

    public void put(CanonicalTrade t) {
        if (t!=null && t.getPlatformId()!=null) map.put(t.getPlatformId(), t);
    }
    public CanonicalTrade get(String id) {
        return map.get(id);
    }
    public Collection<CanonicalTrade> all() {
        return map.values();
    }
    public void remove(String id) {
        map.remove(id);
    }
}
