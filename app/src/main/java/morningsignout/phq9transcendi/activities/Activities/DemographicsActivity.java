package morningsignout.phq9transcendi.activities.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.firebase.client.Firebase;

import morningsignout.phq9transcendi.R;
import morningsignout.phq9transcendi.activities.HelperClasses.DemographicsAdapter;
import morningsignout.phq9transcendi.activities.HelperClasses.FirebaseExtras;
import morningsignout.phq9transcendi.activities.HelperClasses.Utils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Stella on 3/20/2016.
 * If new school is added in Demographics: just change string.xml. No need to change code here.
 */
public class DemographicsActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {
    static final String demo_log_name = "DemographicsActivity";

    private AlertDialog.Builder dialogBuilder;
    private Spinner gender, ethnicity, schoolYear, familyFirst, collegeName;
    private EditText ageField, genderField, ethnicityField, collegeNameField;

    private String gender_answer, ethnicity_answer, schoolYear_answer, familyFirst_answer, collegeName_answer;
    private int age_answer = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setup layout

        Utils.onActivityCreateSetTheme(this, Utils.GetTheme(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demographics);

        // Various fields for demographics
        // Age
        ageField = (EditText) findViewById(R.id.demo_age_answer);

        // Gender (male or female)
        gender = (Spinner) findViewById(R.id.demo_gender_answer);
        String[] genderOptions = getResources().getStringArray(R.array.gender_array);
        DemographicsAdapter genderAdapter =
                new DemographicsAdapter(this, android.R.layout.simple_spinner_item, genderOptions);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(genderAdapter);
        gender.setOnItemSelectedListener(this);

        genderField = (EditText) findViewById(R.id.gender_custom_answer);
        genderField.setVisibility(View.GONE);

        // Ethnicity - list of possibilities in string.xml
        ethnicity = (Spinner) findViewById(R.id.demo_ethnic_answer);
        String[] ethnicityOptions = getResources().getStringArray(R.array.ethnicity_array);
        DemographicsAdapter ethnicityAdapter =
                new DemographicsAdapter(this, android.R.layout.simple_spinner_item, ethnicityOptions);
        ethnicityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ethnicity.setAdapter(ethnicityAdapter);
        ethnicity.setOnItemSelectedListener(this);

        ethnicityField = (EditText) findViewById(R.id.ethnicity_custom_answer);
        ethnicityField.setVisibility(View.GONE);

        // School year - list of possibilities in string.xml
        schoolYear = (Spinner) findViewById(R.id.demo_sch_yr_answer);
        String[] schoolOptions = getResources().getStringArray(R.array.school_year);
        DemographicsAdapter schoolAdapter =
                new DemographicsAdapter(this, android.R.layout.simple_spinner_item, schoolOptions);
        schoolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        schoolYear.setAdapter(schoolAdapter);
        schoolYear.setOnItemSelectedListener(this);

        // First student to attend college?
        familyFirst = (Spinner) findViewById(R.id.demo_first_answer);
        String[] familyOptions = getResources().getStringArray(R.array.yes_no_array);
        DemographicsAdapter familyAdapter =
                new DemographicsAdapter(this, android.R.layout.simple_spinner_item, familyOptions);
        familyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        familyFirst.setAdapter(familyAdapter);
        familyFirst.setOnItemSelectedListener(this);

        // Name of college attended
        collegeName = (Spinner) findViewById(R.id.demo_college_answer);
        String[] collegeNameOptions = getResources().getStringArray(R.array.college_name_array);
        DemographicsAdapter collegeNameAdapter =
                new DemographicsAdapter(this, android.R.layout.simple_spinner_item, collegeNameOptions);
        collegeNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        collegeName.setAdapter(collegeNameAdapter);
        collegeName.setOnItemSelectedListener(this);

        collegeNameField = (EditText) findViewById(R.id.college_name_custom_answer);
        collegeNameField.setVisibility(View.GONE);

        // Start the questionnaire/submit the demographics
        Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setAgeAnswer();             // Grab text from edittext and set answer (may throw exception)
                setGenderAnswerCustom();    // Grab text if custom edittext was used
                setEthnicityAnswerCustom();
                setCollegeNameAnswerCustom();
                confirmSubmission();        // Submit data to firebase
            }
        });

        dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage(R.string.dialog_quit_demographics)
                .setPositiveButton(R.string.dialog_take_quiz, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(DemographicsActivity.this, QuizActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setNeutralButton(R.string.dialog_leave, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        dialogBuilder.create().show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));   // For custom Rubik font
    }

    // Get contents of age editText and turn into number (parsing fails if the field is empty or not a number)
    boolean setAgeAnswer() {
        try {
            age_answer = Integer.parseInt(ageField.getText().toString());
        } catch (NumberFormatException nfe) {
            return false;
        }

        return true;
    }

    // Sets answer if it's a special edit field
    boolean setGenderAnswerCustom() {
        if (gender_answer.equals("Enter option")) {
            gender_answer = genderField.getText().toString();
            return true;
        }

        return false;
    }

    // Sets answer if it's a special edit field
    boolean setEthnicityAnswerCustom() {
        if (ethnicity_answer.equals("Enter option")) {
            ethnicity_answer = ethnicityField.getText().toString();
            return true;
        }

        return false;
    }

    // Sets answer if it's a special edit field
    boolean setCollegeNameAnswerCustom() {
        if (collegeName_answer.equals("Enter option")) {
            collegeName_answer = collegeNameField.getText().toString();
            return true;
        }

        return false;
    }

    //ALERT BOX: Make sure user wants to submit Demographics then start quiz activity
    void confirmSubmission() {
        AlertDialog.Builder alert = new AlertDialog.Builder(DemographicsActivity.this);
        alert.setMessage("Are you sure you want to submit?")
             .setPositiveButton("YES", new DialogInterface.OnClickListener() {

                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                     uploadDataToDatabase();

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

    void uploadDataToDatabase() {
        //reference to firebase and create child
        Firebase ref = new Firebase(FirebaseExtras.DATA_URL);
        String userID = getSharedPreferences(LaunchScreenActivity.PREFS_NAME, MODE_PRIVATE)
                .getString(FirebaseExtras.USER_ID, null);

        if(userID != null) {
            Firebase userRef = ref.child("users").child(userID);

            if (age_answer != -1)
                userRef.child("age").setValue(age_answer);
            if (!gender.getSelectedItem().equals("-"))  // Check that spinner is not on dash item
                userRef.child("gender").setValue(gender_answer.toLowerCase());
            if (!ethnicity.getSelectedItem().equals("-"))
                userRef.child("ethnicity").setValue(ethnicity_answer.toLowerCase());
            if (!familyFirst.getSelectedItem().equals("-"))
                userRef.child("firstGenerationCollege").setValue(familyFirst_answer.toLowerCase());
            if (!schoolYear.getSelectedItem().equals("-"))
                userRef.child("yearInSchool").setValue(schoolYear_answer.toLowerCase());
            if (!collegeName.getSelectedItem().equals("-"))
                userRef.child("collegeName").setValue(collegeName_answer.toLowerCase());

            //Log.d(demo_log_name, "Submitted Data");
        }


        //Log.d(demo_log_name, String.valueOf(age_answer));
        //Log.d(demo_log_name, gender_answer);
        //Log.d(demo_log_name, ethnicity_answer);
        //Log.d(demo_log_name, schoolYear_answer);
        //Log.d(demo_log_name, familyFirst_answer);
        //Log.d(demo_log_name, collegeName_answer);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner) parent;
        if (gender.equals(spinner)) {
            gender_answer = String.valueOf(spinner.getItemAtPosition(position));
            if (gender_answer.equals("Enter option"))
                genderField.setVisibility(View.VISIBLE);
            else
                genderField.setVisibility(View.GONE);
        } else if (ethnicity.equals(spinner)) {
            ethnicity_answer = String.valueOf(spinner.getItemAtPosition(position));
            if (ethnicity_answer.equals("Enter option"))
                ethnicityField.setVisibility(View.VISIBLE);
            else
                ethnicityField.setVisibility(View.GONE);
        } else if (schoolYear.equals(spinner)) {
            schoolYear_answer = String.valueOf(spinner.getItemAtPosition(position));
        } else if (familyFirst.equals(spinner)) {
            familyFirst_answer = String.valueOf(spinner.getItemAtPosition(position));
        } else {
            collegeName_answer = String.valueOf(spinner.getItemAtPosition(position));
            if (collegeName_answer.equals("Enter option"))
                collegeNameField.setVisibility(View.VISIBLE);
            else
                collegeNameField.setVisibility(View.GONE);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
