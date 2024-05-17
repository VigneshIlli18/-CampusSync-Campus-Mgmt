package com.example.front;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USER_TYPE = "userType";
    private static final int FLASH_SCREEN_DURATION = 400; // in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        setContentView(R.layout.activity_main);

        new android.os.Handler().postDelayed(
                () -> {
                    String savedEmail = sharedPreferences.getString(KEY_EMAIL, null);
                    String savedUserType = sharedPreferences.getString(KEY_USER_TYPE, null);

                    if (savedEmail != null && savedUserType != null) {
                        navigateToDashboard(savedUserType);
                    } else {
                        navigateToWelcome();
                    }
                },
                FLASH_SCREEN_DURATION
        );
    }

    private void navigateToWelcome() {
        Intent intent = new Intent(MainActivity.this, welcomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToDashboard(String userType) {
        // Navigate to dashboard based on user type
        Intent intent;
        switch (userType) {
            case "student":
                intent = new Intent(MainActivity.this, activityStudentDashboard.class);
                break;
            case "faculty":
                intent = new Intent(MainActivity.this, activityFacultyDashboard.class);
                break;
            default:
                intent = new Intent(MainActivity.this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
