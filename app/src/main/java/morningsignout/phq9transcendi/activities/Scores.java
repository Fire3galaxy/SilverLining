package morningsignout.phq9transcendi.activities;

import android.util.Log;

import com.firebase.client.Firebase;

import java.util.TreeMap;

/**
 * Created by pokeforce on 5/26/16.
 */
public class Scores {
    // Using names instead of numbers for FireBase on the off-chance that
    // the order of questions is not fixed (would be bad for database to use #).
    //
    // Ordered by current order of questions, arranged by category
    //
    // Think of this array as a map from the question # to question name
    static private final String[] questions = {
            "anhedoniainterest", "anhedoniaenjoy",
            "mooddepress", "moodhopeless",
            "sleeplow", "sleephigh",
            "fatigue",
            "appetitelow", "appetitehigh",
            "guilt",
            "concentrationpoor", "concentrationdistracted",
            "psychomotorslowself", "psychomotorslowother",
            "suicidalityactive", "suicidalitypassive",

            // red flag
            "continuousdepression_flag", "longdepression_flag", "interference", "suicidality_flag",
            "suicideaction_flag"
    };

    // Categories of questions
    static private final String[] categoryNames = {
            "anhedonia",
            "mood",
            "sleep",
            "fatigue",
            "appetite",
            "guilt",
            "concentration",
            "psychomotor",
            "suicidality",
            "redflag"
    };

    // Array of which category each question is associated with
    static private final int[] categories = {
        0, 0,
        1, 1,
        2, 2,
        3,
        4, 4,
        5,
        6, 6,
        7, 7,
        8, 8,
        9, 9, 9, 9, 9   // red flag
    };

    // Keys used in uploading to Firebase (Note: false = a, true = b).
    static private final String[] answers = {
            "answer-a",
            "answer-b",
            "answer-c",
            "answer-d"
    };

    static private int RED_FLAG_QUESTION = 16;  // Index where red flag starts

    private TreeMap<String, Integer> scoreDictionary;
    private TreeMap<String, Boolean> questionIsVisited;

    public Scores() {
        scoreDictionary = new TreeMap<>();
        questionIsVisited = new TreeMap<>();

        for (String category : questions) {
            scoreDictionary.put(category, 0);
            questionIsVisited.put(category, false);
        }
    }

    public void putScore(int i, int value) {
        scoreDictionary.put(questions[i], value);
        questionIsVisited.put(questions[i], true);
    }

    // Note: use to access by index (for (int i = 0; i < questions.length; i++) {})
    public int getScore(int i) {
        if (i >= 0 && i <= questions.length)
            return scoreDictionary.get(questions[i]);

        return -1;
    }

    public int getTotalScore() {
        int sum = 0;
        int max = 0;
        int currentCategory = categories[0];

        for (int i = 0; i < RED_FLAG_QUESTION; i++) {
            max = Math.max(scoreDictionary.get(questions[i]), max);

            // Next category
            if (i + 1 < categories.length && currentCategory != categories[i + 1]) {
                Log.d("Scores", String.valueOf(currentCategory) + ": " + String.valueOf(max) + ", " + String.valueOf(sum + max));

                sum += max;
                max = 0;
                currentCategory = categories[i + 1];
            }
        }

        Log.d("Scores", "---------------------------------------");

        return sum;
    }

    public boolean containsRedFlag() {
        for (int i = RED_FLAG_QUESTION; i < questions.length; i++) {
            Log.d("Scores", "9: " + scoreDictionary.get(questions[i]));

            if (scoreDictionary.get(questions[i]) == 1)
                return true;
        }

        Log.d("Scores", "---------------------------------------");

        return false;
    }

    public boolean questionIsVisited(int i) {
        if (questionIsVisited.containsKey(questions[i]))
            return questionIsVisited.get(questions[i]);

        throw new IndexOutOfBoundsException(); // Should not happen
    }

    public boolean allQuestionsVisited() {
        for (int i = 0; i < questions.length; i++)
            if (!questionIsVisited(i))
                return false;

        return true;
    }

    public boolean uploadDataToDatabase(Firebase ref, String userID, String startTime, String endTime) {
        Log.d("Scores", "In upload function");

        Firebase testRef = ref.child("tests").push(),
                userRef = ref.child("users").child(userID),
                questionsRef = ref.child("questions-analytics");
        String testID = testRef.getKey();

//        // Questions analytics
//        for (String q : questions) {
//            StringBuilder path = new StringBuilder();
//            int currAnswer = scoreDictionary.get(q);
//
//            // question/answer-#/userID/"testID"
//            path.append(q).append("/")
//                    .append(answers[currAnswer]).append("/")
//                    .append(userID).append("/")
//                    .append("testID");
//
//            // add "new val":testID to database
//            questionsRef.child(path.toString()).push().setValue(testID);
//        }

        // Users
        userRef.child("testIDs").push().setValue(testID);

        // Tests
        testRef.child("startTimestamp").setValue(startTime);
        testRef.child("endTimestamp").setValue(endTime);
        // FIXME: Convert getTotalScore into two functions, one that calls a private function that returns
        // FIXME: an int array with each category in each slot, and the other public one that just adds up the
        // FIXME: contents minus red flag score. That way I can use that function for this scores database function.
//        for ()
//        testRef.child("answers").setValue(scoreDictionary);
//        testRef.child()

        return true;    // FIXME: replace with check for success
    }
}
