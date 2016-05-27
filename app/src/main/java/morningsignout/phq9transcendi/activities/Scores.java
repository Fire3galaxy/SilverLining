package morningsignout.phq9transcendi.activities;

import java.util.TreeMap;

/**
 * Created by pokeforce on 5/26/16.
 */
public class Scores {
    // Using names instead of categories for FireBase and the off-chance that
    // the order of questions is not fixed (would be bad for database to use #).
    // Ordered by current order of questions.
    // Think of this as a map from the question # (index #) to category name
    static public final String[] questions = {
            "anhedoniainterest",
            "anhedoniaenjoy",
            "mooddepress",
            "moodhopeless",
            "sleeplow",
            "sleephigh",
            "fatigue",
            "appetitelow",
            "appetitehigh",
            "guilt",
            "concentrationpoor",
            "concentrationdistracted",
            "psychomotorslowself",
            "psychomotorslowother",
            "suicidalityactive",
            "suicidalitypassive",

            "continousdepression",  // Red flag
            "longdepression",
            "interference",
            ""
    };

    private TreeMap<String, Integer> scoreDictionary;

    public Scores() {
        scoreDictionary = new TreeMap<>();


    }
}
