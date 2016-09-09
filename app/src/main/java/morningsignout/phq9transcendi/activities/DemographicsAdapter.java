package morningsignout.phq9transcendi.activities;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by pokeforce on 9/1/16.
 */
public class DemographicsAdapter extends ArrayAdapter<CharSequence> {
    private final static int IS_EMPTY_VIEW = 1;

    public DemographicsAdapter(Context context, int resource, CharSequence[] objects) {
        super(context, resource, objects);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (position == 0) {
            TextView emptyView = new TextView(getContext());
            emptyView.setHeight(0);
            emptyView.setTag(IS_EMPTY_VIEW);    // Mark the empty view
            return emptyView;
        } else {
            if (convertView == null || convertView.getTag() != null)
                return super.getDropDownView(position, null, parent);   // Don't use empty view, make new one
            else
                return super.getDropDownView(position, convertView, parent);    // Can use previous view
        }
    }
}
