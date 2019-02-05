package com.vgw.demo.gameweb.actor;

import akka.actor.Props;
import akka.persistence.AbstractPersistentActor;
import akka.persistence.SnapshotOffer;
import akka.persistence.SnapshotSelectionCriteria;
import kamon.Kamon;

import java.io.Serializable;
import java.util.ArrayList;

class Cmd implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String data;

    public Cmd(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}

class Evt implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String data;

    public Evt(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}

class ExampleState implements Serializable {
    private static final long serialVersionUID = 1L;
    private final ArrayList<String> events;

    public ExampleState() {
        this(new ArrayList<>());
    }

    public ExampleState(ArrayList<String> events) {
        this.events = events;
    }

    public ExampleState copy() {
        return new ExampleState(new ArrayList<>(events));
    }

    public void update(Evt evt) {
        events.add(evt.getData());
    }

    public int count(String eventName){
        int count=0;
        for(String event:events){
            if(event.contains("inc-"+eventName))
                count++;
            else if(count>0){
                if(event.contains("dec-"+eventName)) count--;
            }
        }
        return count;
    }

    public int size() {
        return events.size();
    }

    @Override
    public String toString() {
        return events.toString();
    }
}

public class CountActor extends AbstractPersistentActor {

    private ExampleState state = new ExampleState();
    private int snapShotInterval = 1000;
    private String playerId;


    static public Props props(String playerId) {
        return Props.create(CountActor.class, () -> new CountActor(playerId));
    }

    public CountActor(String playerId) {
        this.playerId = playerId;
    }

    private int getNumEvents() {
        return state.size();
    }

    private int getNumEventsByName(String eventName) {
        return state.count(eventName);
    }

    private void reset(){
        state = new ExampleState();
        deleteSnapshots(SnapshotSelectionCriteria.Latest());
    }

    private void crash()  {
        postStop();
    }

    @Override
    public String persistenceId() { return "countactor-"+playerId; }

    @Override
    public Receive createReceiveRecover() {
        return receiveBuilder()
                .match(Evt.class, state::update)
                .match(SnapshotOffer.class, ss -> state = (ExampleState) ss.snapshot())
                .build();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Cmd.class, c -> {
                    Kamon.currentSpan().tag("my-tag", "awesome-value");
                    final String data = c.getData();
                    final Evt evt = new Evt(data + "-" + getNumEvents());
                    persist(evt, (Evt e) -> {
                        state.update(e);
                        getContext().getSystem().eventStream().publish(e);
                        if (lastSequenceNr() % snapShotInterval == 0 && lastSequenceNr() != 0)
                            // IMPORTANT: create a copy of snapshot because ExampleState is mutable
                            saveSnapshot(state.copy());
                    });
                })
                .matchEquals("print", s -> System.out.println(state))
                .matchEquals("playcount", s -> {
                    int playcount = getNumEventsByName("playcount");
                    System.out.println(s+":"+playcount);
                    getSender().tell(playcount,null);
                })
                .matchEquals("buyincount", s -> {
                    int playcount = getNumEventsByName("buyincount");
                    System.out.println(s+":"+playcount);
                    getSender().tell(playcount,null);
                })
                .matchEquals("reset", s-> reset() )
                .matchEquals("crash", s-> crash() )
                .build();
    }

}
