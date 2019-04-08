import java.io.Serializable;

public class Message implements Serializable {
    private String patientName;
    private ExamTypes examType;

    public Message(String patientName,ExamTypes exam){
        this.patientName = patientName;
        this.examType = exam;
    }
    public String getPatientName(){
        return this.patientName;
    }
}
