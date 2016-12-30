package morningsignout.phq9transcendi.activities.HelperClasses;

/**
 * Created by Daniel on 12/18/2016.
 * Intended to make adding questions much easier. No dependency in QuizActivity for particular
 * number or organization of questions except on this class and Scores (category of new question).
 *
 * Warning: Red flag question positions are FIXED in Scores class. If those positions change, then
 * you have to edit Scores class. They are also assumed to be consecutive.
 *
 * What to update when question is added: Every variable. If no new answer type is needed, there is
 * no need to add a new type. If so, add a new type and change the class constructor.
 * If you have more than 5 possible answers for a question, then RangeSliderTextAddOns,
 * activity_quiz.xml, and @style/[every styleName]RangeSlider need to be updated to accommodate this.
 */
public class QuestionData {
    static public final int NORMAL = 0, // Answer array types
            FLAG = 1,
            DEPRESSION = 2,
            SITUATION = 3,
            APPOINTMENT = 4,
            STRANGER = 5,
            SUPPORTIVE = 6, // Currently the most slider answers with 5. (Fixed max of textviews in activity_quiz.xml)
            YES_NO = 7;
    static public final int NUM_QUESTIONS = 27; // total number of questions
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
            true,  // 21
            true,  // 22
            true,  // 23
            true,  // 24
            true,  // 25
            true   // 26
    };

    static public final char[] ANSW_CHOICE = {
            NORMAL,     // 0
            NORMAL,     // 1
            NORMAL,     // 2
            NORMAL,     // 3
            NORMAL,     // 4
            NORMAL,     // 5
            NORMAL,     // 6
            NORMAL,     // 7
            NORMAL,     // 8
            NORMAL,     // 9
            NORMAL,     // 10
            NORMAL,     // 11
            NORMAL,     // 12
            NORMAL,     // 13
            NORMAL,     // 14
            NORMAL,     // 15
            YES_NO,     // 16
            YES_NO,     // 17
            FLAG,       // 18
            YES_NO,     // 19
            YES_NO,     // 20
            DEPRESSION, // 21
            SITUATION,  // 22
            SITUATION,  // 23
            APPOINTMENT,// 24
            STRANGER,   // 25
            SUPPORTIVE  // 26
    };

    public String[][] answerChoices;

    public QuestionData() {
        answerChoices = new String[8][];
    }
}
