package com.vgw.demo.gameweb.actor;

import akka.actor.AbstractActor;
import com.vgw.demo.gameweb.message.Greeting;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GreetingActor extends AbstractActor {

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
            .match(Greeting.class, c -> {
                String name = c.getName();
                getSender().tell("Hello, " + name ,getSelf()); // response for test
            })
            .build();
    }

}