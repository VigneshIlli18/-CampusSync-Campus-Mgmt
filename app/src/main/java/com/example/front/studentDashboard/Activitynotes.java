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

public class Activitynotes extends AppCompatActivity {

    private AutoCompleteTextView subjectAutoCompleteTextView;
    private Button viewNotesButton;
    private FirebaseStorage storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_notes);

        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance();

        // Find views
        subjectAutoCompleteTextView = findViewById(R.id.subjectAutoCompleteTextView);
        viewNotesButton = findViewById(R.id.viewnotes);

        // Initialize ArrayAdapter for subject suggestions
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.Subject));
        subjectAutoCompleteTextView.setAdapter(adapter);

        // Button click listener
        viewNotesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedSubject = subjectAutoCompleteTextView.getText().toString();
                if (!selectedSubject.isEmpty()) {
                    // Call method to navigate to view_notes activity and pass subject name
                    navigateToViewNotes(selectedSubject);
                } else {
                    Toast.makeText(Activitynotes.this, "Please enter a subject name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Method to navigate to view_notes activity and pass subject name
    private void navigateToViewNotes(String subject) {
        Intent intent = new Intent(Activitynotes.this, ActivityViewNotes.class);
        intent.putExtra("subject", subject);
        startActivity(intent);
    }
}