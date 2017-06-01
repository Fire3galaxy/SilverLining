package morningsignout.phq9transcendi.activities.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import morningsignout.phq9transcendi.R;
import morningsignout.phq9transcendi.activities.HelperClasses.FirebaseExtras;
import morningsignout.phq9transcendi.activities.HelperClasses.ThemesAdapter;
import morningsignout.phq9transcendi.activities.HelperClasses.Utils;
import morningsignout.phq9transcendi.activities.PHQApplication;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Stella on 3/2/2016.
 */
public class ThemesActivity extends AppCompatActivity {
    ListView contents;
    Button homeButton;
    ThemesAdapter themeCustomAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Utils.onActivityCreateSetTheme(this, Utils.GetTheme(this));
        super.onCreate(savedInstanceState);

        setContentView(R.layout.themes);

        contents = (ListView)findViewById(R.id.Contents);
        contents.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Utils.SaveTheme("theme", position, ThemesActivity.this);
                Utils.changeToTheme(ThemesActivity.this);
                updateThemePref();
            }
        });

        homeButton = (Button)findViewById(R.id.back_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ThemesActivity.this, IndexActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Create the Custom Adapter Object
        themeCustomAdapter = new ThemesAdapter(this);

        // Set the Adapter
        contents.setAdapter(themeCustomAdapter);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));   // For custom Rubik font
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ThemesActivity.this, IndexActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateThemePref() {
        String userID = FirebaseAuth.getInstance(PHQApplication.getFirebaseAppInstance())
                .getCurrentUser().getUid();
        DatabaseReference themePref = FirebaseDatabase.getInstance(PHQApplication.getFirebaseAppInstance())
                .getReference("users/" + userID + "/themePreference");
        themePref.setValue(Utils.THEME_NAMES[Utils.GetTheme(this)]);
    }
}
