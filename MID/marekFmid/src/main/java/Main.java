import io.grpc.*;

public class Main {
    public static void main(String args[] ) throws Exception
    {
        System.out.println("hello wordl");
        Server server = ServerBuilder.forPort(5005).addService(new CurrencyExchangeService()).build();
        server.start();
        Thread.sleep(5000*1000);

    }
}
