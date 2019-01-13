package com.vgw.demo.gameweb.actor.gamecore;

import akka.actor.AbstractFSM;
import akka.actor.ActorRef;

import java.util.LinkedList;
import java.util.List;

import static com.vgw.demo.gameweb.actor.gamecore.GameState.Idle;
import static com.vgw.demo.gameweb.actor.gamecore.Uninitialized.Uninitialized;

enum GameState {
    Idle, Ready, Bet, Turn,Result
}
// state data
interface Data {
}

enum Flush {
    Flush
}

enum Uninitialized implements Data {
    Uninitialized
}

final class Todo implements Data {
    private final ActorRef target;
    private final List<Object> queue;

    public Todo(ActorRef target, List<Object> queue) {
        this.target = target;
        this.queue = queue;
    }

    public LinkedList<Object> copy(LinkedList<Object> queue) {
        return null;
    }

    public ActorRef getTarget() {
        return target;
    }

    public List<Object> getQueue() {
        return queue;
    }
    // boilerplate ...
}

final class SetTarget {
    private final ActorRef ref;

    public SetTarget(ActorRef ref) {
        this.ref = ref;
    }

    public ActorRef getRef() {
        return ref;
    }
    // boilerplate ...
}

public class GUCGameFSM extends AbstractFSM<GameState, Data> {
    {
        startWith(Idle, Uninitialized);
        // transitions
        when(Idle,
                matchEvent(SetTarget.class, Uninitialized.class, (setTarget, uninitialized) ->
                    stay().using(new Todo(setTarget.getRef(), new LinkedList<>()))
        ));
        /*
        when(Ready, Duration.create(1, "second"),
                matchEvent(Arrays.asList(Flush.class, StateTimeout()), Todo.class, (event, todo) ->
                        goTo(Idle).using(todo.copy(new LinkedList<>()))));*/

        initialize();
    }
}
