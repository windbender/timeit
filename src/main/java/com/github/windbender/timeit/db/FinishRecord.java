package com.github.windbender.timeit.db;

public class FinishRecord {
    public long time;
    public String rider;

    @Override
    public String toString() {
        return "FinishRecord{" +
                "time=" + time +
                ", rider='" + rider + '\'' +
                '}';
    }

    public double getTime() {
        return time;
    }
}
