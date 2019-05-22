package Messages;

import akka.actor.ActorRef;

import java.io.Serializable;

public class MessageToService implements Serializable {
    public String bookTitle;
    public ActorRef actorRef;
    /** something to let the service response directly to client */

    public MessageToService(String bookTitle,ActorRef ref){
        this.actorRef = ref; //reference to client actor
        this.bookTitle = bookTitle;
    }
}
