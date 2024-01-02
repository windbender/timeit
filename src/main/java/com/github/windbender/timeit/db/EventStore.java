package com.github.windbender.timeit.db;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EventStore {


    public EventStore() {
    }
    private ConcurrentHashMap<String,Event> storemap = new ConcurrentHashMap();
    public Event fetch(String guid) {
        return storemap.get(guid);
    }

    public Event createNewEvent(EventSpec eventSpec) {
        UUID uuid = UUID.randomUUID();
        Event e = new Event(uuid.toString(), eventSpec);
        storemap.put(uuid.toString(),e);
        return e;
    }

}
