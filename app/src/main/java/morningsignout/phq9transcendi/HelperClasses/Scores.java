package morningsignout.phq9transcendi.HelperClasses;

import android.util.Log;
import android.util.Pair;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import morningsignout.phq9transcendi.PHQApplication;

/**
 * Created by pokeforce on 5/26/16.
 * Container of score values and if question is "visited"
 */
public class Scores {
    // Mapping of question name to score value or visit flag
    private int[] scoreDictionary;
    private TreeMap<String, Boolean> questionIsAnswered;
    private QuestionData questionData;

    public Scores(QuestionData questionData) {
        this.scoreDictionary = new int[questionData.questionsLength()];
        this.questionIsAnswered = new TreeMap<>();
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
            questionIsAnswered.put(questionData.getQuestionName(i), false);
        }
    }

    private void restoreScores(String savedScore, String savedVisit) {
        for (int i = 0; i < QuestionData.questionNames.length; i++) {
            scoreDictionary[i] = ((int) savedScore.charAt(i)) - 0x30;
            questionIsAnswered.put(QuestionData.questionNames[i], savedVisit.charAt(i) != '0');   // char to bool (0 or 1)
        }
    }

    boolean scoreDataMatchesVersion(String savedScore, String savedVisit) {
        return savedScore != null && savedVisit != null &&
                savedScore.endsWith(String.valueOf(questionData.getVersionOfQuestionOrder())) &&
                savedVisit.endsWith(String.valueOf(questionData.getVersionOfQuestionOrder()));
    }

    public void putScore(int index, int value) {
        scoreDictionary[index] = value;
        questionIsAnswered.put(QuestionData.questionNames[index], true);
    }

    public int getQuestionScore(int i) {
        if (i >= 0 && i <= QuestionData.questionNames.length)
            return scoreDictionary[i];

        return -1;
    }

    public int getFinalScore() {
        int sum = 0;
        int max = 0;
        int currentCategory = QuestionData.categoryIndices[0];

        for (int i = 0; i < QuestionData.questionNames.length; i++) {
            max = Math.max(scoreDictionary[i], max);

            // Next category
            if (i + 1 < QuestionData.categoryIndices.length && currentCategory != QuestionData.categoryIndices[i + 1]) {
                if (currentCategory < QuestionData.RED_FLAG_CATEGORY)
                    sum += max;
                max = 0;
                currentCategory = QuestionData.categoryIndices[i + 1];
            }
        }

        return sum;
    }

    public boolean containsRedFlag() {
        int redFlagNum = 0;
        int end = QuestionData.RED_FLAG_QUESTION + QuestionData.redFlagThreshold.length;

        // Assumes that red flag questions are consecutive
        for (int i = QuestionData.RED_FLAG_QUESTION; i < end; i++, redFlagNum++)
            if (scoreDictionary[i] >= QuestionData.redFlagThreshold[redFlagNum])
                return true;

        return false;
    }

    public boolean questionIsVisited(int i) {
        if (questionIsAnswered.containsKey(QuestionData.questionNames[i]))
            return questionIsAnswered.get(QuestionData.questionNames[i]);

        throw new IndexOutOfBoundsException(); // Should not happen
    }

    public boolean allQuestionsVisited() {
        for (Boolean isVisited : questionIsAnswered.values()) {
            Log.d("Scores", Boolean.toString(isVisited));
            if (!isVisited) {
                return false;
            }
        }

        return true;
    }

    private int getCategoryScore(int category) {
        int max = 0;

        for (int i = 0; i < QuestionData.categoryIndices.length && QuestionData.categoryIndices[i] <= category; i++)
            if (QuestionData.categoryIndices[i] == category)
                max = Math.max(scoreDictionary[i], max);

        return max;
    }

    public HashMap<String, Integer> getCategoryScoreMap() {
        HashMap<String, Integer> scores = new HashMap<>(QuestionData.categoryNames.length);
        for (int i = 0; i < QuestionData.categoryNames.length; i++)
            scores.put(QuestionData.categoryNames[i], getCategoryScore(i));
        return scores;
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
        for (String q : QuestionData.questionNames)
            visited.append(questionIsAnswered.get(q).compareTo(false));   // Returns 1 or 0 (true/false)

        visited.append("_").append(questionData.getVersionOfQuestionOrder());

        return visited.toString();
    }

    // Functions below are for emailing results in the results activity
    public BitSet getRedFlagBits() {
        BitSet redFlagBitSet = new BitSet(5);

        // Assumes that red flag questions are consecutive
        int redFlagNum = 0;
        int end = QuestionData.RED_FLAG_QUESTION + QuestionData.redFlagThreshold.length;
        for (int i = QuestionData.RED_FLAG_QUESTION; i < end; i++, redFlagNum++)
            redFlagBitSet.set(redFlagNum,
                    (scoreDictionary[i] >= QuestionData.redFlagThreshold[redFlagNum]));

        return redFlagBitSet;
    }

    public BitSet getFamOrCultureBits() {
        BitSet famOrCultureBits = new BitSet(2);
        famOrCultureBits.set(0, scoreDictionary[QuestionData.FAMILY_SITUATION] != 0); // convert int to boolean
        famOrCultureBits.set(1, scoreDictionary[QuestionData.CULTURAL_BACKGROUND] != 0);
        return famOrCultureBits;
    }

    public int getFamilyUnderstandsAnswer() {
        return scoreDictionary[QuestionData.FAMILY_UNDERSTANDS];
    }

    public int getiAppointmentAnswer() {
        return scoreDictionary[QuestionData.I_APPOINTMENT];
    }
}