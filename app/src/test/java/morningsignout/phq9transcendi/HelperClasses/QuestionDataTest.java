package morningsignout.phq9transcendi.HelperClasses;

import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

// Test cases do not rely on Context class to get excel sheet from Assets folder. Instead,
// they use a local version in the root of the project.
class QuestionDataTest {
    // Names of actual questions in the excel sheet. Not likely these will change.
    @org.junit.jupiter.params.ParameterizedTest
    @CsvSource("anhedoniainterest, guilt, suicidality_flag")
    void loadQuestionNames_containsCorrectStrings(String questionName) {
        QuestionData questionData = new QuestionData();
        assertNotNull(questionData.getQuestionText(questionName),
                "Value for question (" + questionName + ") should exist");
    }
}