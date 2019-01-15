package morningsignout.phq9transcendi.HelperClasses;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ScoresTest {
    private final static String QUESTION_ASSET_FILE = "3_questions.csv";
    private final static String ANSWER_ASSET_FILE = "answers.csv";
    private final static String CONFIG_ASSET_FILE = "config.csv";
    private static final boolean IS_UNIT_TEST = true;

    private static QuestionData questionData;

    @BeforeAll
    static void setUp() {
        // Make sure test files exist
        File questionFile = new File(QUESTION_ASSET_FILE);
        File answerFile = new File(ANSWER_ASSET_FILE);
        File configFile = new File(CONFIG_ASSET_FILE);

        assertTrue(questionFile.exists());
        assertTrue(answerFile.exists());
        assertTrue(configFile.exists());

        questionData = null;
        try {
            questionData = new QuestionData(IS_UNIT_TEST, QUESTION_ASSET_FILE);
        } catch (IOException e) {
            fail("QuestionData should not throw exception: " + e.getMessage());
        }
    }

    @Test
    void defaultConstructor_scoresStartWithZero() {
        Scores scores = new Scores(questionData);

        // Go through all questions
        for (int i = 0; i < QuestionData.NUM_QUESTIONS; i++) {
            assertEquals(0,
                    scores.getQuestionScore(i),
                    "Score for question " + i + " should be 0"
            );
        }
    }

    @Test
    void defaultConstructor_noQuestionsAreVisited() {
        Scores scores = new Scores(questionData);

        for (int i = 0; i < QuestionData.NUM_QUESTIONS; i++)
            assertFalse(scores.questionIsVisited(i));
    }

    @org.junit.jupiter.params.ParameterizedTest
    @CsvSource({"0, 2", "10, 3", "4, 0"})
    void putScore_singleScoreInsertedCorrectly(int questionIndex, int scoreVal) {
        Scores scores = new Scores(questionData);

        scores.putScore(questionIndex, scoreVal);

        assertEquals(scoreVal,
                scores.getQuestionScore(questionIndex),
                "Score for question " + questionIndex + " does not match inserted value");
    }

    @Test
    void putScore_multipleScoresInsertedCorrectly() {
        Scores scores = new Scores(questionData);
        int[] scoreVals = {0, 1, 2, 3, 0, 1};

        // Insert scores into Scores as scores of the first questions
        for (int i = 0; i < scoreVals.length; i++) {
            scores.putScore(i, scoreVals[i]);
        }
        // Scores inserted should match scoreVals
        for (int i = 0; i < scoreVals.length; i++) {
            assertEquals(scoreVals[i],
                    scores.getQuestionScore(i),
                    "Inserted values do not match actual scores");
        }
        // If score is not inserted for question, its score is 0
        for (int i = scoreVals.length; i < QuestionData.NUM_QUESTIONS; i++) {
            assertEquals(0,
                    scores.getQuestionScore(i),
                    "All untouched scores should be 0");
        }
    }

    @org.junit.jupiter.params.ParameterizedTest
    @ValueSource(ints = {1, 3, 5})
    void questionIsVisited_singleQuestionIsVisited(int questionIndex) {
        Scores scores = new Scores(questionData);
        final int ARBITRARY_SCORE = 0;

        scores.putScore(questionIndex, ARBITRARY_SCORE);

        assertTrue(scores.questionIsVisited(questionIndex));
    }

    @Test
    void questionIsVisited_multipledQuestionsAreVisited() {
        Scores scores = new Scores(questionData);
        final int NUM_VISITED = 5;

        for (int i = 0; i < NUM_VISITED; i++)
            scores.putScore(i, 0);

        for (int i = 0; i < NUM_VISITED; i++)
            assertTrue(scores.questionIsVisited(i));
        for (int i = NUM_VISITED; i < QuestionData.NUM_QUESTIONS; i++)
            assertFalse(scores.questionIsVisited(i));
    }

    @Test
    void containsRedFlag_noAnswersInputted_noRedFlag() {
        Scores scores = new Scores(questionData);

        assertFalse(scores.containsRedFlag());
    }

    @Test
    void containsRedFlag_someAnswersInputted_noRedFlag() {
        Scores scores = new Scores(questionData);

        // Assuming red flag questions are never the first 3 questions
        scores.putScore(0, 0);
        scores.putScore(1, 1);
        scores.putScore(2, 2);

        assertFalse(scores.containsRedFlag());
    }

    @Test
    void containsRedFlag_redFlagAnswerInputted_redFlag() {
        Scores scores = new Scores(questionData);

        scores.putScore(QuestionData.RED_FLAG_QUESTION, 1);

        assertTrue(scores.containsRedFlag());
    }

    @Test
    void containsRedFlag_redFlagAnswerChanged_noRedFlag() {
        Scores scores = new Scores(questionData);

        scores.putScore(QuestionData.RED_FLAG_QUESTION, 1);
        scores.putScore(QuestionData.RED_FLAG_QUESTION, 0);

        assertFalse(scores.containsRedFlag());
    }

    @Test
    void getScoreString_noQuestionsAnswered() {
        Scores scores = new Scores(questionData);
        final String legacyExpectedString = "000000000000000000000000000_"
                + questionData.getVersionOfQuestionOrder();

        assertEquals(legacyExpectedString, scores.getScoreString());
    }
}