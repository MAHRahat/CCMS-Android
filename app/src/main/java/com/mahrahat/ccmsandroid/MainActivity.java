package com.mahrahat.ccmsandroid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


/**
 * Activity where the app starts.
 */

public class MainActivity extends AppCompatActivity {

    private final String TAG = "CCMS Main Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
        String token = dbHelper.readValueOfKee("Token");
        if (token != null) {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
            finishAffinity();
        }
        setContentView(R.layout.activity_main);
        setUpRegisterButton();
        setUpLoginButton();
    }

    private void setUpRegisterButton() {
        Button registerButton = findViewById(R.id.btn_act_register);
        registerButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RegistrationActivity.class)));
    }

    private void setUpLoginButton() {
        Button loginButton = findViewById(R.id.btn_act_login);
        loginButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));
    }
}
