package com.example.front.studentDashboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.front.LoginActivity;
import com.example.front.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class Activityuserprofile extends AppCompatActivity {
    FirebaseAuth fauth;
    FirebaseFirestore fstore;
    FirebaseStorage fstorage;
    StorageReference storageReference;
    Button Logout;
    String userid;
    ImageView profileImg;
    TextView profileName, profileEmail, regdt;
    Uri selectedImageUri; // Declare a global variable to store the selected image URI
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        profileImg = findViewById(R.id.profileImg);
        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        regdt = findViewById(R.id.regdt);
        Logout = findViewById(R.id.logoutbutton); // Initialize logout button

        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        fstorage = FirebaseStorage.getInstance();
        storageReference = fstorage.getReference();

        userid = fauth.getCurrentUser().getUid();

        DocumentReference documentReference = fstore.collection("student").document(userid);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                profileName.setText(value.getString("name"));
                profileEmail.setText(value.getString("email"));
                regdt.setText(value.getString("regd"));

                // Load profile image from Firebase Storage directly
                String userEmail = value.getString("email");
                StorageReference profileImageRef = storageReference.child("profile_pics/" + userEmail + ".jpg");
                profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Picasso.get().load(uri)
                            .fit()
                            .into(profileImg);
                }).addOnFailureListener(exception -> {
                    // Handle error loading profile image
                    profileImg.setImageResource(R.drawable.person_svgrepo_com); // Set default profile picture
                });
            }
        });

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open an image picker dialog or activity
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        // Logout button click listener
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    // Function to logout user
    private void logoutUser() {
        // Logout the user
        fauth.signOut();

        // Navigate to the login screen
        Intent intent = new Intent(Activityuserprofile.this, LoginActivity.class);
        getSharedPreferences("LoginPrefs",MODE_PRIVATE).edit().clear().apply();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            // Get the user's email
            String userEmail = profileEmail.getText().toString();

            // Prompt user for confirmation before overwriting existing profile picture
            new AlertDialog.Builder(this)
                    .setTitle("Replace Profile Picture")
                    .setMessage("Are you sure you want to replace your current profile picture?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        // Create a reference to the storage location where the image is saved
                        StorageReference profileImageRef = storageReference.child("profile_pics/" + userEmail + ".jpg");

                        // Upload the selected image to Firebase Storage, overwriting the existing image
                        profileImageRef.putFile(selectedImageUri)
                                .addOnSuccessListener(taskSnapshot -> {
                                    // Handle successful upload
                                    // Load the new image into the ImageView as the new profile picture
                                    Picasso.get()
                                            .load(selectedImageUri)
                                            .fit()
                                            .into(profileImg);
                                })
                                .addOnFailureListener(e -> {
                                    // Handle upload failure
                                });
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
}