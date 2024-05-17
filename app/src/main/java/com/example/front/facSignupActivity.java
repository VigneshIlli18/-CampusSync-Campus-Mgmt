package com.example.front;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;


public class facSignupActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private EditText nameedittext;
    private EditText emailedittext;
    private EditText passcodeedittext;
    private EditText passwordedittext;
    private Button signupbutton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_signup);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        nameedittext = findViewById(R.id.signup_name);
        passcodeedittext=findViewById(R.id.passcode_signup);
        emailedittext = findViewById(R.id.signup_email);
        passwordedittext = findViewById(R.id.signup_password);
        signupbutton = findViewById(R.id.signup_button);

        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
    }

    private void signup(){
        String name = nameedittext.getText().toString().trim();
        String email = emailedittext.getText().toString().trim();
        String passcode = passcodeedittext.getText().toString().trim();
        String password = passwordedittext.getText().toString().trim();

        if (!validateInput()){
            return;
        }
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if(user!= null){
                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    showEmailverificationSnackbar();
                                                    addUserDetailsToFirestore(email,name,password);
                                                }
                                                else{
                                                    Toast.makeText(facSignupActivity.this, "Failed to send verification email", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(facSignupActivity.this,"Signup Failed" ,Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }


    private boolean validateInput() {
        String name = nameedittext.getText().toString().trim();
        String email = emailedittext.getText().toString().trim();
        String password = passwordedittext.getText().toString().trim();
        String passcode = passcodeedittext.getText().toString().trim();

        // Validate name
        if (TextUtils.isEmpty(name)) {
            nameedittext.setError("Name is required");
            return false;
        } else if (!name.matches("[a-zA-Z]+")) {
            nameedittext.setError("Name should contain only alphabets");
            return false;
        }

        // Validate email
        if (TextUtils.isEmpty(email)) {
            emailedittext.setError("Email is required");
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailedittext.setError("Enter a valid email address");
            return false;
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            passwordedittext.setError("Password is required");
            return false;
        } else if (password.length() < 6 || password.length() > 12) {
            passwordedittext.setError("Password must be between 6 and 12 characters");
            return false;
        }

        // Validate passcode
        if (TextUtils.isEmpty(passcode)) {
            passcodeedittext.setError("Passcode is required");
            return false;
        } else if (!passcode.equals("vignesh18")) {
            passcodeedittext.setError("Incorrect passcode");
            return false;
        }
        return true;
    }

    private void addUserDetailsToFirestore(String email, String name, String password) {
        String userId = firebaseAuth.getCurrentUser().getUid();
        Map<String , Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("password",password);
        userData.put("email", email);

        firestore.collection("faculty")
                .document(userId)
                .set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // User data stored successfully
                        startActivity(new Intent(facSignupActivity.this, LoginActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to store user data
                        Toast.makeText(facSignupActivity.this, "Failed to store user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void showEmailverificationSnackbar(){
        Snackbar.make(findViewById(android.R.id.content),"Verification email sent. Please verify your email address.", Snackbar.LENGTH_LONG).show();
    }


}
