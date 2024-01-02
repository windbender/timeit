package com.github.windbender.timeit.db;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Event {

    public List<FinishRecord> getFinishes() {
        return finishes;
    }

    private final List<FinishRecord> finishes;

    public String getName() {
        return name;
    }

    private final String name;
    String guid;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }


    public Event(String guid, EventSpec es) {
        this.guid = guid;
        this.name = es.name;
        this.finishes = new CopyOnWriteArrayList<FinishRecord>();
    }

    public Event(String guid, String name, List<FinishRecord> finishes) {
        this.guid = guid;
        this.name = name;
        this.finishes = finishes;
    }

    public void addFinish(FinishRecord fr) {
        finishes.add(fr);
    }
}
