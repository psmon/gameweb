

## RestAPI with ActorMessage

    @RequestMapping("/gameinfo/dealer/{gameId}")
    int getDealer(@PathVariable String gameId) {
        try{
            String actorPath = "user/lobby/table-" + gameId;
            ActorSelection tableActor = system.actorSelection(actorPath);
            return (int)AkkaUtil.AskToActorSelect(tableActor,new TableInfo(TableInfo.Cmd.DealerPos),1);
        }catch (Exception e){
            logger.error(e.toString());
            return -100;
        }
    }
    
## Webocket with Actor

![](../../../../../../../../doc/actorwithws.png)
