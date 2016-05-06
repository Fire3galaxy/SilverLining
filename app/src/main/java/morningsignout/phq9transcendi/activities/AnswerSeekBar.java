package morningsignout.phq9transcendi.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.SeekBar;

/**
 * Created by pokeforce on 5/5/16.
 */
public class AnswerSeekBar extends SeekBar {
    int previousAnswer = 0;
    static final int RANGE = 40;

    public AnswerSeekBar(Context context) {
        super(context);
    }

    public AnswerSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnswerSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AnswerSeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);

        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            Log.d("AnswerSeekBar", "Touch Event");
            int progress = getProgress();
            int answer = calculateAnswer(progress);

            setProgress(answer);
            if (previousAnswer != answer) previousAnswer = answer;
        }

        return result; // Still do default activity
    }

    // Given that max = 300 and 0-50 is Not at all, 51-150 is Few days a week,
    // 151-250 is More than half a week, and 251-300 is Everyday, translate the big
    // seekbar progress into the correct answer.
    // Alternate idea: Change answer only if user "seems" to want to do so (within range).
    int calculateAnswer(int progress) {
        if (progress < RANGE) return 0;
        else if (Math.abs(progress - 100) < RANGE) return 100;
        else if (Math.abs(progress - 200) < RANGE) return 200;
        else if (Math.abs(progress - 300) < RANGE) return 300;

        return previousAnswer;

//        if (progress < 51) return 0;
//        else if (progress < 151) return 100;
//        else if (progress < 251) return 200;
//        else return 300;

        // Same logic in equation form (faster than branches, less intuitive)
//        int answer = ((progress - 1) / 50);
//        return (answer / 2 + answer % 2) * 100;
    }
}
