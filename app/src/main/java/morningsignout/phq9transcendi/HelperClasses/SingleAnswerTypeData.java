package morningsignout.phq9transcendi.HelperClasses;

class SingleAnswerTypeData {
    final private String answerType;
    final private String[] answerValues;
    final private String answerUIType;

    SingleAnswerTypeData(String answerType, String answerUIType, String[] answerValues) {
        this.answerType = answerType;
        this.answerUIType = answerUIType;
        this.answerValues = answerValues;
    }

    String getAnswerType() {
        return answerType;
    }

    String[] getAnswerValues() {
        return answerValues;
    }

    public String getAnswerUIType() {
        return answerUIType;
    }
}
