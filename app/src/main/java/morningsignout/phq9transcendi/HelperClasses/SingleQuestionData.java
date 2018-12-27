package morningsignout.phq9transcendi.HelperClasses;

public class SingleQuestionData {
    private String questionName;
    private String questionText;

    public SingleQuestionData(String questionName, String questionText) {
        this.questionName = questionName;
        this.questionText = questionText;
    }

    public String getQuestionName() {
        return questionName;
    }

    public String getQuestionText() {
        return questionText;
    }
}
