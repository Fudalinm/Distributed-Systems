import java.io.Serializable;

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
        //wynik to nazwa pacjenta + typ badania + „done”
        this.examResults = m.patientName + " " + m.examType + " DONE\n";
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
