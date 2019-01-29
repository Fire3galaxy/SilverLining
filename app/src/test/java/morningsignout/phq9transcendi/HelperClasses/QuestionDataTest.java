package morningsignout.phq9transcendi.HelperClasses;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

// Test cases do not rely on Context class to get excel sheet from Assets folder.
// Instead, they use a local dummy version in the root of the project.
class QuestionDataTest {
    private static final String TWO_QUESTION_TEST_FILE = "2_questions.csv";
    private static final String THREE_QUESTION_TEST_FILE = "3_questions.csv";
    private static final String ANSWER_TEST_FILE = "answers.csv";
    private static final String CONFIG_TEST_FILE = "config.csv";
    private static final boolean IS_UNIT_TEST = true;

    /* Basically all the values corresponding to THREE_QUESTION_TEST_FILE are here. This
     * level of hard-coding "kinda" makes sense - after all, no one should be changing the dummy
     * values, so these tests should be able to assume the data is correct.
     */
    private static final String[] THREE_QUESTION_NAMES = new String[]{
            "DUMMY",
            "anhedoniainterest",
            "guilt"
    };
    private static final String[] THREE_QUESTION_TEXT = new String[]{
            "DUMMY QUESTION",
            "Example Question 1",
            "Example Question 2"
    };
    private static final String[] THREE_QUESTION_ANSWER_TYPES = new String[]{
            "DUMMY",
            "NORMAL",
            "SUPPORTIVE"
    };
    private static final int QUESTION_ORDERING_VERSION = 42;

    private static QuestionData defaultTestQuestionData;

    @BeforeAll
    static void setUp() {
        // Make sure test files exist
        File twoQuestionFile = new File(TWO_QUESTION_TEST_FILE);
        File threeQuestionFile = new File(THREE_QUESTION_TEST_FILE);
        File answerFile = new File(ANSWER_TEST_FILE);
        File configFile = new File(CONFIG_TEST_FILE);

        assertTrue(twoQuestionFile.exists());
        assertTrue(threeQuestionFile.exists());
        assertTrue(answerFile.exists());
        assertTrue(configFile.exists());

        defaultTestQuestionData = null;
        try {
            defaultTestQuestionData = new QuestionData(IS_UNIT_TEST, THREE_QUESTION_TEST_FILE);
        } catch (IOException e) {
            fail("QuestionData should not throw exception: " + e.getMessage());
        }
    }

    // TODO: Replace dummy excel sheet with a mocked Apache POI that opens mock data.
    // Unit tests should only focus on logic of reading files and getting question text, not on
    // opening files and using Apache POI properly.
    @org.junit.jupiter.params.ParameterizedTest
    @CsvSource({
            TWO_QUESTION_TEST_FILE      + ", 2",
            THREE_QUESTION_TEST_FILE    + ", 3"
    })
    void questionsLength_numQuestionsIsCorrect(String filename, int correctNumQuestions) {
        QuestionData questionData = null;
        try {
            questionData = new QuestionData(IS_UNIT_TEST, filename);
        } catch (IOException e) {
            fail("QuestionData should not throw exception");
        }

        assertEquals(correctNumQuestions, questionData.questionsLength(),
                "Number of questions in spreadsheet should be " + correctNumQuestions);
    }

    @org.junit.jupiter.api.Test
    void getQuestionText_containsExpectedDummyQuestionNamesInCorrectOrder() {
        for (int i = 0; i < THREE_QUESTION_NAMES.length; i++) {
            assertEquals(THREE_QUESTION_NAMES[i], defaultTestQuestionData.getQuestionName(i));
        }
    }

    @org.junit.jupiter.api.Test
    void getQuestionText_containsExpectedDummyQuestionTextInCorrectOrder() {
        for (int i = 0; i < THREE_QUESTION_TEXT.length; i++) {
            assertEquals(THREE_QUESTION_TEXT[i],  defaultTestQuestionData.getQuestionText(i));
        }
    }

    @org.junit.jupiter.api.Test
    void getAnswerType_containsExpectedAnswerTypesInCorrectOrder() {
        for (int i = 0; i < THREE_QUESTION_ANSWER_TYPES.length; i++) {
            assertEquals(THREE_QUESTION_ANSWER_TYPES[i], defaultTestQuestionData.getAnswerType(i));
        }
    }

    @org.junit.jupiter.params.ParameterizedTest
    @ValueSource(strings = {
            "NORMAL, Not at all, One or two days a week, Three to four days a week, Everyday",
            "SUPPORTIVE, Very poor, Poor, Okay, Good, Very good",
            "YES/NO, Yes, No"
    })
    void getAnswerValues_containsExpectedAnswerValues(
            @ConvertWith(ToAnswerTypeDataWithoutUIType.class) SingleAnswerTypeData singleAnswerTypeData) {
        String[] actualAnswerValues = defaultTestQuestionData.getAnswerValues(singleAnswerTypeData.getAnswerType());

        for (int i = 0; i < singleAnswerTypeData.getAnswerValues().length; i++) {
            assertEquals(singleAnswerTypeData.getAnswerValues()[i], actualAnswerValues[i]);
        }
    }

    @org.junit.jupiter.params.ParameterizedTest
    @ValueSource(strings = {"DUMMY"})
    void getAnswerValues_invalidAnswerType(String answerType) {
        assertNull(defaultTestQuestionData.getAnswerValues(answerType));
    }

    @org.junit.jupiter.params.ParameterizedTest
    @ValueSource(strings = {
            "NORMAL, Not at all, One or two days a week, Three to four days a week, Everyday",
            "SUPPORTIVE, Very poor, Poor, Okay, Good, Very good",
            "YES/NO, Yes, No"
    })
    void getAnswerValuesLength_isExpectedLength(
            @ConvertWith(ToAnswerTypeDataWithoutUIType.class) SingleAnswerTypeData singleAnswerTypeData) {
        String[] actualAnswerVals = defaultTestQuestionData.getAnswerValues(singleAnswerTypeData.getAnswerType());
        int actualAnswerValsLength = actualAnswerVals.length;

        assertEquals(singleAnswerTypeData.getAnswerValues().length, actualAnswerValsLength);
    }

    @org.junit.jupiter.params.ParameterizedTest
    @CsvSource({
            "NORMAL, RadioButtons",
            "SUPPORTIVE, RadioButtons",
            "YES/NO, StandardButtons"
    })
    void getAnswerUIType_isExpectedUIType(String answerType, String UIType) {
        AnswerUITypeEnum expectedUIType = AnswerUITypeEnum.valueOf(AnswerUITypeEnum.class, UIType);
        AnswerUITypeEnum actualUIType = defaultTestQuestionData.getAnswerUIType(answerType);

        assertEquals(expectedUIType, actualUIType, answerType + " has wrong UI type");
    }

    @org.junit.jupiter.api.Test
    void getVersionOfQuestionOrder_isCorrectVersion() {
        final int versionNum = defaultTestQuestionData.getVersionOfQuestionOrder();

        assertEquals(QUESTION_ORDERING_VERSION, versionNum);
    }
}