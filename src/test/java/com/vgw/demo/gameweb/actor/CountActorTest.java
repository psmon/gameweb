package com.vgw.demo.gameweb.actor;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.testkit.javadsl.TestKit;
import com.vgw.demo.gameweb.config.AppConfiguration;
import com.vgw.demo.gameweb.gameobj.Money;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;


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
    public void moneyTest(){
        Money a= new Money(new BigDecimal("100.12"));
        Money b= new Money(new BigDecimal("100.12"));
        Money c= new Money(new BigDecimal("100.12"));
        assertThat(a).isEqualTo(b);
        assertThat(a).isEqualTo(c);
        assertThat(b).isEqualTo(a);     // a == b Even if the verification succeeds, b == a may fail.

    }

    @Test
    public void testIt(){
        new TestKit(system) {{
            final String playerId="psmon";
            ActorRef countActor = system.actorOf(CountActor.props(playerId), "countActor");
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

            // print event..
            countActor.tell("print",null);

            // Reset for DownTime
            countActor.tell("reset",null);
            countActor.tell(new Cmd("inc-playcount"),null);
            countActor.tell(new Cmd("inc-playcount"),null);
            countActor.tell("playcount",observer);
            expectMsg(Duration.ofSeconds(1),2);

            // Recover Test
            countActor.tell(PoisonPill.getInstance(),null);
            expectNoMessage(Duration.ofSeconds(3));
            countActor = system.actorOf(CountActor.props(playerId), "countActor");
            countActor.tell("print",null);
            countActor.tell("playcount",observer);
            expectMsg(Duration.ofSeconds(1),3);

        }};
    }
}
