package morningsignout.phq9transcendi.HelperClasses;

import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

// Test cases do not rely on Context class to get excel sheet from Assets folder.
// Instead, they use a local dummy version in the root of the project.
class QuestionDataTest {
    private static final String TWO_QUESTION_TEST_FILE = "2_questions.csv";
    private static final String THREE_QUESTION_TEST_FILE = "3_questions.csv";
    private static final boolean IS_UNIT_TEST = true;

    // TODO: Replace dummy excel sheet with a mocked Apache POI that opens mock data.
    // Unit tests should only focus on logic of reading files and getting question text, not on
    // opening files and using Apache POI properly.
    @org.junit.jupiter.params.ParameterizedTest
    @CsvSource({
            TWO_QUESTION_TEST_FILE      + ", 2",
            THREE_QUESTION_TEST_FILE    + ", 3"
    })
    void numQuestionsIsCorrect(String filename, int correctNumQuestions) {
        QuestionData questionData = null;
        try {
            questionData = new QuestionData(IS_UNIT_TEST, filename);
        } catch (IOException e) {
            fail("QuestionData should not throw exception");
        }

        assertEquals(correctNumQuestions, questionData.size(),
                "Number of questions in spreadsheet should be " + correctNumQuestions);
    }

    @org.junit.jupiter.params.ParameterizedTest
    @CsvSource({
            "DUMMY, DUMMY QUESTION",
            "anhedoniainterest, Example Question 1",
            "guilt, Example Question 2"
            })
    void containsExpectedDummyQuestionNamesAndText(String questionName, String actualQuestionText) {
        QuestionData questionData = null;
        try {
            questionData = new QuestionData(IS_UNIT_TEST, THREE_QUESTION_TEST_FILE);
        } catch (IOException e) {
            fail("QuestionData should not throw exception");
        }

        assertEquals(actualQuestionText, questionData.getQuestionText(questionName));
    }
}