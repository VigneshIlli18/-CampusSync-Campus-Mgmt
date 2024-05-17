package com.example.front.facultyDashboard;// UploadNoticeActivity.java

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.front.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ActivityFacNotice extends AppCompatActivity {

    private EditText noticeDepartmentEditText, noticeTimeEditText, noticeDateEditText, noticeTitleEditText, noticeDescriptionEditText;
    private Button uploadNoticeButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fac_notice);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        noticeDepartmentEditText = findViewById(R.id.noticeDepartmentEditText);
        noticeTimeEditText = findViewById(R.id.noticeTimeEditText);
        noticeDateEditText = findViewById(R.id.noticeDateEditText);
        noticeTitleEditText = findViewById(R.id.noticeTitleEditText);
        noticeDescriptionEditText = findViewById(R.id.noticeDescriptionEditText);
        uploadNoticeButton = findViewById(R.id.uploadNoticeButton);

        // Set click listener for upload button
        uploadNoticeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadNotice();
            }
        });
    }

    private void uploadNotice() {
        // Get notice details
        String department = noticeDepartmentEditText.getText().toString().trim();
        String time = noticeTimeEditText.getText().toString().trim();
        String date = noticeDateEditText.getText().toString().trim();
        String title = noticeTitleEditText.getText().toString().trim();
        String description = noticeDescriptionEditText.getText().toString().trim();

        // Check if any field is empty
        if (TextUtils.isEmpty(department) || TextUtils.isEmpty(time) || TextUtils.isEmpty(date) || TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a unique document ID for the notice
        String documentId = db.collection("notices").document().getId();

        // Create a map to store notice details
        Map<String, Object> noticeData = new HashMap<>();
        noticeData.put("department", department);
        noticeData.put("time", time);
        noticeData.put("date", date);
        noticeData.put("title", title);
        noticeData.put("description", description);

        // Upload notice data to Firestore
        db.collection("notices").document(documentId)
                .set(noticeData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ActivityFacNotice.this, "Notice uploaded successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Finish activity after uploading notice
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ActivityFacNotice.this, "Failed to upload notice", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}