package Messages;

import java.io.Serializable;

public class ClientMessage implements Serializable {
    public ClientMessageType clientMessageType;
    public String bookTitle;

    public ClientMessage(ClientMessageType cmt, String bookName){
        this.clientMessageType = cmt;
        this.bookTitle = bookName;
    }
}
