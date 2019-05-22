package Bookshop;

import Messages.ClientMessage;
import Messages.ClientMessageType;
import Messages.MessageToService;
import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import scala.concurrent.duration.Duration;

import static akka.actor.SupervisorStrategy.stop;

/**Komunikacja z klientem
 *  + delegowanie zadan do nastepnych aktorow reprezentujacych odpowiednie usługi
 *
 *  */
public class Bookshop extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(ClientMessage.class, m -> {
                    if(m.clientMessageType == ClientMessageType.SEARCH_REQUEST){
                        log.info("Received search request");
                        MessageToService mts = new MessageToService(m.bookTitle,getSender());
                        context().child("search_service").get().tell(mts,getSelf());
                    } else if (m.clientMessageType == ClientMessageType.STREAM_REQUEST){
                        log.info("Received stream request");
                        MessageToService mts = new MessageToService(m.bookTitle,getSender());
                        context().child("stream_service").get().tell(mts,getSelf());
                    } else if (m.clientMessageType == ClientMessageType.ORDER_REQUEST){
                        log.info("Received order request");
                        MessageToService mts = new MessageToService(m.bookTitle,getSender());
                        context().child("order_service").get().tell(mts,getSelf());
                    } else {
                        log.warning("WTF????!!!");
                    }
                })
                .matchAny(o -> log.info("Bookshop: received unknown message"))
                .build();
    }

    @Override
    public void preStart(){
        /** TODO: Starting client communicator actor */

        context().actorOf(Props.create(SearchService.class,"resources/toSearch/"), "search_service");
        context().actorOf(Props.create(StreamService.class,"resources/toStream/"), "stream_service"); // need to pass path where the books are
        context().actorOf(Props.create(OrderService.class,"resources/toOrder/"), "order_service"); //need to pass path to write to file
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
