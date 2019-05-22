package Messages;

import java.io.Serializable;

public class ResponseFromDatabaseWorker implements Serializable {
    public MessageToService messageToService;
    public Integer price;

    public ResponseFromDatabaseWorker(MessageToService mts, Integer price ){
        this.messageToService = mts;
        this.price = price;
    }
}
