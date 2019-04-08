import com.rabbitmq.client.*;

import java.io.IOException;

public class AdminCommunicator {
    private static String EXCHANGE_NAME = "EXCHANGE";
    private static String keyToAdmin = "TO_ADMIN";
    private static String keyFromAdmin;
    private static Channel channelToAdmin;

    public AdminCommunicator(String keyFromAdmin2) throws Exception{
        keyFromAdmin = "#." + keyFromAdmin2 + ".#";
        System.out.println("Creating admin communicator");
        channelCreation();

/*mamy wysylac i odbierac na roznych kanałach */
    }

    private static void channelCreation()throws Exception{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        channelToAdmin = connection.createChannel();
        channelToAdmin.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

        //bind queue
            //Actually queuename is useless , i need it only to basic consume
        String queueName = channelToAdmin.queueDeclare().getQueue();
            //key is something like TECHNICIAN.noAdmins
        channelToAdmin.queueBind(queueName,EXCHANGE_NAME,keyFromAdmin);

        Consumer consumer = new DefaultConsumer(channelToAdmin) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
               try{
                   Message m = new Message(body);
                   System.out.println("\u001B[33m"+
                           "Message from admin\n" +
                           m.getExamResults() +
                           "\u001B[0m");
               }catch (Exception e){
                    e.printStackTrace();
               }
            }
        };
        channelToAdmin.basicConsume(queueName,true,consumer);
    }

    public void sendToAdmin(byte[] toSend) throws Exception{
        channelToAdmin.basicPublish(EXCHANGE_NAME, keyToAdmin, null, toSend);
    }
}
