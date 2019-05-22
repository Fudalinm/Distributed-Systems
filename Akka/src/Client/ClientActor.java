package Client;

import Messages.ClientMessage;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ClientActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(ClientMessage.class, o -> {
                    //TODO: change way of passing message
                    getContext().actorSelection("akka.tcp://bookshop@127.0.0.1:3552/user/local").tell(o,getSelf());
                })
                /** shitty but there is not time to fix it */
                .match(String.class, o -> {
                    System.out.println("Received: " + o);
                })
                .matchAny(o -> log.info("local client actor: received unknown message"))
                .build();
    }

}
