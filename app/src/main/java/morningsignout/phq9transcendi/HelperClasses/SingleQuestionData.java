package morningsignout.phq9transcendi.HelperClasses;

class SingleQuestionData {
    private final String questionName;
    private final String questionText;
    private final String answerType;

    SingleQuestionData(String questionName, String answerType, String questionText) {
        this.questionName = questionName;
        this.answerType = answerType;
        this.questionText = questionText;
    }

    String getQuestionName() {
        return questionName;
    }

    String getAnswerType() {
        return answerType;
    }

    String getQuestionText() {
        return questionText;
    }
}
