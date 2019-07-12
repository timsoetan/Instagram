package com.example.instagram.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.instagram.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {

    // Signup info
    private EditText etEmailInput;
    private EditText etUsernameInput;
    private EditText etPasswordInput;
    private EditText etConfirmPasswordInput;
    private Button btnSignIn;
    private TextView tvSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Create animated gradient background
        ConstraintLayout constraintLayout = findViewById(R.id.signinBackground);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        // Find references for the views
        etEmailInput = findViewById(R.id.etEmailInput);
        etUsernameInput = findViewById(R.id.etUsernameInput);
        etPasswordInput = findViewById(R.id.etPasswordInput);
        etConfirmPasswordInput = findViewById(R.id.etConfirmPasswordInput);
        btnSignIn = findViewById(R.id.btnLogIn);
        tvSignIn = findViewById(R.id.btnSignIn);

        // Initially disable button
        btnSignIn.setEnabled(false);
        btnSignIn.setAlpha((float ) 0.5);

        // Set up listener for username/password input
        etEmailInput.addTextChangedListener(signupAvailable);
        etUsernameInput.addTextChangedListener(signupAvailable);
        etPasswordInput.addTextChangedListener(signupAvailable);
        etConfirmPasswordInput.addTextChangedListener(signupAvailable);

        // Set up listener for sign in button
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Set up listener for signup button
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = etEmailInput.getText().toString();
                final String username = etUsernameInput.getText().toString();
                final String password = etPasswordInput.getText().toString();
                final String passwordConfirm = etConfirmPasswordInput.getText().toString();

                // Check that password and password confirmation inputs are the same
                if (password.compareTo(passwordConfirm) == 0) {
                    signUp(email, username, password);
                } else {
                    new AlertDialog.Builder(SignupActivity.this)
                            .setTitle("Passwords Don't Match")
                            .setMessage("The passwords you entered don't match. Please re-enter and try again.")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                }
                            })

                            .setIcon(R.drawable.ic_alert)
                            .show();
                }
            }
        });
    }

    // Sign up the user with the given username, email, and password.
    private void signUp(String email, String username, String password) {

        // Create the ParseUser
        ParseUser user = new ParseUser();

        // Set user properties
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);

        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    Log.d("SignupActivity", "Sign up successful!");
                    // Launch MainActivity (and finish Login and Signup activities)
                    final Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                    startActivity(intent);
                    LoginActivity.getLoginActivty().finish();
                    finish();
                } else {
                    Log.e("SignupActivity", "Sign up failed.");
                    e.printStackTrace();
                }
            }
        });
    }

    // Wait till text is entered into username/password inputs to enable sign up button
    private final TextWatcher signupAvailable = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (etEmailInput.getText().toString().trim().length() > 0 &&
                    etUsernameInput.getText().toString().trim().length() > 0 &&
                    etPasswordInput.getText().toString().trim().length() > 0 &&
                    etConfirmPasswordInput.getText().toString().trim().length() > 0) {
                btnSignIn.setEnabled(true);
                btnSignIn.setBackground(getDrawable(R.drawable.btn_bg));
                btnSignIn.setAlpha((float ) 1);
                btnSignIn.setTextColor(getColor(R.color.login_form_details));
            } else {
                btnSignIn.setEnabled(false);
                btnSignIn.setBackground(getDrawable(R.drawable.btn_bg));
                btnSignIn.setAlpha((float ) 0.5);
                btnSignIn.setTextColor(getColor(R.color.login_form_details_medium));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
