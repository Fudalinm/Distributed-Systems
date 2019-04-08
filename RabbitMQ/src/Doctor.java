import com.rabbitmq.client.*;

import java.io.*;

public class Doctor {
    private static String doctorName;
    private static Connection connection1;
    private static Connection connection2;
    private static Channel examChannel;
    private static Channel resultChannel;
    private static String QUEUE_ELBOW = ExamTypes.ELBOW.getValue();
    private static String QUEUE_KNEE = ExamTypes.KNEE.getValue();
    private static String QUEUE_HIP = ExamTypes.HIP.getValue();
    private static AdminCommunicator adminCommunicator;

    public static void main(String[] argv) throws Exception{
        initDoc();
        adminCommunicator = new AdminCommunicator();
        handleDoc();
    }
    private static void initDoc() throws Exception{
        System.out.println("Hello I'm doctor give me MY name");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        doctorName = br.readLine();

        /** Queue initialization for exam*/
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection1 = factory.newConnection();
        examChannel = connection1.createChannel();

        examChannel.queueDeclare(QUEUE_ELBOW,false,false,false,null);
        examChannel.queueDeclare(QUEUE_KNEE,false,false,false,null);
        examChannel.queueDeclare(QUEUE_HIP,false,false,false,null);

        /** Result queue init*/
        connection2 = factory.newConnection();
        resultChannel = connection2.createChannel();
        String RESULT_QUEUE = doctorName;
        resultChannel.queueDeclare(RESULT_QUEUE,false,false,false,null);

        Consumer consumer = new DefaultConsumer(resultChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                try {
                    Message m = new Message(body);
                    System.out.println(
                            "\u001B[34m" +
                            "#############################################################\n"+
                            "Got new results : \n" + m.getExamResults()+
                            "#############################################################" +
                            "\u001B[0m");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        resultChannel.basicConsume(RESULT_QUEUE, true, consumer);
    }
    private static void handleDoc() throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while(true){
            /** Collecting data to be sent */
            System.out.println("Give me your name");
            String patientName = br.readLine();
            System.out.println("Where should I send you for exam?\n" +
                    "   please provide one letter \n" +
                    "       H - hip\n" +
                    "       K - Knee\n" +
                    "       E - elbow\n" +
                    "       EXIT to exit");
            String typeOfExamGiven = br.readLine();
            ExamTypes examType;
            if(typeOfExamGiven.length() == 1){
                examType = ExamTypes.fromCharacter(typeOfExamGiven.charAt(0));
                if(examType == ExamTypes.ERROR){
                    System.out.println("Bad type given try again");
                    continue;
                }
            }else{
                if(typeOfExamGiven.equals("EXIT")){
                    System.out.println("Thank you goodbye");
                    examChannel.close();
                    connection1.close();
                    resultChannel.close();
                    connection2.close();
                    System.exit(0);
                }
                System.out.println("Bad type given try again try again");
                continue;
            }
            /** Creating message */
            Message mToSend = new Message(patientName,examType,doctorName);
            /** Send bytes */
            examChannel.basicPublish("",examType.getValue(),null,mToSend.serialize());
        }
    }
}
