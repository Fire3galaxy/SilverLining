package morningsignout.phq9transcendi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import morningsignout.phq9transcendi.R;

/**
 * Created by Stella on 3/20/2016.
 */
public class DemographicsActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {
    static final String demo_log_name = "DemographicsActivity";

    private Spinner gender, ethnicity, schoolYear,familyFirst;
    private EditText ageField;

    private String gender_answer, ethnicity_answer, schoolYear_answer, familyFirst_answer;
    private int age_answer = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setup layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demographics);

        // Various fields for demographics
        // Age
        ageField = (EditText) findViewById(R.id.demo_age_answer);
        String age = ageField.getText().toString();

        // Gender (male or female)
        gender = (Spinner) findViewById(R.id.demo_gender_answer);
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(genderAdapter);
        gender.setOnItemSelectedListener(this);

        // Ethnicity - list of possibilities in string.xml
        ethnicity = (Spinner) findViewById(R.id.demo_ethnic_answer);
        ArrayAdapter<CharSequence> ethnicityAdapter = ArrayAdapter.createFromResource(this,
                R.array.ethnicity_array, android.R.layout.simple_spinner_item);
        ethnicityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ethnicity.setAdapter(ethnicityAdapter);
        ethnicity.setOnItemSelectedListener(this);

        // School year - list of possibilities in string.xml
        schoolYear = (Spinner) findViewById(R.id.demo_sch_yr_answer);
        ArrayAdapter<CharSequence> schoolAdapter = ArrayAdapter.createFromResource(this,
                R.array.school_year, android.R.layout.simple_spinner_item);
        schoolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        schoolYear.setAdapter(schoolAdapter);
        schoolYear.setOnItemSelectedListener(this);

        // First student to attend college?
        familyFirst = (Spinner) findViewById(R.id.demo_first_answer);
        ArrayAdapter<CharSequence> familyAdapter = ArrayAdapter.createFromResource(this,
                R.array.yes_no_array, android.R.layout.simple_spinner_item);
        familyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        familyFirst.setAdapter(familyAdapter);
        familyFirst.setOnItemSelectedListener(this);

        // Start the questionnaire/submit the demographics
        // FIXME: Should prevent user from continuing if demographics is complete, to at least ask "are you sure?"
        Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setAgeAnswer();                     // Grab text from edittext and set answer (may throw exception)
                if (demographicsIsCompleted()) {    // Only submit if all fields finished
                    submitDemographics();
                }

                Intent intent = new Intent(DemographicsActivity.this, QuizActivity.class);
                startActivity(intent);
            }
        });
    }

    boolean setAgeAnswer() {
        // FIXME: Maybe one day include allowed range of age (not 1000, for example)
        // Get contents of age editText and turn into number (parsing fails if field is empty)
        try {
            age_answer = Integer.parseInt(ageField.getText().toString());
        } catch (NumberFormatException nfe) {
            // If failed, user did not input age
            return false;
        }

        return true;
    }

    // FIXME: Network request to firebase for Demographics here
    // FIXME: What if default selector value is the correct one? Then we should use
    // FIXME: spinner.getSelectedItem().toString() instead of this complicated null check system.
    void submitDemographics() {
        Log.d(demo_log_name, String.valueOf(age_answer));
        Log.d(demo_log_name, gender_answer);
        Log.d(demo_log_name, ethnicity_answer);
        Log.d(demo_log_name, schoolYear_answer);
        Log.d(demo_log_name, familyFirst_answer);
    }

    // Checks that every field is answered
    boolean demographicsIsCompleted() {
        // Default age_answer is 0, so user could not have answered
        boolean didNotComplete = (age_answer == 0);

        // Check spinner answers to see if user answered all questions
        didNotComplete = didNotComplete || checkForNullSpinnerAnswers();

        return didNotComplete;
    }

    // Make sure all strings are answered
    boolean checkForNullSpinnerAnswers() {
        return gender_answer == null
                || ethnicity_answer == null
                || schoolYear_answer == null
                || familyFirst == null;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(demo_log_name, "HERE");
        Spinner spinner = (Spinner) parent;
        if (gender.equals(spinner))
            gender_answer = String.valueOf(spinner.getItemAtPosition(position));
        else if (ethnicity.equals(spinner))
            ethnicity_answer = String.valueOf(spinner.getItemAtPosition(position));
        else if (schoolYear.equals(spinner))
            schoolYear_answer = String.valueOf(spinner.getItemAtPosition(position));
        else
            familyFirst_answer = String.valueOf(spinner.getItemAtPosition(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
