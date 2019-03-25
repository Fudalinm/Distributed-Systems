package Enums;

public enum ServerTypeMessages {
    INITIALIZATION_REQUEST("INITIALIZATION_REQUEST"),
    INITIALIZATION_RESPONSE("INITIALIZATION_RESPONSE"),
    REMOVE_REQUEST("REMOVE_REQUEST"),
    PUT_REQUEST("PUT_REQUEST"),
    ERROR_MESSAGE("ERROR_MESSAGE")
    ;

    private final String value;

    ServerTypeMessages(String ServerTypeMessages){this.value = ServerTypeMessages;}

    public String getServerMessageType(){return this.value;}

    public static ServerTypeMessages serverTypeMessagesFromString(String s){
        if(s.equals(INITIALIZATION_REQUEST.getServerMessageType())){
            return INITIALIZATION_REQUEST;
        }else if(s.equals(INITIALIZATION_RESPONSE)){
            return INITIALIZATION_RESPONSE;
        }else if(s.equals(REMOVE_REQUEST)){
            return REMOVE_REQUEST;
        }else if(s.equals(PUT_REQUEST)){
            return PUT_REQUEST;
        }else{
            return ERROR_MESSAGE;
        }
    }
}
