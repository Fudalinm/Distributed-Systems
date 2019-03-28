import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String id = "_" + args[0];
        int port = new Integer(args[1]);
        System.out.println(port);

        Client c = new Client("fudalinm" + id,port,"127.0.0.1");
        c.run();
    }
}
