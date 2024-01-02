package com.github.windbender.timeit.db;

import jakarta.ws.rs.sse.Sse;

public interface EventSender {
    void sendEvent(Event e, FinishRecord fr,  Sse sse);
}
