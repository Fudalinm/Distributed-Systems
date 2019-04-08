import com.rabbitmq.client.*;

import java.io.*;

public class Technician {
    private static ExamTypes type1;
    private static ExamTypes type2;
    private static String EXCHANGE_NAME = "exchange10";
    private static Channel examChannel;
    /** Consumer like */
    public static void main(String[] argv) throws  Exception{
        /** Initialization */
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Give ONLY two out of three types which technician can examine\n" +
                "   K - Knee\n" +
                "   H - Hip\n" +
                "   E - Elbow");
        String types = br.readLine();
        if(types.length() == 2) {
            System.out.println(types.charAt(0));
            System.out.println(types.charAt(1));
            type1 = ExamTypes.fromCharacter(types.charAt(0));
            type2 = ExamTypes.fromCharacter(types.charAt(1));
        }else{
            System.out.println("Bad length types given");
            return;
        }
        if(type1 == ExamTypes.ERROR || type2 == ExamTypes.ERROR){
            System.out.println("Bad arguments given");
            return;
        }
        /** End of init */
        // connection & channel
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        examChannel = connection.createChannel();
        /** Change swap behaviour */
        examChannel.basicQos(1);

        //examChannel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        /** Init queue for each type that i can examine*/
            //1
        String queueName1 = type1.getValue();
        //String queueName = examChannel.queueDeclare().getQueue();
        //examChannel.queueBind(queueName1, EXCHANGE_NAME, type1.getValue());
        examChannel.queueDeclare(queueName1, false, false, false, null);
        System.out.println("created queue: " + queueName1);
            //2
        String queueName2 = type2.getValue();
        //String queueName2 = examChannel.queueDeclare().getQueue();
        //examChannel.queueBind(queueName2, EXCHANGE_NAME, type2.getValue());
        examChannel.queueDeclare(queueName2, false, false, false, null);
        System.out.println("created queue: " + queueName2);
        /** End of init queue*/

        /** Consumer creation */
        Consumer consumer = new DefaultConsumer(examChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                ByteArrayInputStream bis = new ByteArrayInputStream(body);
                ObjectInput in = null;
                in = new ObjectInputStream(bis);
                Message m;
                try {
                    m = (Message) in.readObject();
                    System.out.println(m.getPatientName());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

        /** Receive requests */
        System.out.println("Waiting for messages...");
        examChannel.basicConsume(queueName1, true, consumer);
        examChannel.basicConsume(queueName2, true, consumer);

        /** Respond request*/











    }
}
