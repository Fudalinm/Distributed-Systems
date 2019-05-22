package Bookshop;

import Messages.MessageToService;
import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import scala.concurrent.duration.Duration;
import static akka.actor.SupervisorStrategy.restart;

public class StreamService extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    String bookDirPath;
    int counter = 0;

    public StreamService(String bookDirPath){
        this.bookDirPath = bookDirPath;
    }


    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(MessageToService.class, m -> {
                    counter += 1;
                    log.info("Creating stream worker for : " + m.actorRef.toString());
                    context().actorOf(Props.create(StreamWorker.class,this.bookDirPath), "xD" + counter);
                    context().child("xD" + counter).get().tell(m,getSelf());
                })
                .matchAny(o -> log.info("StreamService: received unknown message"))
                .build();
    }


    @Override
    public void preStart(){
        log.info("Starting stream service");
    }

    /** TODO: check strategy */
    private static SupervisorStrategy strategy
            = new OneForOneStrategy(10, Duration.create("1 minute"), DeciderBuilder.
            matchAny(o -> restart()).
            build());

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }





}
