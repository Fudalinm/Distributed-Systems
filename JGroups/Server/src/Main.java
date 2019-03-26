import org.json.JSONObject;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) throws Exception{


//        DistributedMap dm = new DistributedMap();
//
//        Runnable r;
//        Thread t;
//        r = new ClientCommunicator(1,1978,dm);
//        t = new Thread(r);
//        t.start();

//        Thread.sleep(10000);
        DistributedMap dm2 = new DistributedMap();

        Runnable r2;
        Thread t2;
        r2 = new ClientCommunicator(2,1979,dm2);
        t2 = new Thread(r2);
        t2.start();

    }
}
