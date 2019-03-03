package morningsignout.phq9transcendi.HelperClasses;

import android.util.Log;
import android.util.Pair;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.IndexOutOfBoundsException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import morningsignout.phq9transcendi.PHQApplication;

/**
 * Created by pokeforce on 5/26/16.
 * Container of score values and if question is "visited"
 */
public class Scores {
    // constant category name for red flag questions
    private static final String RED_FLAG = "red-flag";

    // Mapping of question name to score value or visit flag
    private int[] scoreDictionary;
    private boolean[] questionIsAnswered;
    private QuestionData questionData;

    public Scores(QuestionData questionData) {
        this.scoreDictionary = new int[questionData.questionsLength()];
        this.questionIsAnswered = new boolean[questionData.questionsLength()];
        this.questionData = questionData;

        setZeroScores();
    }

    // For restoring Scores state
    public Scores(QuestionData questionData, String savedScore, String savedVisit) {
        this(questionData);
        if (scoreDataMatchesVersion(savedScore, savedVisit)) restoreScores(savedScore, savedVisit);
    }

    private void setZeroScores() {
        for (int i = 0; i < questionData.questionsLength(); i++) {
            scoreDictionary[i] = 0;
            questionIsAnswered[i] = false;
        }
    }

    void restoreScores(String savedScore, String savedVisit) {
        for (int i = 0; i < questionData.questionsLength(); i++) {
            scoreDictionary[i] = ((int) savedScore.charAt(i)) - 0x30;
            questionIsAnswered[i] = (savedVisit.charAt(i) != '0');
        }
    }

    boolean scoreDataMatchesVersion(String savedScore, String savedVisit) {
        return savedScore != null && savedVisit != null &&
                savedScore.endsWith(String.valueOf(questionData.getVersionOfQuestionOrder())) &&
                savedVisit.endsWith(String.valueOf(questionData.getVersionOfQuestionOrder()));
    }

    public void putScore(int index, int value) {
        scoreDictionary[index] = value;
        questionIsAnswered[index] = true;
    }

    public int getQuestionScore(int i) {
        if (i < 0 || i > questionData.questionsLength())
            throw new IndexOutOfBoundsException();

        return scoreDictionary[i];
    }

    public int getFinalScore() {
        HashMap<String, Integer> categoryScores = getCategoryScoreMap();

        // Sum up final results
        int sum = 0;
        for (Integer score : categoryScores.values())
            sum += score;

        return sum;
    }

    public boolean containsRedFlag() {
        for (int i = 0; i < questionData.questionsLength(); i++) {
            if (questionData.getQuestionCategoryType(i).equals(RED_FLAG) && scoreDictionary[i] != 0)
                return true;
        }

        return false;
    }

    public boolean questionIsVisited(int i) {
        return questionIsAnswered[i];
    }

    public boolean allQuestionsVisited() {
        for (boolean isVisited : questionIsAnswered) {
            if (!isVisited) {
                return false;
            }
        }

        return true;
    }

    public HashMap<String, Integer> getCategoryScoreMap() {
        HashMap<String, Integer> categoryScores = new HashMap<>();
        Set<String> finalScoreCategories = questionData.getFinalScoreCategories();

        // Separate scores by categories
        for (int i = 0; i < questionData.questionsLength(); i++) {
            String currCategory = questionData.getQuestionCategoryType(i);

            // Don't consider categories that don't contribute to final score
            if (!finalScoreCategories.contains(currCategory))
                continue;

            Integer categoryScore = categoryScores.get(currCategory);

            // First question in a category
            if (categoryScore == null)
                categoryScores.put(currCategory, scoreDictionary[i]);

                // Second question in a category onward. Keep highest value.
            else if (scoreDictionary[i] > categoryScore)
                categoryScores.put(currCategory, scoreDictionary[i]);
        }

        return categoryScores;
    }

    public ArrayList<Integer> getScoreValsArray() {
        ArrayList<Integer> answers = new ArrayList<>();
        for (int score : scoreDictionary) answers.add(score);

        return answers;
    }

    public String getScoreString() {
        StringBuilder scoreBuilder = new StringBuilder();
        for (int i = 0; i < questionData.questionsLength(); i++)
            scoreBuilder.append(scoreDictionary[i]);

        scoreBuilder.append("_").append(questionData.getVersionOfQuestionOrder());

        return scoreBuilder.toString();
    }

    public String getVisitedString() {
        StringBuilder visited = new StringBuilder();
        for (int i = 0; i < questionData.questionsLength(); i++)
            visited.append(questionIsAnswered[i] ? 1 : 0);

        visited.append("_").append(questionData.getVersionOfQuestionOrder());

        return visited.toString();
    }

    // Functions below are for emailing results in the results activity
    public BitSet getRedFlagBits() {
        Queue<Integer> redFlagAnswers = new LinkedList<>();
        for (int i = 0; i < questionData.questionsLength(); i++) {
            if (questionData.getQuestionCategoryType(i).equals(RED_FLAG)) {
                redFlagAnswers.add(scoreDictionary[i]);
            }
        }

        BitSet redFlagBits = new BitSet(redFlagAnswers.size());
        int i = 0;
        while (redFlagAnswers.peek() != null) {
            redFlagBits.set(i, redFlagAnswers.remove() != 0); // int to bool
            i++;
        }

        return redFlagBits;
    }

    public BitSet getFamilyOrCultureBits() {
        BitSet famOrCultureBits = new BitSet(2);
        famOrCultureBits.set(0, scoreDictionary[questionData.getIndex_familySituation()] != 0); // convert int to boolean
        famOrCultureBits.set(1, scoreDictionary[questionData.getIndex_culturalBackground()] != 0);
        return famOrCultureBits;
    }

    public int getFamilyUnderstandsAnswer() {
        return scoreDictionary[questionData.getIndex_familyUnderstands()];
    }

    public int getiAppointmentAnswer() {
        return scoreDictionary[questionData.getIndex_iAppointment()];
    }
}