package morningsignout.phq9transcendi.activities;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;

/**
 * Created by pokeforce on 7/26/16.
 */
public class BlinkScrollView extends ScrollView {
    private static final int FADE_DURATION = 50;

    public BlinkScrollView(Context context) {
        super(context);
        setVerticalScrollBarEnabled(true);
    }

    public BlinkScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setVerticalScrollBarEnabled(true);
    }

    public BlinkScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setVerticalScrollBarEnabled(true);
    }

    public boolean canScrollVertically() {
        return canScrollVertically(-1) || canScrollVertically(1);
    }

    // blinks the scrollbar twice
    public void blinkScrollBar() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Runnable showBars = new Runnable() {
                    @Override
                    public void run() {
                        smoothScrollTo(0, 0);
                    }
                };

                for (int i = 0; i < 2; i++) {
                    post(showBars);

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
