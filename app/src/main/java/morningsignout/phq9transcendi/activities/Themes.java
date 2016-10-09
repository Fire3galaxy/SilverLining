package morningsignout.phq9transcendi.activities;

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
 * Created by Stella on 9/1/2016.
 */
public class Themes extends AppCompatActivity {

    ListView contents;
    Button homeButton;
    ReferenceAdapter themeCustomAdapter;
    LinearLayout linearLayout;
    int sTheme = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Utils.onActivityCreateSetTheme(this, Utils.GetTheme(this));
        super.onCreate(savedInstanceState);

        setContentView(R.layout.reference_view);


        contents = (ListView)findViewById(R.id.Contents);
        contents.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView parent, View view, int position, long id) {

                Utils.SaveTheme("theme", position, Themes.this);
                Utils.changeToTheme(Themes.this);

            }
        });

        homeButton = (Button)findViewById(R.id.back_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Themes.this, IndexActivity.class);
                startActivity(intent);
            }
        });


        // Create the Custom Adapter Object
        themeCustomAdapter = new ReferenceAdapter(this);

        // Set the Adapter
        contents.setAdapter(themeCustomAdapter);

        linearLayout = (LinearLayout) findViewById(R.id.resourceRefView);
    }
}


