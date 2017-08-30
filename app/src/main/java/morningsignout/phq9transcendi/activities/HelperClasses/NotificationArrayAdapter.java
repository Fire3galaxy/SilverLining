package morningsignout.phq9transcendi.activities.HelperClasses;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import morningsignout.phq9transcendi.activities.PHQApplication;

/**
 * Created by Daniel on 8/29/2017.
 */

public class NotificationArrayAdapter extends ArrayAdapter<CharSequence> {


    public NotificationArrayAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    @Override
    public int getCount() {
        if (PHQApplication.isDebug())
            return super.getCount() - 1;    // To omit tester item

        return super.getCount();
    }
}
