package morningsignout.phq9transcendi.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;

import morningsignout.phq9transcendi.R;

public class NewUserActivity extends AppCompatActivity {

    private EditText usernameInput, passwordInput, confirmPassword, emailInput;
    private Button confirm;

    //For the popup
    private PopupWindow popup;
    private Button popupButton;
    private TextView popupText;
    private LinearLayout popupLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        usernameInput = (EditText) findViewById(R.id.nameInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
        confirmPassword = (EditText) findViewById(R.id.passwordInput2);
        emailInput = (EditText) findViewById(R.id.emailInput);
        confirm = (Button) findViewById(R.id.confirmInput);

        usernameInput.setHint("Username");
        passwordInput.setHint("Password");
        confirmPassword.setHint("Confirm Password");
        emailInput.setHint("Email (optional)");

        confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //passwordInput.getText().equals(confirmPassword.getText())
                if(usernameInput.getText().length() > 0 && passwordInput.length() > 0) {
                    if(confirmPassword.getText().length() > 0) {
                        if (passwordInput.getText().toString().equals(confirmPassword.getText().toString())) {
                            alertConfirm();
                        } else {
                            alertMatch();
                        }
                    } else {
                        alertPassConfirm();
                    }
                } else {
                    alertEmpty();
                }
            }
        });
    }

    private void alertEmpty() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Error")
                .setMessage("Please input a username and password.")
                .setPositiveButton("Ok", null)
                .show();
    }

    private void alertMatch() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Error")
                .setMessage("Your passwords do not match.")
                .setPositiveButton("Ok", null)
                .show();
    }

    private void alertPassConfirm() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Error")
                .setMessage("Please confirm your password.")
                .setPositiveButton("Ok", null)
                .show();
    }

    private void alertExists() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Error")
                .setMessage("Username already exists.")
                .setPositiveButton("Ok", null)
                .show();
    }


    private void alertConfirm() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("User " + usernameInput.getText() + " has been created!")
                .setMessage("You can now log in with this username.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent menu = new Intent(NewUserActivity.this, MainActivity.class);
                        startActivity(menu);
                    }
                })
                .show();
    }
}
