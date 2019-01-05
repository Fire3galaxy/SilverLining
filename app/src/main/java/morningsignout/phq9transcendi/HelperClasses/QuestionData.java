package morningsignout.phq9transcendi.HelperClasses;

import android.content.Context;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Daniel on 12/18/2016.
 * Intended to make adding questions much easier. No dependency in QuizActivity for particular
 * number or organization of questions except on this class and Scores (category of new question).
 *
 * Warning: Red flag question positions are FIXED in Scores class. If those positions change, then
 * you have to edit Scores class. They are also assumed to be consecutive.
 *
 * What to update when question is added: Every variable. If no new answer type is needed, there is
 * no need to add a new type. If so, add a new type and change the class constructor.
 * If you have more than 5 possible answers for a question, then RangeSliderTextAddOns,
 * activity_quiz.xml, and @style/[every styleName]RangeSlider need to be updated to accommodate this.
 */
public class QuestionData {
    // Appended to saved scores string to ensure string corresponds with this order of questions
    // It could change in the future.
    static public final int VERSION_OF_ORDER_NUM = 2;

    static public final int NUM_QUESTIONS = 27; // total number of questions

    // Index where red flag starts
    static public final int RED_FLAG_QUESTION = 16;

    // Category index that red flags begin (All questions that contribute to score are below red flag)
    static public final int RED_FLAG_CATEGORY = 9;

    // index constants for email message in results activity
    public static final int FAMILY_UNDERSTANDS = 21;
    public static final int FAMILY_SITUATION = 22;
    public static final int CULTURAL_BACKGROUND = 23;
    public static final int I_APPOINTMENT = 24;

    // Array of which category each question is associated with
    public static final int[] categoryIndices = {
            0, 0, 1, 1, 2, 2, 3, 4, 4, 5, 6, 6, 7, 7, 8, 8, 9, 9, 9, 9, 9, 10, 11, 12, 13, 14, 15
    };
    // Categories of questions
    public static final String[] categoryNames = {
            "anhedonia", "mood", "sleep-disturbance", "energy", "appetite", "guilt", "cognition-concentration",
            "psychomotor", "suicide", "red-flag", "familyunderstands", "familysituation", "culturalbackground",
            "i_appointment", "fearofstranger", "i_adequateresources"
    };

    // Score value that would trigger a red flag alert (from 0-1)
    public static final int[] redFlagThreshold = {
            1, 1, 1, 1, 1
    };

    static public final boolean[] USES_SLIDER = {
            true,  // 0
            true,  // 1
            true,  // 2
            true,  // 3
            true,  // 4
            true,  // 5
            true,  // 6
            true,  // 7
            true,  // 8
            true,  // 9
            true,  // 10
            true,  // 11
            true,  // 12
            true,  // 13
            true,  // 14
            true,  // 15
            false, // 16
            false, // 17
            false, // 18
            false, // 19
            false, // 20
            true,  // 21
            true,  // 22
            true,  // 23
            true,  // 24
            true,  // 25
            true   // 26
    };

    // Relevant question data variables
    // Using names instead of numbers for FireBase on the off-chance that
    // the order of questions is not fixed (would be bad for database to use #).
    //
    // Ordered by current order of questions, arranged by category
    static final String[] questionNames = {
            // 1-16 (normal)
            "anhedoniainterest", "anhedoniaenjoy",
            "mooddepress", "moodhopeless",
            "sleeplow", "sleephigh",
            "fatigue",
            "appetitelow", "appetitehigh",
            "guilt",
            "concentrationpoor", "concentrationdistracted",
            "psychomotorslowself", "psychomotorslowother",
            "suicidalityactive", "suicidalitypassive",

            // 17-21 (red flag)
            "continuousdepression_flag", "longdepression_flag", "interference", "suicidality_flag",
            "suicideaction_flag",

            // 22-27 (research)
            "familyunderstands", "familysituation", "culturalbackground", "i_appointment",
            "fearofstranger", "i_adequateresources"
    };

    private enum QuestionsHeaders {
        questionName("Question Name"),
        categoryType("Category Type"),
        answerType  ("Answer Type"),
        questionText("Question Text");

        private String readableName;

        QuestionsHeaders(String s) {
            readableName = s;
        }

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

    // Note: DO NOT CHANGE THIS FILE NAME WHEN UPDATING CSV. This is hardcoded.
    private static final String QUESTION_SPREADSHEET_NAME = "questions.csv";
    private static final String ANSWER_SPREADSHEET_NAME = "answers.csv";
    private LinkedList<SingleQuestionData> questionList;

    private HashMap<String, SingleAnswerTypeData> answerMap;
    private boolean isUnitTest;
    private Context context;
    public QuestionData(Context context) throws IOException {
        this(context, false, QUESTION_SPREADSHEET_NAME);
    }

    // Special constructor for custom questions for unit tests.
    QuestionData(boolean isUnitTest, String filename) throws IOException {
        this(null, isUnitTest, filename);
    }
    private QuestionData(Context context, boolean isUnitTest, String filename) throws IOException {
        this.context = context;
        this.isUnitTest = isUnitTest;
        this.questionList = new LinkedList<>();
        this.answerMap = new HashMap<>();

        loadQuestionDataFromSpreadsheet(filename);
        loadAnswerDataFromSpreadsheet();
    }

    private void loadAnswerDataFromSpreadsheet() throws IOException {
        Reader in = getAnswersReader();
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);

        for (CSVRecord record : records) {
            String answerType = record.get(AnswersHeaders.answerType);
            AnswerUITypeEnum answerUIType = AnswerUITypeEnum.valueOf(
                    AnswerUITypeEnum.class, record.get(AnswersHeaders.answerUIType));
            String[] answerArray = getAnswerArray(record);
            SingleAnswerTypeData answerData = new SingleAnswerTypeData(answerType, answerUIType, answerArray);

            answerMap.put(answerType, answerData);
        }
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
        Reader in = getQuestionsReader(filename);
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);

        for (CSVRecord record : records) {
            String questionName = record.get(QuestionsHeaders.questionName);
            String answerType   = record.get(QuestionsHeaders.answerType);
            String questionText = record.get(QuestionsHeaders.questionText);

            questionList.add(new SingleQuestionData(questionName, answerType, questionText));
        }
    }

    private Reader getQuestionsReader(String filename) throws IOException {
        if (this.isUnitTest)
            return new FileReader(filename);

        if (this.context == null)
            throw new IllegalStateException("Null value passed in for context");

        return new InputStreamReader(this.context.getAssets().open(filename));
    }

    private Reader getAnswersReader() throws IOException {
        if (this.isUnitTest)
            return new FileReader(ANSWER_SPREADSHEET_NAME);

        if (this.context == null)
            throw new IllegalStateException("Null value passed in for context");

        return new InputStreamReader(this.context.getAssets().open(ANSWER_SPREADSHEET_NAME));
    }

    String getQuestionName(int i) {
        return questionList.get(i).getQuestionName();
    }

    public String getQuestionText(int i) {
        return questionList.get(i).getQuestionText();
    }

    public String getAnswerType(int i) {
        return questionList.get(i).getAnswerType();
    }

    public int size() {
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
}
