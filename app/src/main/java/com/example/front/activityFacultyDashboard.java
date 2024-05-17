package com.example.front;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.front.facultyDashboard.ActivityFacAttendance;
import com.example.front.facultyDashboard.ActivityFacNotice;
import com.example.front.facultyDashboard.ActivityFacUploadNotes;
import com.example.front.facultyDashboard.ActivityFacUploadPrevPapers;
import com.example.front.facultyDashboard.ActivityFacUploadSyllabus;
import com.example.front.facultyDashboard.ActivityFacUploadTimetable;
import com.example.front.facultyDashboard.ActivityFacUserProfile;
import com.example.front.facultyDashboard.Activity_fac_events;
import com.example.front.facultyDashboard.Activity_marks;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


public class activityFacultyDashboard extends AppCompatActivity {
    private String userEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fac_dashboard);

        // Find the ImageView and TextView for the user's name and image
        ImageView userImageView = findViewById(R.id.facimage);
        TextView userNameTextView = findViewById(R.id.facname);

        // Get the user's email from intent
        userEmail = getIntent().getStringExtra("userEmail");

        // Initialize Firebase components
        FirebaseAuth fauth = FirebaseAuth.getInstance();
        FirebaseFirestore fstore = FirebaseFirestore.getInstance();
        FirebaseStorage fstorage = FirebaseStorage.getInstance();
        StorageReference storageReference = fstorage.getReference();

        // Get the current user's ID
        String userid = fauth.getCurrentUser().getUid();

        // Reference to the document in Firestore containing user details
        DocumentReference documentReference = fstore.collection("faculty").document(userid);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                // Set the user's name
                userNameTextView.setText(value.getString("name"));

                // Get user's email from Firestore
                String userEmailFirestore = value.getString("email");

                // Load profile image from Firebase Storage
                StorageReference profileImageRef = storageReference.child("profile_pics/" + userEmailFirestore + ".jpg");
                profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Load profile image using Picasso
                        Picasso.get()
                                .load(uri)
                                .placeholder(R.drawable.person_svgrepo_com)
                                .error(R.drawable.person_svgrepo_com)
                                .into(userImageView);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure to load profile image
                        // You can set a default image or show an error message here
                    }
                });
            }
        });

        // Set OnClickListener for user profile card
        CardView userProfileCard = findViewById(R.id.userProfileCard);
        userProfileCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activityFacultyDashboard.this, ActivityFacUserProfile.class);
                intent.putExtra("userEmail", userEmail);
                startActivity(intent);
            }
        });

        // Set OnClickListener for eventsCard
        CardView eventsCard = findViewById(R.id.eventsCard);
        eventsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activityFacultyDashboard.this, Activity_fac_events.class);
                startActivity(intent);
            }
        });

        // Set OnClickListener for notesCard
        CardView notesCard = findViewById(R.id.notesCard);
        notesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activityFacultyDashboard.this, ActivityFacUploadNotes.class);
                startActivity(intent);
            }
        });

        // Set OnClickListener for marksCard
        CardView marksCard = findViewById(R.id.marksCard);
        marksCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activityFacultyDashboard.this, Activity_marks.class);
                startActivity(intent);
            }
        });

        // Set OnClickListener for timetableCard
        CardView timetableCard = findViewById(R.id.timetableCard);
        timetableCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activityFacultyDashboard.this, ActivityFacUploadTimetable.class);
                startActivity(intent);
            }
        });

        // Set OnClickListener for syllabusCard
        CardView syllabusCard = findViewById(R.id.syllabusCard);
        syllabusCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activityFacultyDashboard.this, ActivityFacUploadSyllabus.class);
                startActivity(intent);
            }
        });

        // Set OnClickListener for prevPapersCard
        CardView prevPapersCard = findViewById(R.id.prevpapersCard);
        prevPapersCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activityFacultyDashboard.this, ActivityFacUploadPrevPapers.class);
                startActivity(intent);
            }
        });

        // Set OnClickListener for qACard
        CardView qACard = findViewById(R.id.qaCard);
        qACard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activityFacultyDashboard.this, ActivityFacNotice.class);
                startActivity(intent);
            }
        });

        // Set OnClickListener for attendanceCard
        CardView attendanceCard = findViewById(R.id.attendanceCard);
        attendanceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activityFacultyDashboard.this, ActivityFacAttendance.class);
                startActivity(intent);
            }
        });
    }
}