package morningsignout.phq9transcendi.HelperClasses;

import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ScoresTest {

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
    }

    @org.junit.jupiter.api.Test
    void newScores_scoresStartWithZero() {
        Scores scores = new Scores();

        for (int i = 0; i < QuestionData.NUM_QUESTIONS; i++) {
            assertEquals(0,
                    scores.getQuestionScore(i),
                    "Score for question " + i + " should be 0"
            );
        }
    }

    @org.junit.jupiter.params.ParameterizedTest
    @CsvSource({"0, 2", "10, 3", "4, 0"})
    void singleScoreInsertedCorrectly(int questionIndex, int scoreVal) {
        Scores scores = new Scores();

        scores.putScore(questionIndex, scoreVal);

        assertEquals(scoreVal,
                scores.getQuestionScore(questionIndex),
                "Score for question " + questionIndex + " does not match inserted value");
    }
}