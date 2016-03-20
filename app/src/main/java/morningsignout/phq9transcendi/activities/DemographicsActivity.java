package morningsignout.phq9transcendi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import morningsignout.phq9transcendi.R;

/**
 * Created by Stella on 3/20/2016.
 */
public class DemographicsActivity extends AppCompatActivity {

    private Spinner gender, ethnicity, schoolYear,familyFirst;
    private Button start;
    private EditText ageField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.demographics);

        ageField = (EditText) findViewById(R.id.Age);
        String age = ageField.getText().toString();

        gender = (Spinner) findViewById(R.id.Gender);
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(genderAdapter);

        ethnicity = (Spinner) findViewById(R.id.Ethnicity);
        ArrayAdapter<CharSequence> ethnicityAdapter = ArrayAdapter.createFromResource(this,
                R.array.ethnicity_array, android.R.layout.simple_spinner_item);
        ethnicityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ethnicity.setAdapter(ethnicityAdapter);

        schoolYear = (Spinner) findViewById(R.id.SchoolYear);
        ArrayAdapter<CharSequence> schoolAdapter = ArrayAdapter.createFromResource(this,
                R.array.school_year, android.R.layout.simple_spinner_item);
        schoolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        schoolYear.setAdapter(schoolAdapter);

        familyFirst = (Spinner) findViewById(R.id.FamilyFirst);
        ArrayAdapter<CharSequence> familyAdapter = ArrayAdapter.createFromResource(this,
                R.array.yes_no_array, android.R.layout.simple_spinner_item);
        familyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        familyFirst.setAdapter(familyAdapter);

        start = (Button)findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(DemographicsActivity.this, QuizActivity.class);
                startActivity(intent);
            }
        });


    }



}
