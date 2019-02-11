package morningsignout.phq9transcendi.HelperClasses;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

// Test cases do not rely on Context class to get excel sheet from Assets folder.
// Instead, they use a local dummy version in the root of the project.
class QuestionDataTest {
    private static final String TWO_QUESTION_TEST_FILE = "2_questions.csv";
    private static final String THREE_QUESTION_TEST_FILE = "3_questions.csv";
    private static final String ANSWER_TEST_FILE = "answers.csv";
    private static final String CONFIG_TEST_FILE = "config.csv";
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
            defaultTestQuestionData = new QuestionData(THREE_QUESTION_TEST_FILE);
        } catch (IOException e) {
            fail("QuestionData should not throw exception: " + e.getMessage());
        }
    }

    // TODO: Replace dummy excel sheet with a mocked Apache POI that opens mock data.
    // Unit tests should only focus on logic of reading files and getting question text, not on
    // opening files and using Apache POI properly.
    @ParameterizedTest
    @CsvSource({
            TWO_QUESTION_TEST_FILE      + ", 2",
            THREE_QUESTION_TEST_FILE    + ", 3"
    })
    void questionsLength_numQuestionsIsCorrect(String filename, int correctNumQuestions) {
        QuestionData questionData = null;
        try {
            questionData = new QuestionData(filename);
        } catch (IOException e) {
            fail("QuestionData should not throw exception");
        }

        assertEquals(correctNumQuestions, questionData.questionsLength(),
                "Number of questions in spreadsheet should be " + correctNumQuestions);
    }

    @Test
    void getQuestionText_containsExpectedDummyQuestionNamesInCorrectOrder() {
        final String[] threeQuestionNames = new String[]{
                "DUMMY",
                "anhedoniainterest",
                "guilt"
        };

        for (int i = 0; i < threeQuestionNames.length; i++) {
            assertEquals(threeQuestionNames[i], defaultTestQuestionData.getQuestionName(i));
        }
    }

    @Test
    void getQuestionText_containsExpectedDummyQuestionTextInCorrectOrder() {
        final String[] threeQuestionText = new String[]{
                "DUMMY QUESTION",
                "Example Question 1",
                "Example Question 2"
        };

        for (int i = 0; i < threeQuestionText.length; i++) {
            assertEquals(threeQuestionText[i],  defaultTestQuestionData.getQuestionText(i));
        }
    }

    @Test
    void getAnswerType_containsExpectedAnswerTypesInCorrectOrder() {
        final String[] threeQuestionAnswerTypes = new String[]{
                "DUMMY",
                "NORMAL",
                "SUPPORTIVE"
        };

        for (int i = 0; i < threeQuestionAnswerTypes.length; i++) {
            assertEquals(threeQuestionAnswerTypes[i], defaultTestQuestionData.getAnswerType(i));
        }
    }

    @ParameterizedTest
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

    @ParameterizedTest
    @ValueSource(strings = {"DUMMY"})
    void getAnswerValues_invalidAnswerType(String answerType) {
        assertNull(defaultTestQuestionData.getAnswerValues(answerType));
    }

    @ParameterizedTest
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

    @ParameterizedTest
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

    @Test
    void getVersionOfQuestionOrder_isCorrectVersion() {
        final int expectedVersionNum = 49;
        final int actualVersionNum = defaultTestQuestionData.getVersionOfQuestionOrder();

        assertEquals(expectedVersionNum, actualVersionNum);
    }

    @Test
    void getQuestionCategoryType_isCorrectCategoryType() {
        String dummyCategoryType = "DUMMY";         // question 0
        String anhedoniaCategoryType = "anhedonia"; // question 1
        String guiltCategoryType = "guilt";         // question 2

        assertEquals(dummyCategoryType, defaultTestQuestionData.getQuestionCategoryType(0));
        assertEquals(anhedoniaCategoryType, defaultTestQuestionData.getQuestionCategoryType(1));
        assertEquals(guiltCategoryType, defaultTestQuestionData.getQuestionCategoryType(2));
    }

    @Test
    void getFinalScoreCategories_isCorrectSet() {
        String dummyCategoryType = "DUMMY";         // Should not be in
        String anhedoniaCategoryType = "anhedonia"; // Should be in
        String guiltCategoryType = "guilt";         // Should be in
        Set<String> finalScoreCategories = defaultTestQuestionData.getFinalScoreCategories();

        assertFalse(finalScoreCategories.contains(dummyCategoryType));
        assertTrue(finalScoreCategories.contains(anhedoniaCategoryType));
        assertTrue(finalScoreCategories.contains(guiltCategoryType));
    }
}