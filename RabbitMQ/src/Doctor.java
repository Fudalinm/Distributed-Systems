import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.*;

public class Doctor {
    private static String doctorName;
    private static Channel examChannel;
    private static String EXCHANGE_NAME = "exchange10";
    private static String QUEUE_ELBOW = ExamTypes.ELBOW.getValue();
    private static String QUEUE_KNEE = ExamTypes.KNEE.getValue();
    private static String QUEUE_HIP = ExamTypes.HIP.getValue();
    /*Producer Like */
    public static void main(String[] argv) throws Exception{
        System.out.println("Hello I'm doctor give me MY name\n");
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
        //examChannel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        while(true){
            /** Collecting data to be sent */
            System.out.println("Give me your name");
            String patientName = br.readLine();
            System.out.println("Where should I send you for exam?\n" +
                    "   please provide one letter \n" +
                    "       H - hip\n" +
                    "       K - Knee\n" +
                    "       E - elbow\n" +
                    "       EXIT to exit\n");
            String typeOfExam = br.readLine();
            ExamTypes examType;
            if(typeOfExam.length() == 1){
                examType = ExamTypes.fromCharacter(typeOfExam.charAt(0));
                if(examType == ExamTypes.ERROR){
                    System.out.println("Bad type given try again try again\n");
                    continue;
                }
            }else{
                if(typeOfExam.equals("EXIT")){
                    System.out.println("Thank you goodbye \n");
                    /** Close */
                    examChannel.close();
                    connection.close();
                    return;
                }
                System.out.println("Bad type given try again try again\n");
                continue;
            }

            /** Creating message */
            Message m = new Message(patientName,examType);

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
