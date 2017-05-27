package morningsignout.phq9transcendi.activities.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import morningsignout.phq9transcendi.R;
import morningsignout.phq9transcendi.activities.HelperClasses.FirebaseExtras;
import morningsignout.phq9transcendi.activities.HelperClasses.ThemesAdapter;
import morningsignout.phq9transcendi.activities.HelperClasses.Utils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Stella on 3/2/2016.
 */
public class Themes extends AppCompatActivity {
    ListView contents;
    Button homeButton;
    ThemesAdapter themeCustomAdapter;
//    Firebase userRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Utils.onActivityCreateSetTheme(this, Utils.GetTheme(this));
        super.onCreate(savedInstanceState);

        setContentView(R.layout.themes);

        String userID = getSharedPreferences(LaunchScreenActivity.PREFS_NAME, MODE_PRIVATE)
                .getString(FirebaseExtras.USER_ID, null);
        // FIXME: update theme in database
//        if(userID != null)
//            userRef = new Firebase(FirebaseExtras.getDataURL()).child("users").child(userID);

        contents = (ListView)findViewById(R.id.Contents);
        contents.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView parent, View view, int position, long id) {
//                userRef.child("themePreference").setValue(Utils.THEME_NAMES[position]);
                Utils.SaveTheme("theme", position, Themes.this);
                Utils.changeToTheme(Themes.this);
            }
        });

        homeButton = (Button)findViewById(R.id.back_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Themes.this, IndexActivity.class);
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
        Intent intent = new Intent(Themes.this, IndexActivity.class);
        startActivity(intent);
        finish();
    }
}
