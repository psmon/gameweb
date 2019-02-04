package com.vgw.demo.gameweb.actor;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import com.vgw.demo.gameweb.config.AppConfiguration;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = AppConfiguration.class)
public class CountActorTest {

    // Counting with Event Sourcing

    static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testIt(){
        new TestKit(system) {{
            final String playerId="psmon";
            final ActorRef countActor = system.actorOf(CountActor.props(playerId), "countActor");
            final ActorRef observer = getRef();

            countActor.tell("reset",null);
            countActor.tell(new Cmd("inc-playcount"),null);
            countActor.tell(new Cmd("dec-playcount"),null);
            countActor.tell(new Cmd("inc-playcount"),null);
            countActor.tell(new Cmd("inc-buyincount"),null);
            countActor.tell(new Cmd("inc-buyincount"),null);
            countActor.tell(new Cmd("inc-buyincount"),null);

            countActor.tell("playcount",observer);
            expectMsg(Duration.ofSeconds(1),1);

            // for tourment register/unregister
            countActor.tell("buyincount",observer);
            expectMsg(Duration.ofSeconds(1),3);

            // Reset for DownTime
            countActor.tell("reset",null);
            countActor.tell(new Cmd("inc-playcount"),null);
            countActor.tell(new Cmd("inc-playcount"),null);
            countActor.tell("playcount",observer);
            expectMsg(Duration.ofSeconds(1),2);

        }};
    }
}
