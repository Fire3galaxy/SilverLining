package morningsignout.phq9transcendi.HelperClasses;

class AnswerTypeData {
    final private String answerType;
    final private String[] answerValues;

    AnswerTypeData(String answerType, String[] answerValues) {
        this.answerType = answerType;
        this.answerValues = answerValues;
    }

    String getAnswerType() {
        return answerType;
    }

    String[] getAnswerValues() {
        return answerValues;
    }
}
