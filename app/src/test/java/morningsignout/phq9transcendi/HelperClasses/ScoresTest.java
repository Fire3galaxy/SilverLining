package morningsignout.phq9transcendi.HelperClasses;

import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ScoresTest {
    @org.junit.jupiter.api.Test
    void defaultConstructor_scoresStartWithZero() {
        Scores scores = new Scores();

        // Go through all questions
        for (int i = 0; i < QuestionData.NUM_QUESTIONS; i++) {
            assertEquals(0,
                    scores.getQuestionScore(i),
                    "Score for question " + i + " should be 0"
            );
        }
    }

    @org.junit.jupiter.api.Test
    void defaultConstructor_noQuestionsAreVisited() {
        Scores scores = new Scores();

        for (int i = 0; i < QuestionData.NUM_QUESTIONS; i++)
            assertFalse(scores.questionIsVisited(i));
    }

    @org.junit.jupiter.params.ParameterizedTest
    @CsvSource({"0, 2", "10, 3", "4, 0"})
    void putScore_singleScoreInsertedCorrectly(int questionIndex, int scoreVal) {
        Scores scores = new Scores();

        scores.putScore(questionIndex, scoreVal);

        assertEquals(scoreVal,
                scores.getQuestionScore(questionIndex),
                "Score for question " + questionIndex + " does not match inserted value");
    }

    @org.junit.jupiter.api.Test
    void putScore_multipleScoresInsertedCorrectly() {
        Scores scores = new Scores();
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
        Scores scores = new Scores();
        final int ARBITRARY_SCORE = 0;

        scores.putScore(questionIndex, ARBITRARY_SCORE);

        assertTrue(scores.questionIsVisited(questionIndex));
    }

    @org.junit.jupiter.api.Test
    void questionIsVisited_multipledQuestionsAreVisited() {
        Scores scores = new Scores();
        final int NUM_VISITED = 5;

        for (int i = 0; i < NUM_VISITED; i++)
            scores.putScore(i, 0);

        for (int i = 0; i < NUM_VISITED; i++)
            assertTrue(scores.questionIsVisited(i));
        for (int i = NUM_VISITED; i < QuestionData.NUM_QUESTIONS; i++)
            assertFalse(scores.questionIsVisited(i));
    }

    @org.junit.jupiter.api.Test
    void containsRedFlag_noAnswersInputted_noRedFlag() {
        Scores scores = new Scores();

        assertFalse(scores.containsRedFlag());
    }

    @org.junit.jupiter.api.Test
    void containsRedFlag_someAnswersInputted_noRedFlag() {
        Scores scores = new Scores();

        // Assuming red flag questions are never the first 3 questions
        scores.putScore(0, 0);
        scores.putScore(1, 1);
        scores.putScore(2, 2);

        assertFalse(scores.containsRedFlag());
    }

    @org.junit.jupiter.api.Test
    void containsRedFlag_redFlagAnswerInputted_redFlag() {
        Scores scores = new Scores();

        scores.putScore(QuestionData.RED_FLAG_QUESTION, 1);

        assertTrue(scores.containsRedFlag());
    }

    @org.junit.jupiter.api.Test
    void containsRedFlag_redFlagAnswerChanged_noRedFlag() {
        Scores scores = new Scores();

        scores.putScore(QuestionData.RED_FLAG_QUESTION, 1);
        scores.putScore(QuestionData.RED_FLAG_QUESTION, 0);

        assertFalse(scores.containsRedFlag());
    }
}