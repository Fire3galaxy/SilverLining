package morningsignout.phq9transcendi.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import morningsignout.phq9transcendi.R;

/**
 * Created by Stella on 3/2/2016.
 */
public class ReferenceAdapter extends BaseAdapter {

    Context context;
    String[] contents;


    public ReferenceAdapter(Context c){
        this.context = c;
        this.contents = c.getResources().getStringArray(R.array.resource_array);
    }
    //private final Integer[] images = {"", "", ""};

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

            LayoutInflater inflater = ((ReferenceActivity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.list_item_view, parent, false);

            viewHolder.textViewTitle = (TextView) row.findViewById(R.id.textView_rr);
            //viewHolder.imageView = (ImageView) row.findViewById(R.id.imageView_rr);
            row.setTag(viewHolder);
        } else {
            viewHolder = (ReferencesViewHolder) row.getTag();
        }

        viewHolder.textViewTitle.setText(this.contents[position]);
        //viewHolder.imageView.setImageResource(this.images[position]);

        return row;
    }

    private class ReferencesViewHolder {
        TextView textViewTitle;
        ImageView imageView;
    }


}
