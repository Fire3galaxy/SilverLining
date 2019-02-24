package morningsignout.phq9transcendi.HelperClasses;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.util.BitSet;

import static org.junit.jupiter.api.Assertions.*;

class ScoresTest {
    private final static String QUESTION_ASSET_FILE = "3_questions.csv";
    private final static String SPECIAL_QUESTION_ASSET_FILE = "special_questions.csv";
    private final static String ANSWER_ASSET_FILE = "answers.csv";
    private final static String CONFIG_ASSET_FILE = "config.csv";

    private static QuestionData questionData;
    private static QuestionData specialQuestionData;

    private boolean intToBool(int i) {
        return i != 0;
    }

    @BeforeAll
    static void setUp() {
        // Make sure test files exist
        File questionFile = new File(QUESTION_ASSET_FILE);
        File specialQuestionFile = new File(SPECIAL_QUESTION_ASSET_FILE);
        File answerFile = new File(ANSWER_ASSET_FILE);
        File configFile = new File(CONFIG_ASSET_FILE);

        assertTrue(questionFile.exists());
        assertTrue(specialQuestionFile.exists());
        assertTrue(answerFile.exists());
        assertTrue(configFile.exists());

        questionData = null;
        try {
            questionData = new QuestionData(QUESTION_ASSET_FILE);
        } catch (IOException e) {
            fail("QuestionData should not throw exception: " + e.getMessage());
        }

        specialQuestionData = null;
        try {
            specialQuestionData = new QuestionData(SPECIAL_QUESTION_ASSET_FILE);
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

        // Insert scores into Scores as scores of the scoreValues questions
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
    void getFinalScore_noQuestionsAnswered() {
        Scores scores = new Scores(questionData);
        int expectedScore = 0;

        assertEquals(expectedScore, scores.getFinalScore());
    }

    @Test
    void getFinalScore_contributingCategoriesAnswered() {
        Scores scores = new Scores(questionData);
        int expectedScore = 2;

        scores.putScore(1, 1);
        scores.putScore(2, 1);

        assertEquals(expectedScore, scores.getFinalScore());
    }

    @Test
    void getFinalScore_nonContributingCategoriesAnswered() {
        Scores scores = new Scores(questionData);
        int expectedScore = 0;

        scores.putScore(0, 1);

        assertEquals(expectedScore, scores.getFinalScore());
    }

    @Test
    void getFinalScore_multipleQuestionsInOneCategoryAnswered() {
        try {
            QuestionData manyQuestionData = new QuestionData("many_questions.csv");
            Scores scores = new Scores(manyQuestionData);
            int expectedScore = 3;

            // category: anhedonia
            scores.putScore(0, 3);
            scores.putScore(2, 1);
            scores.putScore(3, 2);

            // Expecting to get higher value of two questions, not sum of scores
            assertEquals(expectedScore, scores.getFinalScore());
        } catch (IOException e) {
            fail("QuestionData should not throw exception: " + e.getMessage());
        }
    }

    @Test
    void containsRedFlag_noRedFlagsRaised_noAnswers() {
        Scores scores = new Scores(questionData);

        assertFalse(scores.containsRedFlag());
    }

    @Test
    void containsRedFlag_noRedFlagsRaised_someAnswers() {
        Scores scores = new Scores(questionData);

        scores.putScore(0, 1);
        scores.putScore(1, 1);
        scores.putScore(1, 1);

        assertFalse(scores.containsRedFlag());
    }

    @Test
    void containsRedFlag_redFlagsRaised() {
        try {
            QuestionData manyQuestionData = new QuestionData("many_questions.csv");
            Scores scores = new Scores(manyQuestionData);

            scores.putScore(5, 1);

            assertTrue(scores.containsRedFlag());
        } catch (IOException e) {
            fail("QuestionData should not throw exception: " + e.getMessage());
        }
    }

    @Test
    void getScoreString_noQuestionsAnswered() {
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

    @ParameterizedTest
    @ValueSource(strings = {
            "000,000",
            "000,111",
            "220,110",
            "312,111"
    })
    void restoreScores_scoreAndVisitedStringRestoredCorrectly_noneVisited(
            @ConvertWith(ToScoreStateContainer.class) ScoreStateContainer expectedScoreObjects)
    {
        Scores scores = new Scores(questionData);

        scores.restoreScores(expectedScoreObjects.scoreString, expectedScoreObjects.visitedString);

        for (int i = 0; i < questionData.questionsLength(); i++) {
            assertEquals(expectedScoreObjects.scoreValues[i], scores.getQuestionScore(i));
            assertEquals(expectedScoreObjects.visitedValues[i], scores.questionIsVisited(i));
        }
    }

    @Test
    void getiAppointmentAnswer_expectedScoreIsCorrect() {
        Scores scores = new Scores(specialQuestionData);
        int iAppointmentIndex = specialQuestionData.getIndex_iAppointment();
        int expectedScore = 3;

        scores.putScore(iAppointmentIndex, expectedScore);

        assertEquals(expectedScore, scores.getiAppointmentAnswer());
    }

    @Test
    void getFamilyOrCultureBits_expectedScoreIsCorrect() {
        Scores scores = new Scores(specialQuestionData);
        int familySituationIndex = specialQuestionData.getIndex_familySituation();
        int culturalBackgroundIndex = specialQuestionData.getIndex_culturalBackground();
        int expectedFSScore = 0;
        int expectedCBScore = 1;

        scores.putScore(familySituationIndex, expectedFSScore);
        scores.putScore(culturalBackgroundIndex, expectedCBScore);
        BitSet familyOrCultureBits = scores.getFamilyOrCultureBits();

        int familySituationBitIndex = 0;
        int culturalBackgroundBitIndex = 1;
        assertEquals(intToBool(expectedFSScore), familyOrCultureBits.get(familySituationBitIndex));
        assertEquals(intToBool(expectedCBScore), familyOrCultureBits.get(culturalBackgroundBitIndex));
    }

    @Test
    void getFamilyUnderstandsAnswer_expectedScoreIsCorrect() {
        Scores scores = new Scores(specialQuestionData);
        int familyUnderstandsIndex = specialQuestionData.getIndex_familyUnderstands();
        int expectedScore = 3;

        scores.putScore(familyUnderstandsIndex, expectedScore);

        assertEquals(expectedScore, scores.getFamilyUnderstandsAnswer());
    }
}
