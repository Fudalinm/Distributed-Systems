import com.rabbitmq.client.*;

import java.io.*;

public class Doctor {
    private static String doctorName;
    private static Channel examChannel;
    private static Channel resultChannel;
    //private static String EXCHANGE_NAME = "exchange10";
    private static String QUEUE_ELBOW = ExamTypes.ELBOW.getValue();
    private static String QUEUE_KNEE = ExamTypes.KNEE.getValue();
    private static String QUEUE_HIP = ExamTypes.HIP.getValue();
    /*Producer Like */
    public static void main(String[] argv) throws Exception{
        System.out.println("Hello I'm doctor give me MY name");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        doctorName = br.readLine();

        /** I need to prepare to receive response from technician*/

        /** Queue initialization for exam*/
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        examChannel = connection.createChannel();

        examChannel.queueDeclare(QUEUE_ELBOW,false,false,false,null);
        examChannel.queueDeclare(QUEUE_KNEE,false,false,false,null);
        examChannel.queueDeclare(QUEUE_HIP,false,false,false,null);

        /** exchange */
        /** exchange only for admin purpose */

        /** Result queue init*/
        Connection connection2 = factory.newConnection();
        resultChannel = connection2.createChannel();
        String RESULT_QUEUE = doctorName;
        resultChannel.queueDeclare(RESULT_QUEUE,false,false,false,null);

        Consumer consumer = new DefaultConsumer(resultChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                ByteArrayInputStream bis = new ByteArrayInputStream(body);
                ObjectInput in = null;
                in = new ObjectInputStream(bis);
                Message m;
                try {
                    m = (Message) in.readObject();
                    System.out.println("Got new results : \n" + m.getExamResults());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        resultChannel.basicConsume(RESULT_QUEUE, true, consumer);

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
            String typeOfExam = br.readLine();
            ExamTypes examType;
            if(typeOfExam.length() == 1){
                examType = ExamTypes.fromCharacter(typeOfExam.charAt(0));
                if(examType == ExamTypes.ERROR){
                    System.out.println("Bad type given try again try again");
                    continue;
                }
            }else{
                if(typeOfExam.equals("EXIT")){
                    System.out.println("Thank you goodbye");
                    /** Close */
                    examChannel.close();
                    connection.close();
                    return;
                }
                System.out.println("Bad type given try again try again");
                continue;
            }

            /** Creating message */
            Message m = new Message(patientName,examType,doctorName);

            /** Serializing */
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = null;
            out = new ObjectOutputStream(bos);
            out.writeObject(m);
            out.flush();
            byte[] bytesToSend = bos.toByteArray();

            /** Send bytes */
            examChannel.basicPublish("",examType.getValue(),null,bytesToSend);

        }



    }
}
