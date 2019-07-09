package com.example.instagram;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    // Login info
    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);


            // Create animated gradient background
            ConstraintLayout constraintLayout = findViewById(R.id.loginBackground);
            AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
            animationDrawable.setEnterFadeDuration(2000);
            animationDrawable.setExitFadeDuration(4000);
            animationDrawable.start();

            usernameInput = findViewById(R.id.usernameInput);
            passwordInput = findViewById(R.id.passwordInput);
            loginBtn = findViewById(R.id.loginBtn);

            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String username = usernameInput.getText().toString();
                    final String password = passwordInput.getText().toString();

                    login(username, password);
                }
            });
        }

        private void login(String username, String password) {
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e != null) {
                        Log.d("LoginActivity", "Login successful");
                    } else {
                        Log.e("LoginActivity", "Login failure");
                        e.printStackTrace();
                    }
                }
            });
        }

    }

