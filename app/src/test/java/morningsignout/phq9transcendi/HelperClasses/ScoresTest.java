package morningsignout.phq9transcendi.HelperClasses;

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

    @org.junit.jupiter.api.Test
    void putScore() {
    }

    @org.junit.jupiter.api.Test
    void getQuestionScore() {
    }

    @org.junit.jupiter.api.Test
    void getFinalScore() {
    }

    @org.junit.jupiter.api.Test
    void questionIsVisited() {
    }
}