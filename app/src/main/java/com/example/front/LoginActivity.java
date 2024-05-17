package com.example.front;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin, forgotpassword;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;

    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USER_TYPE = "userType";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editTextEmail = findViewById(R.id.logemail);
        editTextPassword = findViewById(R.id.logpass);
        buttonLogin = findViewById(R.id.logbutton);
        forgotpassword = findViewById(R.id.ForgtPass);
        firebaseAuth = FirebaseAuth.getInstance();

        buttonLogin.setOnClickListener(v -> loginUser());
        forgotpassword.setOnClickListener(v -> sendForgotPasswordEmail());
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            checkUserTypeAndNavigate(user.getEmail());
                        } else {
                            Toast.makeText(LoginActivity.this, "Please verify your email.",
                                    Toast.LENGTH_SHORT).show();
                            firebaseAuth.signOut();
                            clearSharedPreferences(); // Clear shared preferences on logout
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserTypeAndNavigate(String userEmail) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String[] collections = {"student", "faculty", "admin"};

        for (String collection : collections) {
            db.collection(collection)
                    .whereEqualTo("email", userEmail)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // User found in this collection, determine user type
                            String userType = collection;
                            saveLoginData(userEmail, userType);
                            navigateToDashboard(userEmail, userType);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Error retrieving user type
                        Toast.makeText(LoginActivity.this, "Error retrieving user type.",
                                Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void saveLoginData(String email, String userType) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_USER_TYPE, userType);
        editor.apply();
    }

    private void navigateToDashboard(String userEmail, String userType) {
        Intent intent = null;
        switch (userType) {
            case "student":
                intent = new Intent(LoginActivity.this, activityStudentDashboard.class);
                break;
            case "faculty":
                intent = new Intent(LoginActivity.this, activityFacultyDashboard.class);
                break;
        }
        if (intent != null) {
            intent.putExtra("userEmail", userEmail);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    // Method to clear shared preferences data
    private void clearSharedPreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    // Method to send forgot password email
    private void sendForgotPasswordEmail() {
        // Inflate the custom layout for the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_logout_email, null);
        EditText editTextEmailForgot = dialogView.findViewById(R.id.editTextEmail);

        builder.setTitle("Forgot Password")
                .setView(dialogView)
                .setPositiveButton("Send", (dialog, which) -> {
                    String email = editTextEmailForgot.getText().toString().trim();
                    if (!email.isEmpty()) {
                        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Password reset email sent.",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Failed to send password reset email.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(LoginActivity.this, "Please enter your email address.",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}