import org.json.JSONObject;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) throws Exception{


        DistributedMap dm = new DistributedMap();

        Runnable r;
        Thread t;
        r = new ClientCommunicator(1,1978,dm);
        t = new Thread(r);
        t.start();

        Thread.sleep(10000);
        DistributedMap dm2 = new DistributedMap();

        Runnable r2;
        Thread t2;
        r2 = new ClientCommunicator(2,1979,dm2);
        t2 = new Thread(r2);
        t2.start();


//        HashMap h= new HashMap();
//        h.put("xDDD",1);
//        h.put("xDDD1",12);
//        h.put("xDD3D",10);
//        h.put("xDD4D",14);
//
//        System.out.println(h.toString());
//        JSONObject j = new JSONObject(h);
//        System.out.println(j.toString());
//        h =(HashMap) j.toMap();
//
//
//        System.out.println(h.toString());
//        JSONObject ob = new JSONObject();
//        ob.put(Enums.JSONKeys.MESSAGE_TYPE.getMessageType(), Enums.ServerTypeMessages.INITIALIZATION_RESPONSE);
//        ob.put(Enums.JSONKeys.KEY.getMessageType(),"kluczyk");
//        ob.put(Enums.JSONKeys.VALUE.getMessageType(),1);
//        ob.put(Enums.JSONKeys.HASH_TABLE.getMessageType(),j);
//        System.out.println(ob.toString());
////        String stm = (String) ob.get(Enums.JSONKeys.HASH_TABLE.getMessageType());
//        Enums.ServerTypeMessages mt = (Enums.ServerTypeMessages) ob.get(Enums.JSONKeys.MESSAGE_TYPE.getMessageType());
//        System.out.println(mt);
//        System.out.println(ob.get(Enums.JSONKeys.MESSAGE_TYPE.getMessageType()).getClass());
//
//
//        System.out.println(ob.toString());
//        System.out.println(ob.get(Enums.JSONKeys.HASH_TABLE.getMessageType()).toString());
//        System.out.println(ob.get(Enums.JSONKeys.KEY.getMessageType()).getClass());
//        System.out.println(ob.get(Enums.JSONKeys.VALUE.getMessageType()).getClass());




    }
}
