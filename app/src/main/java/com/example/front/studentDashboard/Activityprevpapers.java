package com.example.front.studentDashboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.front.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.ListResult;
import java.util.ArrayList;
import java.util.List;

public class Activityprevpapers extends AppCompatActivity {



    private AutoCompleteTextView prevpAutoCompleteTextView;
    private Button viewprevpButton;
    private FirebaseStorage storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_prevpapers);

        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance();

        // Find views
        prevpAutoCompleteTextView = findViewById(R.id.PrevpAutoCompleteTextView);
        viewprevpButton = findViewById(R.id.viewPrevp);

        // Initialize ArrayAdapter for subject suggestions
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.Subject));
        prevpAutoCompleteTextView.setAdapter(adapter);

        // Button click listener
        viewprevpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedSubject = prevpAutoCompleteTextView.getText().toString();
                if (!selectedSubject.isEmpty()) {
                    // Call method to navigate to view_notes activity and pass subject name
                    navigateToViewNotes(selectedSubject);
                } else {
                    Toast.makeText(Activityprevpapers.this, "Please enter a subject name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Method to navigate to view_notes activity and pass subject name
    private void navigateToViewNotes(String subject) {
        Intent intent = new Intent(Activityprevpapers.this, ActivityViewPrevPapers.class);
        intent.putExtra("subject", subject);
        startActivity(intent);
    }
}
