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
import androidx.appcompat.app.AppCompatActivity;

public class Activitysyllabus extends AppCompatActivity {



    private AutoCompleteTextView syllabusAutoCompleteTextView;
    private Button viewSyllabusButton;
    private FirebaseStorage storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_syllabus);

        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance();

        // Find views
        syllabusAutoCompleteTextView = findViewById(R.id.syllabusAutoCompleteTextView);
        viewSyllabusButton = findViewById(R.id.viewsyllabus);

        // Initialize ArrayAdapter for subject suggestions
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.BranchANDSem));
        syllabusAutoCompleteTextView.setAdapter(adapter);

        // Button click listener
        viewSyllabusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedSubject = syllabusAutoCompleteTextView.getText().toString();
                if (!selectedSubject.isEmpty()) {
                    // Call method to navigate to view_notes activity and pass subject name
                    navigateToViewNotes(selectedSubject);
                } else {
                    Toast.makeText(Activitysyllabus.this, "Please enter a subject name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Method to navigate to view_notes activity and pass subject name
    private void navigateToViewNotes(String subject) {
        Intent intent = new Intent(Activitysyllabus.this, ActivityViewSyllabus.class);
        intent.putExtra("subject", subject);
        startActivity(intent);
    }


}
