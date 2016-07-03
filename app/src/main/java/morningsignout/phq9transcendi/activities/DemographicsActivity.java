package morningsignout.phq9transcendi.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.firebase.client.Firebase;

import morningsignout.phq9transcendi.R;

/**
 * Created by Stella on 3/20/2016.
 */
public class DemographicsActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {
    static final String demo_log_name = "DemographicsActivity";

    private Spinner gender, ethnicity, schoolYear, familyFirst;
    private EditText ageField;

    private String gender_answer, ethnicity_answer, schoolYear_answer, familyFirst_answer;
    private int age_answer = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setup layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demographics);
        Firebase.setAndroidContext(this);

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

                    confirmSubmission();

                }
                //above if statement isn't working correctly so below is for testing purposes
                confirmSubmission();
            }
        });
    }

    // FIXME: Maybe one day include allowed range of age (not 1000, for example)
    // Get contents of age editText and turn into number (parsing fails if the field is empty or not a number)
    boolean setAgeAnswer() {
        try {
            age_answer = Integer.parseInt(ageField.getText().toString());
        } catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }

    //ALERT BOX: Make sure user wants to submit Demographics then start quiz activity
    void confirmSubmission() {

        AlertDialog.Builder alert = new AlertDialog.Builder(
                DemographicsActivity.this);
        alert.setMessage("Are you sure you want to submit?")
             .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     submitDemographics();

                     Intent intent = new Intent(DemographicsActivity.this, QuizActivity.class);
                     startActivity(intent);
                     finish();

                 }


             })
             .setNegativeButton("NO", new DialogInterface.OnClickListener() {

                 @Override
                 public void onClick(DialogInterface dialog, int which) {

                 }
             })
             .show();
    }

    // FIXME: 1. Network request to firebase for Demographics here
    // FIXME: 2. What if default selector value is the correct one, so user didn't touch? Then we should use
    // FIXME:    spinner.getSelectedItem().toString() instead of this complicated null check system.
    void submitDemographics() {
        //reference to firebase and create child
        Firebase ref = new Firebase(FirebaseExtras.DATA_URL);
        Firebase demo_info = ref.child("tests");

        //set family first bool value (for database)
        boolean familyFirst_bool = familyFirst_answer.equals("yes");

        //create user object
        DemographicDB user = new DemographicDB(age_answer,
                ethnicity_answer, familyFirst_bool, schoolYear_answer, gender_answer);

        demo_info.push().setValue(user); //push to database with unique ID

        Log.d(demo_log_name, String.valueOf(age_answer));
        Log.d(demo_log_name, gender_answer);
        Log.d(demo_log_name, ethnicity_answer);
        Log.d(demo_log_name, schoolYear_answer);
        Log.d(demo_log_name, familyFirst_answer);
    }

    // Checks that every field is answered (for spinner, at least touched)
    //FIXME: for some reason function is not working correctly.
    boolean demographicsIsCompleted() {
        // Default age_answer is 0, so user could not have answered
        boolean didNotComplete = (age_answer == 0);

        // Check spinner answers to see if user answered all questions
        didNotComplete = didNotComplete || checkForNullSpinnerAnswers();

        if (!didNotComplete) Log.d(demo_log_name, "Incomplete");

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
