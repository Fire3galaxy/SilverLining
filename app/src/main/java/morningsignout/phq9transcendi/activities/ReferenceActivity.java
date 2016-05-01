package morningsignout.phq9transcendi.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import morningsignout.phq9transcendi.R;

/**
 * Created by Stella on 3/2/2016.
 */
public class ReferenceActivity extends AppCompatActivity {
    TextView title;
    TextView subtitle;
    ListView contents;
    Button homeButton;
    ResourceAdapter resourceCustomAdapter;
    ReferenceAdapter referenceCustomAdapter;
    LinearLayout linearLayout;
    Context c;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.reference_view);



        title =(TextView)findViewById(R.id.Title);

        subtitle = (TextView)findViewById(R.id.Subtitle);

        contents = (ListView)findViewById(R.id.Contents);
        contents.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView parent, View view, int position, long id) {
                String url = "http://morningsignout.com";

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

            }
        });

        homeButton = (Button)findViewById(R.id.back_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ReferenceActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        title.setText(getString(R.string._references));
        subtitle.setText("");
        // Create the Custom Adapter Object
        referenceCustomAdapter = new ReferenceAdapter(this);

        // Set the Adapter
        contents.setAdapter(referenceCustomAdapter);



        linearLayout = (LinearLayout) findViewById(R.id.resourceRefView);
    }



}
