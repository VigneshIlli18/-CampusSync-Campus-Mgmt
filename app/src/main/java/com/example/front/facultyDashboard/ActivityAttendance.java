package com.example.front.facultyDashboard;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.front.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ActivityAttendance extends AppCompatActivity {

    private String selectedField;
    private boolean totalDaysUploaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fac_attendance);

        // Get the selected field name from the previous activity
        selectedField = getIntent().getStringExtra("selectedField");

        // Query Firestore to get the list of student IDs
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("marks").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String studentId = document.getId();
                                View entryView = getLayoutInflater().inflate(R.layout.attendance_item, null);
                                TextView textViewRegNum = entryView.findViewById(R.id.textViewRegNum);
                                textViewRegNum.setText("Regd Num: " + studentId);
                                LinearLayout layoutContainer = findViewById(R.id.layoutContainer);
                                layoutContainer.addView(entryView);
                            }
                        } else {
                            showToast("Error getting documents");
                        }
                    }
                });

        // Initialize and set onclick event for the submit button
        findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAttendance();
            }
        });
    }

    // Method to submit attendance
    private void submitAttendance() {
        // Check if total days college run is already uploaded
        if (!totalDaysUploaded) {
            EditText editTextTotalDaysCollege = findViewById(R.id.editTextTotalDaysCollege);
            String totalDaysCollegeStr = editTextTotalDaysCollege.getText().toString().trim();
            if (totalDaysCollegeStr.isEmpty()) {
                showToast("Please enter total days college run");
                return;
            }

            int totalDaysCollege;
            try {
                totalDaysCollege = Integer.parseInt(totalDaysCollegeStr);
            } catch (NumberFormatException e) {
                showToast("Please enter a valid number for total days college run");
                return;
            }

            uploadTotalDaysCollege(totalDaysCollege);
        }

        // Submit attendance
        LinearLayout layout = findViewById(R.id.layoutContainer);
        int childCount = layout.getChildCount();

        for (int i = 0; i < childCount; i++) {
            LinearLayout childLayout = (LinearLayout) layout.getChildAt(i);
            EditText editText = childLayout.findViewById(R.id.editTextDaysAttended);
            int daysAttended = 0;
            try {
                daysAttended = Integer.parseInt(editText.getText().toString().trim());

                // Update Firestore with entered attendance
                String studentID = ((TextView) childLayout.findViewById(R.id.textViewRegNum)).getText().toString().replace("Regd Num: ", "");
                updateAttendance(studentID, daysAttended);
                showToast("Attendance for student " + studentID + " submitted successfully!");
            } catch (NumberFormatException e) {
                showToast("Please enter a valid attendance for student " + (i + 1));
                return; // Exit the method if any attendance is invalid
            }
        }

        // Mark total days college run as uploaded
        totalDaysUploaded = true;
    }

    // Method to update Firestore with entered attendance
    private void updateAttendance(String studentID, int daysAttended) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference attendanceRef = db.collection("attendance").document(studentID);
        attendanceRef.update(selectedField, daysAttended)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            showToast("Error updating attendance for student: " + studentID);
                        }
                    }
                });
    }

    // Method to upload total days college run to Firestore
    private void uploadTotalDaysCollege(int totalDaysCollege) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference totalDaysRef = db.collection("totaldays").document("month");
        totalDaysRef.update(selectedField, totalDaysCollege)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showToast("Total days college run uploaded successfully for month: " + selectedField);
                        } else {
                            showToast("Error uploading total days college run for month: " + selectedField);
                        }
                    }
                });
    }

    // Method to show Toast message
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}