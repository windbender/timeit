package com.github.windbender.timeit.resources;

import com.github.windbender.timeit.db.*;
import jakarta.inject.Singleton;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseBroadcaster;
import jakarta.ws.rs.sse.SseEventSink;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/")
@Slf4j
@Singleton
public class EventResource implements EventSender {

    private final EventStore store;
    private Map<String,SseBroadcaster> broadcasters = new ConcurrentHashMap<>();
    public EventResource(EventStore store) {
        this.store = store;
    }



    @Produces(MediaType.SERVER_SENT_EVENTS)
    @Path("/event/stream/{guid}")
    @GET
    public void listenToBroadcast(final @Context SseEventSink eventSink, @PathParam("guid") String guid) {
        SseBroadcaster sseBroadcaster = broadcasters.get(guid);
        if(sseBroadcaster == null) return;
        sseBroadcaster.register(eventSink);
    }
    @Override
    public void sendEvent(Event e, FinishRecord fr, Sse sse) {
        final OutboundSseEvent event = sse.newEventBuilder()
                .name("newFinish")
                .mediaType(MediaType.APPLICATION_JSON_TYPE)
                .data(FinishRecord.class, fr)
                .build();
        SseBroadcaster broadcaster = broadcasters.get(e.getGuid());
        log.info("now sending an event of "+fr);
        broadcaster.broadcast(event);
    }
    @GET
    @Path("/event/{guid}")
    public Event fetch(@PathParam("guid") String guid) {
        final Event event = store.fetch(guid);
        if (event != null) {
            return event;
        }
        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    @GET
    @Path("/event/processed/{guid}")
    public Event fetchProcessed(@PathParam("guid") String guid) {
        final Event event = store.fetch(guid);

        if (event != null) {
            final Event fixedEvent = process(event);
            return fixedEvent;
        }
        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    private Event process(Event event) {
        Map<String, List<FinishRecord>> riders = event.getFinishes().stream().collect(Collectors.groupingBy(e -> e.rider));
        Map<String, List<FinishRecord>> out = new TreeMap<>();
        riders.entrySet().stream().forEach(ent -> {
            String rider = ent.getKey();
            List<FinishRecord> l = ent.getValue();
            if( l.size() > 1) {
                // do median in case one is wack!
                DoubleStream sortedAges = l.stream().mapToDouble(FinishRecord::getTime).sorted();
                double median = l.size()%2 == 0?
                        sortedAges.skip(l.size()/2-1).limit(2).average().getAsDouble():
                        sortedAges.skip(l.size()/2).findFirst().getAsDouble();
                l = new ArrayList<>();
                FinishRecord fr = new FinishRecord();
                fr.time = (long)median;
                fr.rider = rider;
                l.add(fr);
            }
            out.put(rider,l);
        });
        List<FinishRecord> flist = out.entrySet().stream().map(ent -> ent.getValue().get(0)).collect(Collectors.toList());
        Event outEvent = new Event(event.getGuid(), event.getName(), flist);
        return outEvent;
    }

    @POST
    @Path("/event/{guid}")
    public Response afinish(@PathParam("guid") String guid,FinishRecord fr,@Context Sse sse) {
        Event event = store.fetch(guid);
        if(event == null) throw new NotFoundException();
        event.addFinish(fr);
        this.sendEvent(event,fr,sse);

        URI uri = UriBuilder.fromResource(EventResource.class)
                .path("event/{guid}").build( event.getGuid());
        Response resp = Response.created(uri)
                .build();
        return resp;
    }

    // creates a new event
    @POST
    @Path("/event")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response add(EventSpec eventSpec, @Context Sse sse) {
        Event event = store.createNewEvent(eventSpec);
        SseBroadcaster broadcaster = sse.newBroadcaster();
        broadcasters.put(event.getGuid(),broadcaster);
        URI uri = UriBuilder.fromResource(EventResource.class)
                .path("event/{guid}").build( event.getGuid());
        Response resp = Response.created(uri)
                .entity("\""+event.getGuid()+"\"")
                .type(MediaType.APPLICATION_JSON_TYPE)
                .contentLocation(uri)
                .build();

        log.info("created new Event with GUID="+event.toString());
        log.info("response is "+resp);
        return resp;
    }


}
