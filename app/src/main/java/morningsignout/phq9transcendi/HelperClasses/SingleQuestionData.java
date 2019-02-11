package morningsignout.phq9transcendi.HelperClasses;

class SingleQuestionData {
    private final String questionName;
    private final String questionText;
    private final String answerType;
    private final String categoryType;

    SingleQuestionData(String questionName, String categoryType, String answerType, String questionText) {
        this.questionName = questionName;
        this.categoryType = categoryType;
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

    String getCategoryType() {
        return categoryType;
    }
}
