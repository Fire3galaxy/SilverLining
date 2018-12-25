package morningsignout.phq9transcendi.HelperClasses;

import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

// Test cases do not rely on Context class to get excel sheet from Assets folder.
// Instead, they use a local dummy version in the root of the project.
class QuestionDataTest {
    private static QuestionData questionData;

    @org.junit.jupiter.api.BeforeAll
    static void setUp() {
        final boolean isUnitTest = true;
        String defaultDummyFile = "3_questions.xlsx";
        questionData = new QuestionData(isUnitTest, defaultDummyFile);
    }

    // TODO: Replace dummy excel sheet with a mocked Apache POI that opens mock data.
    // Unit tests should only focus on logic of reading files and getting question text, not on
    // opening files and using Apache POI properly.
    @org.junit.jupiter.params.ParameterizedTest
    @CsvSource({
            "2_questions.xlsx, 2",
            "3_questions.xlsx, 3"
    })
    void numQuestionsIsCorrect(String filename, int correctNumQuestions) {
        boolean isUnitTest = true;
        QuestionData questionData = new QuestionData(isUnitTest, filename);

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
        assertEquals(actualQuestionText, questionData.getQuestionText(questionName));
    }
}