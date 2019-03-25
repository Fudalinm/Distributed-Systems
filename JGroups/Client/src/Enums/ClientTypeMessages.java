package Enums;

/**possible it will need refactor */
public enum ClientTypeMessages {
    VIEW ("VIEW"),
    PUT ("PUT"),
    REMOVE ("REMOVE"),
    GET ("GET"),
    ERROR ("ERROR")
    ;

    private final String value;

    ClientTypeMessages(String ClientTypeMessages){this.value = ClientTypeMessages;}

    public String getClientMessageType(){return this.value;}

    public static ClientTypeMessages ClientTypeMessagesFromString(String s){
        if(s.equals(VIEW.getClientMessageType())){
            return VIEW;
        }else  if(s.equals(PUT.getClientMessageType())){
            return PUT;
        }else  if(s.equals(REMOVE.getClientMessageType())){
            return REMOVE;
        }else  if(s.equals(GET.getClientMessageType())){
            return GET;
        }else{
            return ERROR;
        }

    }

}
