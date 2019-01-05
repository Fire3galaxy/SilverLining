package morningsignout.phq9transcendi.HelperClasses;

class SingleAnswerTypeData {
    final private String answerType;
    final private String[] answerValues;
    final private AnswerUITypeEnum answerUIType;

    SingleAnswerTypeData(String answerType, AnswerUITypeEnum answerUIType, String[] answerValues) {
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

    public AnswerUITypeEnum getAnswerUIType() {
        return answerUIType;
    }
}
