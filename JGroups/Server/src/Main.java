import org.json.JSONObject;

import java.util.HashMap;

public class Main {
    public static void main(String[] args){
        HashMap h= new HashMap();
        h.put("xDDD",1);
        h.put("xDDD1",12);
        h.put("xDD3D",10);
        h.put("xDD4D",14);

        System.out.println(h.toString());
        JSONObject j = new JSONObject(h);
        h =(HashMap) j.toMap();
        System.out.println(h.toString());
        System.out.println(j);


        JSONObject ob = new JSONObject();
        ob.put(JSONKeys.MESSAGE_TYPE.getMessageType(), ServerTypeMessages.INITIALIZATION_RESPONSE);
        ob.put(JSONKeys.KEY.getMessageType(),"kluczyk");
        ob.put(JSONKeys.VALUE.getMessageType(),1);
        ob.put(JSONKeys.HASH_TABLE.getMessageType(),j);
        System.out.println(ob.toString());
        System.out.println(ob.get(JSONKeys.HASH_TABLE.getMessageType()).toString());

        System.out.println(ob.get(JSONKeys.KEY.getMessageType()).getClass());
        System.out.println(ob.get(JSONKeys.VALUE.getMessageType()).getClass());




    }
}
