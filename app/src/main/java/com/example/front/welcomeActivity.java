package com.example.front;

import android.Manifest;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class welcomeActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 100;
    private static final int NOTIFICATION_PERMISSION_CODE = 101;
    private static final String TAG = "PERMISSION_TAG";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Check permissions before proceeding
        if (!checkPermission()) {
            // Permissions not granted, request them
            requestPermission();
        }

        // Initialize UI components and set click listeners
        initializeUI();
    }

    private void initializeUI() {
        Button student = findViewById(R.id.stubutton);
        Button faculty = findViewById(R.id.facbutton);
        TextView login = findViewById(R.id.login);

        // Set click listeners for buttons
        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(welcomeActivity.this, stuSignupActivity.class);
                startActivity(intent);
            }
        });

        faculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(welcomeActivity.this, facSignupActivity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(welcomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void requestPermission() {
        if (checkPermission()) {
            // Permissions already granted, no need to request again
            return;
        }

        // Request storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                storageActivityResultLauncher.launch(intent);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                storageActivityResultLauncher.launch(intent);
            }
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE
            );
        }

        // Request notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
            notificationActivityResultLauncher.launch(intent);
        }
    }

    private ActivityResultLauncher<Intent> storageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        if (Environment.isExternalStorageManager()) {
                            // Storage permission granted
                        } else {
                            // Storage permission denied
                        }
                    } else {
                        // Handle result for versions prior to Android 11
                    }
                }
            }
    );

    private ActivityResultLauncher<Intent> notificationActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // Handle result for notification permission
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (NotificationManagerCompat.from(getApplicationContext()).areNotificationsEnabled()) {
                            // Notification permission granted
                        } else {
                            // Notification permission denied
                        }
                    }
                }
            }
    );

    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 and above
            return Environment.isExternalStorageManager() && NotificationManagerCompat.from(getApplicationContext()).areNotificationsEnabled();
        } else {
            // For Android versions prior to Android 11
            boolean writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            boolean readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            return writePermission && readPermission;
        }
    }
}