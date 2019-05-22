package Bookshop;

import Messages.MessageToService;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import java.io.FileWriter;

/** there is only one because we cannot modify same time  */
public class OrderService extends AbstractActor {
    String orderPath;
    FileWriter fileWriter;
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public OrderService(String orderPath){
        this.orderPath = orderPath + "orders.txt";
    }

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(MessageToService.class, m -> {
                    log.info("received order request writing to file\n");
                    fileWriter.write(m.actorRef.toString() + " " + m.bookTitle);
                    m.actorRef.tell("Wrote order to file : '" + m.actorRef.toString() + " " + m.bookTitle +" '",null);
                })
                .matchAny(o -> log.info("OrderService: received unknown message"))
                .build();
    }

    @Override
    public void preStart() throws Exception{
        this.fileWriter = new FileWriter(this.orderPath);
    }

}
