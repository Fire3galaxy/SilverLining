package morningsignout.phq9transcendi.HelperClasses;

import android.content.Context;
import android.support.annotation.NonNull;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by Daniel on 12/18/2016.
 *
 * To update questions for some new version of the PHQ-9, just modify the questions.csv file in the
 * assets folder. Questions? Email/message me (ask MSO boss for my contact info)!
 */
public class QuestionData {
    // Special questions used for email in ResultsActivity. Names are hardcoded.
    private static final String I_APPOINTMENT_NAME = "i_appointment";
    private static final String CULTURAL_BACKGROUND_NAME = "culturalbackground";
    private static final String FAMILY_SITUATION_NAME = "familysituation";
    private static final String FAMILY_UNDERSTANDS_NAME = "familyunderstands";

    private enum QuestionsHeaders {
        questionName("Question Name"),
        categoryType("Category Type"),
        answerType  ("Answer Type"),
        questionText("Question Text");

        private String readableName;

        QuestionsHeaders(String s) {
            readableName = s;
        }

        @NonNull
        @Override
        public String toString() {
            return readableName;
        }
    }

    private enum AnswersHeaders {
        answerType("Answer Type"),
        answer1("Answer 1"),
        answer2("Answer 2"),
        answer3("Answer 3"),
        answer4("Answer 4"),
        answer5("Answer 5"),
        answerUIType("UI Type");

        private String readableName;

        AnswersHeaders(String s) {
            readableName = s;
        }

        @Override
        public String toString() {
            return readableName;
        }
    }

    private enum ConfigHeaders {
        questionOrderVers("Question Ordering Version"),
        finalScoreCat("Categories that Contribute to Final Score");

        private String readableName;

        ConfigHeaders(String s) {
            readableName = s;
        }

        @Override
        public String toString() {
            return readableName;
        }
    }

    // Note: DO NOT CHANGE THIS FILE NAME WHEN UPDATING CSV. This is hardcoded.
    private static final String QUESTION_SPREADSHEET_NAME = "questions.csv";
    private static final String ANSWER_SPREADSHEET_NAME = "answers.csv";
    private static final String CONFIG_SPREADSHEET_NAME = "config.csv";

    private LinkedList<SingleQuestionData> questionList;
    private HashMap<String, SingleAnswerTypeData> answerMap;
    private HashSet<String> finalScoreCategories;
    private boolean isUnitTest;
    private Context context;
    private int questionOrderingVersion;

    public QuestionData(Context context) throws IOException {
        this(context, QUESTION_SPREADSHEET_NAME);
    }

    // Special constructor for custom questions for unit tests.
    QuestionData(String unitTestQuestionFilename) throws IOException {
        this(null, unitTestQuestionFilename);
    }

    private QuestionData(Context context, String questionFilename) throws IOException {
        this.context = context;
        this.isUnitTest = false;
        this.questionList = new LinkedList<>();
        this.answerMap = new HashMap<>();
        this.finalScoreCategories = new HashSet<>();

        if (context == null)
            this.isUnitTest = true;

        loadQuestionDataFromSpreadsheet(questionFilename);
        loadAnswerDataFromSpreadsheet();
        loadConfigurationFromSpreadsheet();
    }

    private void loadConfigurationFromSpreadsheet() throws IOException {
        Iterable<CSVRecord> records = getConfigRecords();
        CSVRecord firstRecord = records.iterator().next();

        // Get questionOrderingVersion from first row
        questionOrderingVersion = Integer.parseInt(firstRecord.get(ConfigHeaders.questionOrderVers));

        // Get final score categories from first row
        finalScoreCategories.add(firstRecord.get(ConfigHeaders.finalScoreCat));

        // Get rest of final score categories from second row onward
        for (CSVRecord record : records) {
            finalScoreCategories.add(record.get(ConfigHeaders.finalScoreCat));
        }
    }

    private void loadAnswerDataFromSpreadsheet() throws IOException {
        Iterable<CSVRecord> records = getAnswersRecords();

        for (CSVRecord record : records) {
            String answerType = record.get(AnswersHeaders.answerType);
            AnswerUITypeEnum answerUIType = getAnswerUITypeEnum(record);
            String[] answerArray = getAnswerArray(record);
            SingleAnswerTypeData answerData = new SingleAnswerTypeData(answerType, answerUIType, answerArray);

            answerMap.put(answerType, answerData);
        }
    }

    private AnswerUITypeEnum getAnswerUITypeEnum(CSVRecord record) {
        String answerUITypeStr = record.get(AnswersHeaders.answerUIType);
        return AnswerUITypeEnum.valueOf(
                AnswerUITypeEnum.class, answerUITypeStr);
    }

    private String[] getAnswerArray(CSVRecord record) {
        String[] answerArray = new String[determineAnswerArraySize(record)];

        // Using switch case with falling-through mechanic so I don't have large if statement bodies.
        switch (answerArray.length) {
            case 5:
                answerArray[4] = record.get(AnswersHeaders.answer5);
            case 4:
                answerArray[3] = record.get(AnswersHeaders.answer4);
            case 3:
                answerArray[2] = record.get(AnswersHeaders.answer3);
            case 2:
                answerArray[1] = record.get(AnswersHeaders.answer2);
            case 1:
                answerArray[0] = record.get(AnswersHeaders.answer1);
                break;
        }

        return answerArray;
    }

    private int determineAnswerArraySize(CSVRecord record) {
        if (!record.get(AnswersHeaders.answer5).isEmpty())
            return 5;
        if (!record.get(AnswersHeaders.answer4).isEmpty())
            return 4;
        if (!record.get(AnswersHeaders.answer3).isEmpty())
            return 3;
        if (!record.get(AnswersHeaders.answer2).isEmpty())
            return 2;

        throw new IllegalStateException("Should not have a question with less than two or more than six answers.");
    }

    private void loadQuestionDataFromSpreadsheet(String filename) throws IOException {
        Iterable<CSVRecord> records = getQuestionsRecords(filename);

        for (CSVRecord record : records) {
            String questionName = record.get(QuestionsHeaders.questionName);
            String categoryType = record.get(QuestionsHeaders.categoryType);
            String answerType   = record.get(QuestionsHeaders.answerType);
            String questionText = record.get(QuestionsHeaders.questionText);

            questionList.add(
                    new SingleQuestionData(questionName, categoryType, answerType, questionText));
        }
    }

    private Iterable<CSVRecord> getCSVRecords(String filename) throws IOException {
        if (this.isUnitTest) {
            Reader in = new FileReader(filename);
            return CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
        }

        if (this.context == null) {
            throw new IllegalStateException("Null value passed in for context");
        }

        Reader in = new InputStreamReader(context.getAssets().open(filename));
        return CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
    }

    private Iterable<CSVRecord> getQuestionsRecords(String filename) throws IOException {
        return getCSVRecords(filename);
    }

    private Iterable<CSVRecord> getAnswersRecords() throws IOException {
        return getCSVRecords(ANSWER_SPREADSHEET_NAME);
    }

    private Iterable<CSVRecord> getConfigRecords() throws IOException {
        return getCSVRecords(CONFIG_SPREADSHEET_NAME);
    }

    String getQuestionName(int i) {
        return questionList.get(i).getQuestionName();
    }

    public String getQuestionText(int i) {
        return questionList.get(i).getQuestionText();
    }

    public String getQuestionCategoryType(int i) {
        return questionList.get(i).getCategoryType();
    }

    public String getAnswerType(int i) {
        return questionList.get(i).getAnswerType();
    }

    public int questionsLength() {
        return questionList.size();
    }

    public String[] getAnswerValues(String answerType) {
        if (!answerMap.containsKey(answerType))
            return null;

        return answerMap.get(answerType).getAnswerValues();
    }

    public AnswerUITypeEnum getAnswerUIType(String answerType) {
        if (!answerMap.containsKey(answerType))
            return null;

        return answerMap.get(answerType).getAnswerUIType();
    }

    public int getVersionOfQuestionOrder() {
        return questionOrderingVersion;
    }

    public Set<String> getFinalScoreCategories() {
        return finalScoreCategories;
    }

    public int getIndex_iAppointment() throws IllegalStateException {
        return getSpecialQuestionIndex(I_APPOINTMENT_NAME);
    }

    public int getIndex_culturalBackground() throws IllegalStateException {
        return getSpecialQuestionIndex(CULTURAL_BACKGROUND_NAME);
    }

    public int getIndex_familySituation() throws IllegalStateException {
        return getSpecialQuestionIndex(FAMILY_SITUATION_NAME);
    }

    public int getIndex_familyUnderstands() throws IllegalStateException {
        return getSpecialQuestionIndex(FAMILY_UNDERSTANDS_NAME);
    }

    private int getSpecialQuestionIndex(String questionName) {
        for (int i = 0; i < questionList.size(); i++) {
            SingleQuestionData sqd = questionList.get(i);
            if (sqd.getQuestionName().equals(questionName)) {
                return i;
            }
        }

        throw new IllegalStateException(
                "Specially categorized question not in passed-in CSV. Was this intentional?");
    }
}
