package morningsignout.phq9transcendi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import morningsignout.phq9transcendi.R;

/**
 * Created by Stella on 3/2/2016.
 */
public class ResourceActivity extends AppCompatActivity {
    TextView title;
    TextView subtitle;
    ListView contents;
    Button homeButton;
    ResourceAdapter resourceCustomAdapter;
    ReferenceAdapter referenceCustomAdapter;
    LinearLayout linearLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.resource_ref_view);



        title =(TextView)findViewById(R.id.Title);

        subtitle = (TextView)findViewById(R.id.Subtitle);

        contents = (ListView)findViewById(R.id.Contents);
        homeButton = (Button)findViewById(R.id.back_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ResourceActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        String page_type = getIntent().getStringExtra("page_type");
            System.out.print(page_type);
            if(page_type.equals("Resources")){
                title.setText("Resources");
                subtitle.setText("Latest mental health news");
                // Create the Custom Adapter Object
                resourceCustomAdapter = new ResourceAdapter(this);

                // Set the Adapter
                contents.setAdapter(resourceCustomAdapter);
            }
            else {
                title.setText("References");
                subtitle.setText("");
                // Create the Custom Adapter Object
                referenceCustomAdapter = new ReferenceAdapter(this);

                // Set the Adapter
                contents.setAdapter(referenceCustomAdapter);
            }


        linearLayout = (LinearLayout) findViewById(R.id.resourceRefView);
    }



}
