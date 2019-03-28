import org.json.JSONObject;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) throws Exception{
        int id = new Integer(args[0]);
        System.out.println(id);

        int port = new Integer(args[1]);
        System.out.println(port);


        DistributedMap dm = new DistributedMap();

        Runnable r;
        Thread t;
        r = new ClientCommunicator(id,port,dm);
        t = new Thread(r);
        t.start();
    }
}
