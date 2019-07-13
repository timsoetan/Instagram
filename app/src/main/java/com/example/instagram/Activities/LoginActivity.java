package com.example.instagram.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.instagram.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    // Login info
    private EditText etUsernameInput;
    private EditText etPasswordInput;
    private TextView tvSignUp;
    private Button btnLogIn;

    private static Activity loginActivty;

    private Boolean passwordShown;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginActivty = this;

        // Check if a user is currently logged in
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser != null) {
            // Send user to home page
            launchHomeActivity();
        } else {

            // Create animated gradient background
            ConstraintLayout constraintLayout = findViewById(R.id.loginBackground);
            AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
            animationDrawable.setEnterFadeDuration(2000);
            animationDrawable.setExitFadeDuration(4000);
            animationDrawable.start();

            // Find references for the views
            etUsernameInput = findViewById(R.id.etUsernameInput);
            etPasswordInput = findViewById(R.id.etPasswordInput);
            tvSignUp = findViewById(R.id.tvSignUp);
            btnLogIn = findViewById(R.id.btnLogIn);

            // Set up listener for username/password input
            etUsernameInput.addTextChangedListener(loginAvailable);
            etPasswordInput.addTextChangedListener(loginAvailable);

            // Set up listener for login button
            btnLogIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String username = etUsernameInput.getText().toString();
                    final String password = etPasswordInput.getText().toString();

                    login(username, password);
                }
            });

            // Initially disable button
            btnLogIn.setEnabled(false);
            btnLogIn.setAlpha((float) 0.5);

            // Default password not shown
            passwordShown = false;

            // Set up listener for password input
            etPasswordInput.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final int DRAWABLE_RIGHT = 2;

                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (event.getRawX() >= (etPasswordInput.getRight() - etPasswordInput.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            if (passwordShown) {
                                passwordShown = false;
                                etPasswordInput.setInputType(129);
                                etPasswordInput.setTypeface(etUsernameInput.getTypeface());
                            } else {
                                passwordShown = true;
                                etPasswordInput.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            }
                            return true;
                        }
                    }
                    return false;
                }
            });

            // Set up listener for sign up
            tvSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    private void login(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Log.d("LoginActivity", "Login successful");

                    final Intent intent = new Intent(LoginActivity.this,
                            HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("LoginActivity", "Login failure");
                    e.printStackTrace();
                }
            }
        });
    }

    // Wait till text is entered into username/password inputs to enable login button
    private final TextWatcher loginAvailable = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (etUsernameInput.getText().toString().trim().length() > 0 &&
                    etPasswordInput.getText().toString().trim().length() > 0) {
                btnLogIn.setEnabled(true);
                btnLogIn.setBackground(getDrawable(R.drawable.btn_bg));
                btnLogIn.setAlpha((float) 1);
                btnLogIn.setTextColor(getColor(R.color.login_form_details));
            } else {
                btnLogIn.setEnabled(false);
                btnLogIn.setBackground(getDrawable(R.drawable.btn_bg));
                btnLogIn.setAlpha((float) 0.5);
                btnLogIn.setTextColor(getColor(R.color.login_form_details_medium));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public static Activity getLoginActivty() {
        return loginActivty;
    }

    // Launch HomeActivity.
    private void launchHomeActivity() {
        final Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}

