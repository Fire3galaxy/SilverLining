package morningsignout.phq9transcendi.activities.RangeSliderCustom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import morningsignout.phq9transcendi.R;

/**
 * Created by Daniel on 12/22/2016.
 */
public class RangeSliderTextAddOns implements View.OnLayoutChangeListener {
    private class CircleCenter {
        RangeSliderView slider;
        int circleIndex;
        float x, y;

        CircleCenter(RangeSliderView slider, int circleIndex) {
            this.slider = slider;
            this.circleIndex = circleIndex;
            updateCenter();
        }

        void updateCenter() {
            float multiplier = circleIndex + .5f;
            float segmentWidth = slider.getSegmentWidth();
            float centerY = slider.getPaddingTop() + (slider.getHeightWithPadding() >> 1);
            x = slider.getX() + segmentWidth * multiplier;
            y = slider.getY() + centerY;
        }
    }

    private RangeSliderView sliderView;
//        private TextViewAtCircle[] answerViews;
    private TextView[] answerViews;
    private CircleCenter[] circleCenters;

    @SuppressLint("RtlHardcoded")
    public RangeSliderTextAddOns(RangeSliderView r, Activity activity) {
        sliderView = r;
        answerViews = new TextView[5];  // Insert read to QuestionData(?) to get max num
        circleCenters = new CircleCenter[5];

        answerViews[0] = (TextView) activity.findViewById(R.id.range_answer_0);
        answerViews[1] = (TextView) activity.findViewById(R.id.range_answer_1);
        answerViews[2] = (TextView) activity.findViewById(R.id.range_answer_2);
        answerViews[3] = (TextView) activity.findViewById(R.id.range_answer_3);
        answerViews[4] = (TextView) activity.findViewById(R.id.range_answer_4);

        for (int i = 0; i < 5; i++) {
            circleCenters[i] = new CircleCenter(r, i);
            answerViews[i].addOnLayoutChangeListener(this);
        }
        answerViews[0].setGravity(Gravity.LEFT);

        r.addOnLayoutChangeListener(this);
    }

    @Override
    public void onLayoutChange(View view, int left, int top, int right, int bottom,
                               int oldLeft, int oldTop, int oldRight, int oldBottom) {
        // Update textView positions after sliderView is visible
        if (view instanceof RangeSliderView) {
            view.removeOnLayoutChangeListener(this);
            updateTextViewParams();
        }
        // Update x value of textView after width change
        else {
            // Get index of changed view
            int index = 0;
            for (; index < answerViews.length; index++)
                if (answerViews[index].equals(view))
                    break;

            // Set x value
            float x0 = circleCenters[index].x - (view.getWidth() / 2.0f);
            if (index == 0) {
                if (sliderView.getX() > x0) {
                    x0 = sliderView.getX();
                }
            } else if (x0 + (right - left) > sliderView.getRight()) {
                x0 = sliderView.getRight() - (right - left);
            }

            answerViews[index].setX(x0);
        }
    }

    public void setAnswers(String[] answers) {
//        Log.d("RangeSlider+", "in changeAnswers");
        for (int i = 0; i < answers.length; i++)
            answerViews[i].setText(answers[i]);

        sliderView.setRangeCount(answers.length);
        sliderView.resetDrawValues();
        sliderView.invalidate();
        updateTextViewParams();
    }

    @SuppressLint("RtlHardcoded")
    private void updateTextViewParams() {
        int rangeCount = sliderView.getRangeCount();
        int width;
        int leftSideWidth, rightSideWidth;
        RelativeLayout.LayoutParams params;

        if (rangeCount == 2) {
            // Answer 1
            circleCenters[0].updateCenter();
            width = (int) (Math.min(sliderView.getSegmentWidth(), circleCenters[0].x) * 2.0f);
            params = (RelativeLayout.LayoutParams) answerViews[0].getLayoutParams();

            answerViews[0].setMaxWidth(width);
            params.addRule(RelativeLayout.BELOW, sliderView.getId());
            answerViews[0].setGravity(Gravity.CENTER_HORIZONTAL);
            answerViews[0].setVisibility(sliderView.getVisibility());

            // Answer 2
            circleCenters[1].updateCenter();
            width = (int) (Math.min(sliderView.getSegmentWidth(),
                    sliderView.getRight() - circleCenters[1].x) * 2.0f);
            params = (RelativeLayout.LayoutParams) answerViews[1].getLayoutParams();

            answerViews[1].setMaxWidth(width);
            params.addRule(RelativeLayout.ABOVE, 0);    // Null out old rule for above
            params.addRule(RelativeLayout.BELOW, sliderView.getId());
            answerViews[1].setGravity(Gravity.CENTER_HORIZONTAL);
            answerViews[1].setVisibility(sliderView.getVisibility());
        } else {
            for (int i = 0; i < rangeCount; i++) {
                circleCenters[i].updateCenter();
                width = 2 * (int) sliderView.getSegmentWidth();
                leftSideWidth = (int) (circleCenters[i].x + sliderView.getSegmentWidth())
                        - sliderView.getLeft();
                rightSideWidth = sliderView.getRight() -
                        (int) (circleCenters[i].x - sliderView.getSegmentWidth());
                params = (RelativeLayout.LayoutParams) answerViews[i].getLayoutParams();

                // Set max width value
                if (i == 0 && leftSideWidth < width)
                    width = leftSideWidth;
                else if (i == rangeCount - 1 && rightSideWidth < width)
                    width = rightSideWidth;
                answerViews[i].setMaxWidth(width);

                // Text alignment
                if (i == 0)
                    answerViews[i].setGravity(Gravity.LEFT);
                else if (i == rangeCount - 1)
                    answerViews[i].setGravity(Gravity.RIGHT);
                else
                    answerViews[i].setGravity(Gravity.CENTER_HORIZONTAL);

                // Above or below slider
                if (i % 2 == 0) {
                    params.addRule(RelativeLayout.BELOW, sliderView.getId());
                    params.addRule(RelativeLayout.ABOVE, 0);    // Null out old rule for above
                } else {
                    params.addRule(RelativeLayout.ABOVE, sliderView.getId());
                    params.addRule(RelativeLayout.BELOW, 0);    // Null out old rule for above
                }

                // Match sliderView's visibility
                answerViews[i].setVisibility(sliderView.getVisibility());
            }
        }

        // Make unneeded textViews gone
        for (int i = rangeCount; i < answerViews.length; i++)
            answerViews[i].setVisibility(View.GONE);
    }

    public void setVisibility(int visibility) {
        int rangeCount = sliderView.getRangeCount();
        for (int i = 0; i < rangeCount; i++)
            answerViews[i].setVisibility(visibility);
        sliderView.setVisibility(visibility);
    }
}

