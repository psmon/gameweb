package com.vgw.demo.gameweb.actor;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.vgw.demo.gameweb.message.actor.ConnectInfo;
import com.vgw.demo.gameweb.message.actor.TableInfo;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LobbyActor extends AbstractActor {

    private LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private Map<String, SimpMessageSendingOperations> sessionMgr = new HashMap<>();

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(ConnectInfo.class, c -> {
                    if(c.getConnectCmd()== ConnectInfo.ConnectCmd.CONNECT){
                        sessionMgr.put(c.getSessionId(),c.getWsSender());
                        log.info("user connected:"+c.getSessionId());
                    }else if(c.getConnectCmd()== ConnectInfo.ConnectCmd.DISCONET){
                        sessionMgr.remove(c.getSessionId());
                        log.info("user disconnected:"+c.getSessionId());
                    }
                    sessionMgr.put(c.getSessionId(),c.getWsSender());
                })
                .match(TableInfo.class, t->{
                    // Create a table under the lobby, if you have an Actor named TableManagement, you can move easily.
                    String tableUID = "table-" + t.getTableId();
                    if(t.getCmd() == TableInfo.TableCmd.CREATE){
                        getContext().actorOf( TableActor.props(t,this.getSelf() ), tableUID);
                    }
                })
                .build();
    }
}
