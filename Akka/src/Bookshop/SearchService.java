package Bookshop;

import Messages.MessageToService;
import Messages.ResponseFromDatabaseWorker;
import akka.actor.AbstractActor;
import akka.actor.AllForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import scala.concurrent.duration.Duration;
import scala.concurrent.java8.FuturesConvertersImpl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static akka.actor.SupervisorStrategy.stop;

/** Uses database workers to search for specific title
 * creates workers for each database for each client to search in one file */
public class SearchService extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private Map<MessageToService,Integer> clientMap = new HashMap<MessageToService,Integer>();
    private String pathToDatabases;

    public SearchService(String path){
        this.pathToDatabases = path;
    }

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(MessageToService.class, m -> {
                    log.info("Passing search request to databases");
                    clientMap.put(m,0);
                    getContext().child("database1").get().tell(m,getSelf());
                    getContext().child("database2").get().tell(m,getSelf());
                })
                .match(ResponseFromDatabaseWorker.class,response ->{
                    /*how to obtain which worker replied */
                    Integer counter = clientMap.get(response.messageToService);
                    if (counter == null) {
                        //nie mamy takiego klucza ergo blad lub juz wyslane ignorujemy
                        log.info("Ignoring response no such key in map: " +
                                "   -error or the response already sent" );
                    }else{
                        //we found the title
                        if (response.price > 0){
                            //todo:response to client
                            log.info("found title in database responding to client. title: " + response.messageToService.bookTitle + " price: " + response.price);
                            response.messageToService.actorRef.tell("Search response:\n title:  "+response.messageToService.bookTitle + "\n price " + response.price ,null);
                            clientMap.remove(response.messageToService);
                        } else {//couldnt find title we need to increment
                            if(counter == 1){
                                log.info("no such book in databases");
                                //todo: sent no such book in database
                                response.messageToService.actorRef.tell("Search response:\n title:  "+response.messageToService.bookTitle + "not in database",null);
                                clientMap.remove(response.messageToService);
                            }else {
                                clientMap.put(response.messageToService,new Integer(counter + 1));
                            }
                        }
                    }
                })
                .matchAny(o -> log.info("SearchService: received unknown message"))
                .build();
    }

    @Override
    public void preStart(){
        /** TODO: Starting client communicator actor
         * TODO: change path of database*/
        context().actorOf(Props.create(DatabaseWorker.class,this.pathToDatabases + "database1.txt"), "database1");
        context().actorOf(Props.create(DatabaseWorker.class, this.pathToDatabases + "database2.txt"), "database2");
    }

    /** TODO: check strategy */
    private static SupervisorStrategy strategy
            = new AllForOneStrategy(10, Duration.create("1 minute"), DeciderBuilder.
            matchAny(o -> stop()).
            build());

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }
}
