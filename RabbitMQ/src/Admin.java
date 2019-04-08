import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Admin {
    private static String EXCHANGE_NAME = "EXCHANGE";
    private static String keyToAdmin = "TO_ADMIN";

    private static String DOC_GROUP = "DOCTOR";
    private static String TECH_GROUP = "TECHNICIAN";
    private static String NO_ADMINS = "DOCTOR.TECHNICIAN";
    private static Channel channel;
    private static int logCounter = 0;

    public static void main(String[] argv) throws Exception{
        init();
        handleAdmin();
    }

    private static void init() throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        //bind queue
        //Actually queuename is useless , i need it only to basic consume
        String queueName = channel.queueDeclare().getQueue();
        //key is something like TECHNICIAN.noAdmins
        channel.queueBind(queueName,EXCHANGE_NAME,keyToAdmin);
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                try{
                    Message m = new Message(body);
                    System.out.println("\u001B[33m"+
                            logCounter +
                            " LOG to admin\n " +
                            " Type: " + m.getExamType() +
                            " Result: " + m.getExamResults() +
                            "\u001B[0m");
                    logCounter++;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        channel.basicConsume(queueName,true,consumer);
    }
    private static void handleAdmin() throws Exception{
        while(true){
            System.out.println("Hello I'm Admin select to who you want to send info" +
                    "   T - Technicians" +
                    "   D - Doctors" +
                    "   A - All");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String where = br.readLine();
            System.out.println("What you want to send?");
            String info = br.readLine();
            sendInfo(info,where);
        }
    }
    private static void sendInfo(String info,String where) throws Exception{
        Message m = new Message(info);
        switch (where.toUpperCase()){
            case "T":
                channel.basicPublish(EXCHANGE_NAME,TECH_GROUP,null,m.serialize());
                break;
            case "D":
                channel.basicPublish(EXCHANGE_NAME,DOC_GROUP,null,m.serialize());
                break;
            case "A":
                channel.basicPublish(EXCHANGE_NAME,NO_ADMINS,null,m.serialize());
                break;
            default: System.out.println("Unknown type Error try again");
        }


    }
}
