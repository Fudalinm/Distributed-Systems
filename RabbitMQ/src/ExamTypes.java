public enum ExamTypes {
    HIP("H"),
    KNEE("K"),
    ELBOW("E"),
    ERROR("ERROR"),
    RESULT("RESULT"),
    ADMIN_MESSAGE("ADMIN_MESSAGE")
    ;

    private String value;

    ExamTypes(String valInString){
        this.value = valInString;
    }

    public String getValue(){
        return this.value;
    }

    public static ExamTypes fromCharacter(Character c){
        switch (c){
            case 'K':
                return ExamTypes.KNEE;
            case 'H':
                return ExamTypes.HIP;
            case 'E':
                return ExamTypes.ELBOW;
            default:
                return ExamTypes.ERROR;
        }
    }

}
