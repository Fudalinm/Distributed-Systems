import java.io.*;

public class Message implements Serializable {
    private String patientName;
    private ExamTypes examType;
    private String doctorId;
    private String examResults;

    public Message(String patientName,ExamTypes exam,String docId){
        this.doctorId = docId;
        this.patientName = patientName;
        this.examType = exam;
        examResults = "";
    }
    public Message(Message m){
        this.doctorId = m.doctorId;
        this.patientName = m.patientName;
        this.examType = ExamTypes.RESULT;
        this.examResults = m.patientName + " " + m.examType + " DONE\n";
    }
    public Message(byte[] body) throws Exception{
        ByteArrayInputStream bis = new ByteArrayInputStream(body);
        ObjectInput in = null;
        in = new ObjectInputStream(bis);
        Message m = (Message) in.readObject();

        this.patientName = m.patientName;
        this.examType = m.examType;
        this.doctorId = m.doctorId;
        this.examResults = m.examResults;
    }
    public byte[] serialize() throws Exception{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        out = new ObjectOutputStream(bos);
        out.writeObject(this);
        out.flush();
        return bos.toByteArray();
    }
    public String getPatientName(){
        return this.patientName;
    }
    public String getExamResults(){
        return this.examResults;
    }
    public String getDoctorId(){
        return this.doctorId;
    }
    public ExamTypes getExamType(){
        return this.examType;
    }
}
