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
    private EditText usernameInput;
    private EditText passwordInput;
    private TextView signupText;
    private Button loginBtn;

    private static Activity loginActivty;

    private Boolean passwordShown;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginActivty = this;

        // Create animated gradient background
        ConstraintLayout constraintLayout = findViewById(R.id.loginBackground);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        // Find references for the views
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        signupText =findViewById(R.id.signupText);
        loginBtn = findViewById(R.id.signupBtn);

        // Set up listener for username/password input
        usernameInput.addTextChangedListener(loginAvailable);
        passwordInput.addTextChangedListener(loginAvailable);

        // Set up listener for login button
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = usernameInput.getText().toString();
                final String password = passwordInput.getText().toString();

                login(username, password);
            }
        });

        // Initially disable button
        loginBtn.setEnabled(false);
        loginBtn.setAlpha((float ) 0.5);

        // Default password not shown
        passwordShown = false;

        // Set up listener for password input
        passwordInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (passwordInput.getRight() - passwordInput.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (passwordShown) {
                            passwordShown = false;
                            passwordInput.setInputType(129);
                            passwordInput.setTypeface(usernameInput.getTypeface());
                        } else {
                            passwordShown = true;
                            passwordInput.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        // Set up listener for sign up
        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
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
            if (usernameInput.getText().toString().trim().length() > 0 &&
                    passwordInput.getText().toString().trim().length() > 0) {
                loginBtn.setEnabled(true);
                loginBtn.setBackground(getDrawable(R.drawable.btn_bg));
                loginBtn.setAlpha((float ) 1);
                loginBtn.setTextColor(getColor(R.color.login_form_details));
            } else {
                loginBtn.setEnabled(false);
                loginBtn.setBackground(getDrawable(R.drawable.btn_bg));
                loginBtn.setAlpha((float ) 0.5);
                loginBtn.setTextColor(getColor(R.color.login_form_details_medium));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public static Activity getLoginActivty() {
        return loginActivty;
    }
}

