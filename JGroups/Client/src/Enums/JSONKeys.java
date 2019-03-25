package Enums;

public enum JSONKeys {
    MESSAGE_TYPE ("MESSAGE_TYPE"),
    HASH_TABLE ("HASH_TABLE"), //hashTable is sent as jsonObject
    KEY ("KEY"),
    VALUE ("VALUE")
    ;

    private final String value;

    JSONKeys(String JSONKeys){
        this.value = JSONKeys;
    }

    public String getMessageType(){
        return this.value;
    }
}
