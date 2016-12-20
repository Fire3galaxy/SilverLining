package morningsignout.phq9transcendi.activities.HelperClasses;

/**
 * Created by Daniel on 12/18/2016.
 * Intended to make adding questions much easier. No dependency in QuizActivity for particular
 * #/organization of questions except on this class and Scores (category of new question).
 * Warning: Red flag question positions are FIXED in Scores class. If those positions change, then
 * you have to edit Scores class.
 */
public class QuestionData {
    static public final int NORMAL = 0, // Answer array types
            FLAG = 1,
            DEPRESSION = 2,
            APPOINTMENT = 3,
            STRANGER = 4,
            SUPPORTIVE = 5,
            YES_NO = 6;
    static public final int NUM_QUESTIONS = 22; // total number of questions
    static public final boolean[] USES_SLIDER = {
            true,  // 0
            true,  // 1
            true,  // 2
            true,  // 3
            true,  // 4
            true,  // 5
            true,  // 6
            true,  // 7
            true,  // 8
            true,  // 9
            true,  // 10
            true,  // 11
            true,  // 12
            true,  // 13
            true,  // 14
            true,  // 15
            false, // 16
            false, // 17
            true,  // 18
            false, // 19
            false, // 20
            true   // 21
    };

    // 127 == a button question
    static public final char[] ANSW_CHOICE = {
            0,   // 0
            0,   // 1
            0,   // 2
            0,   // 3
            0,   // 4
            0,   // 5
            0,   // 6
            0,   // 7
            0,   // 8
            0,   // 9
            0,   // 10
            0,   // 11
            0,   // 12
            0,   // 13
            0,   // 14
            0,   // 15
            127, // 16
            127, // 17
            1,   // 18
            127, // 19
            127, // 20
            2
    };

    public String[][] answerChoices;

    public QuestionData() {
        answerChoices = new String[3][];
    }
}
