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
    void constructor_scoresStartWithZero() {
        Scores scores = new Scores(questionData);

        // Go through all questions
        for (int i = 0; i < questionData.questionsLength(); i++) {
            assertEquals(0,
                    scores.getQuestionScore(i),
                    "Score for question " + i + " should be 0"
            );
        }
    }

    @Test
    void constructor_noQuestionsAreVisited() {
        Scores scores = new Scores(questionData);

        for (int i = 0; i < questionData.questionsLength(); i++)
            assertFalse(scores.questionIsVisited(i));
    }

    @org.junit.jupiter.params.ParameterizedTest
    @CsvSource({"0, 2", "1, 3", "2, 0"})
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
        final int[] scoreVals = {0, 1, 2}; // Note: Assumes questionLength() == 3

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
        for (int i = scoreVals.length; i < questionData.questionsLength(); i++) {
            assertEquals(0,
                    scores.getQuestionScore(i),
                    "All untouched scores should be 0");
        }
    }

    @org.junit.jupiter.params.ParameterizedTest
    @ValueSource(ints = {0, 1, 2})
    void questionIsVisited_singleQuestionIsVisited(int questionIndex) {
        Scores scores = new Scores(questionData);
        final int ARBITRARY_SCORE = 0;

        scores.putScore(questionIndex, ARBITRARY_SCORE);

        assertTrue(scores.questionIsVisited(questionIndex));
    }

    @Test
    void questionIsVisited_multipledQuestionsAreVisited() {
        Scores scores = new Scores(questionData);
        final int NUM_VISITED = questionData.questionsLength() - 1;

        for (int i = 0; i < NUM_VISITED; i++)
            scores.putScore(i, 0);

        for (int i = 0; i < NUM_VISITED; i++)
            assertTrue(scores.questionIsVisited(i));
        for (int i = NUM_VISITED; i < questionData.questionsLength(); i++)
            assertFalse(scores.questionIsVisited(i));
    }

    @Test
    void getScoreString_noQuestionsAnswered() {
        // Using 3_questions.csv. Expecting string with 3 answers
        Scores scores = new Scores(questionData);
        String expectedString = "000_" + questionData.getVersionOfQuestionOrder();

        assertEquals(expectedString, scores.getScoreString());
    }

    @Test
    void getScoreString_someQuestionsAnswered() {
        Scores scores = new Scores(questionData);
        String expectedString = "013_" + questionData.getVersionOfQuestionOrder();

        scores.putScore(0, 0);
        scores.putScore(1, 1);
        scores.putScore(2, 3);

        assertEquals(expectedString, scores.getScoreString());
    }

    @Test
    void getVisitedString_noQuestionsAnswered() {
        Scores scores = new Scores(questionData);
        String legacyExpectedString = "000_"
                + questionData.getVersionOfQuestionOrder();

        assertEquals(legacyExpectedString, scores.getVisitedString());
    }

    @Test
    void getVisitedString_someQuestionsAnswered() {
        Scores scores = new Scores(questionData);
        String legacyExpectedString = "111_"
                + questionData.getVersionOfQuestionOrder();
        final int ARBITRARY_SCORE = 2;

        scores.putScore(0, ARBITRARY_SCORE);
        scores.putScore(1, ARBITRARY_SCORE);
        scores.putScore(2, ARBITRARY_SCORE);

        assertEquals(legacyExpectedString, scores.getVisitedString());
    }

    @Test
    void scoreDataMatchesVersion_bothStringsMatch() {
        Scores scores = new Scores(questionData);
        String correctVersionStr = String.valueOf(questionData.getVersionOfQuestionOrder());
        String scoreStr = "000000000000000000000000321_" + correctVersionStr;
        String visitedStr = "111111111111111111111111111_" + correctVersionStr;

        assertTrue(scores.scoreDataMatchesVersion(scoreStr, visitedStr));
    }

    @Test
    void scoreDataMatchesVersion_incorrectScoreStringVersion() {
        Scores scores = new Scores(questionData);
        String correctVersionStr = String.valueOf(questionData.getVersionOfQuestionOrder());
        String wrongVersionStr = correctVersionStr + "2";
        String scoreStr = "000000000000000000000000321_" + wrongVersionStr;
        String visitedStr = "111111111111111111111111111_" + correctVersionStr;

        assertFalse(scores.scoreDataMatchesVersion(scoreStr, visitedStr));
    }

    @Test
    void scoreDataMatchesVersion_incorrectVisitedStringVersion() {
        Scores scores = new Scores(questionData);
        String correctVersionStr = String.valueOf(questionData.getVersionOfQuestionOrder());
        String wrongtVersionStr = correctVersionStr + "2";
        String scoreStr = "000000000000000000000000321_" + correctVersionStr;
        String visitedStr = "111111111111111111111111111_" + wrongtVersionStr;

        assertFalse(scores.scoreDataMatchesVersion(scoreStr, visitedStr));
    }

    @Test
    void scoreDataMatchesVersion_bothStringsIncorrect() {
        Scores scores = new Scores(questionData);
        String wrongVersionStr = String.valueOf(questionData.getVersionOfQuestionOrder()) + "2";
        String scoreStr = "000000000000000000000000321_" + wrongVersionStr;
        String visitedStr = "111111111111111111111111111_" + wrongVersionStr;

        assertFalse(scores.scoreDataMatchesVersion(scoreStr, visitedStr));
    }

    @Test
    void restoreScores_scoreAndVisitedStringRestoredCorrectly_noneVisited() {
        Scores scores = new Scores(questionData);
        String zeroScores = "000_" + questionData.getVersionOfQuestionOrder();
        String noneVisited = "000_" + questionData.getVersionOfQuestionOrder();

        scores.restoreScores(zeroScores, noneVisited);

        for (int i = 0; i < questionData.questionsLength(); i++) {
            assertEquals(zeroScores.charAt(i) - 0x30, scores.getQuestionScore(i));
            assertEquals((noneVisited.charAt(i) - 0x30) == 1, scores.questionIsVisited(i));
        }
    }

    // FIXME: Make a copy of the above unit test for other score and visited strings.
    // Better yet, just get rid of the "noneVisited" clause and make this a 
    // parameterized unit test
}
