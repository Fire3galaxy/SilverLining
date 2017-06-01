package morningsignout.phq9transcendi.activities.HelperClasses;

import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import morningsignout.phq9transcendi.R;
import morningsignout.phq9transcendi.activities.Activities.ThemesActivity;

/**
 * Created by Stella on 3/2/2016.
 */
public class ThemesAdapter extends BaseAdapter {
    private ThemesActivity themeActivity;
    private String[] contents;

    public ThemesAdapter(ThemesActivity t){
        this.themeActivity = t;
        this.contents = t.getResources().getStringArray(R.array.themes);

    }

    public int getCount() {
        return contents.length;
    }

    public long getItemId(int position) {
        return position;
    }

    public Object getItem(int position) {
        return contents[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ReferencesViewHolder viewHolder;

        if (row == null) {
            viewHolder = new ReferencesViewHolder();

            LayoutInflater inflater = themeActivity.getLayoutInflater();
            row = inflater.inflate(R.layout.list_item_view, parent, false);

            viewHolder.textViewTitle = (TextView) row.findViewById(R.id.textView_rr);
            row.setTag(viewHolder);
        } else {
            viewHolder = (ReferencesViewHolder) row.getTag();
        }

        if (themeActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (position == 0)
                viewHolder.textViewTitle.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, R.drawable.ice_front_screen, 0);
            else if (position == 1)
                viewHolder.textViewTitle.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, R.drawable.cashmere_front_screen, 0);
        } else {
            if (position == 0)
                viewHolder.textViewTitle.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, R.drawable.ice_front_screen_land, 0);
            else if (position == 1)
                viewHolder.textViewTitle.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, R.drawable.cashmere_front_screen_land, 0);
        }

        viewHolder.textViewTitle.setText(this.contents[position]);

        return row;
    }

    private class ReferencesViewHolder {
        TextView textViewTitle;
    }
}
