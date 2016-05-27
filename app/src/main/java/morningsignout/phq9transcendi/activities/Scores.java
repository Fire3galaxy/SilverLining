package morningsignout.phq9transcendi.activities;

import android.util.Log;

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
    // Think of this as a map from the question # to question name
    static public final String[] questions = {
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
    static private int[] categories = {
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

    private static int RED_FLAG_QUESTION = 16;

    private TreeMap<String, Integer> scoreDictionary;

    public Scores() {
        scoreDictionary = new TreeMap<>();

        for (String category : questions)
            scoreDictionary.put(category, 0);
    }

    public void putScore(String category, int value) {
        scoreDictionary.put(category, value);

        // TESTING
//        for (String s : questions)
//            Log.d("Scores", s + ": " + String.valueOf(scoreDictionary.get(s)));
    }

    // Access by index (for (int i = 0; i < questions.length; i++) {})
    public int getScore(int i) {
        if (i >= 0 && i <= questions.length)
            return scoreDictionary.get(questions[i]);

        return -1;
    }

    // Access by for loop with questions array (for (String s : questions) {})
    public int getScore(String category) {
        return scoreDictionary.get(category);
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
        for (int i = RED_FLAG_QUESTION; i < questions.length; i++)
            if (scoreDictionary.get(questions[i]) == 1)
                return true;

        return false;
    }
}
