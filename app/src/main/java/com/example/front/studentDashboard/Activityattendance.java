package com.example.front.studentDashboard;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.front.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Activityattendance extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteTextViewStudentID;
    private LinearLayout attendanceTable;
    private TextView textViewOverallAttendance;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_attendance);

        autoCompleteTextViewStudentID = findViewById(R.id.autoCompleteTextViewStudentID);
        attendanceTable = findViewById(R.id.attendanceTable);
        textViewOverallAttendance = findViewById(R.id.textViewOverallAttendance);

        db = FirebaseFirestore.getInstance();

        // Fetch student IDs for AutoCompleteTextView
        fetchStudentIDs();

        // Set onClickListener for the search button
        findViewById(R.id.btnSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String studentID = autoCompleteTextViewStudentID.getText().toString().trim();
                if (!studentID.isEmpty()) {
                    fetchAttendanceData(studentID);
                } else {
                    Toast.makeText(Activityattendance.this, "Please enter a student ID", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchStudentIDs() {
        db.collection("attendance")
                .get()
                .addOnCompleteListener(new OnCompleteListener<com.google.firebase.firestore.QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.firebase.firestore.QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> studentIDs = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                String studentID = document.getId();
                                studentIDs.add(studentID);
                            }
                            // Set up ArrayAdapter for AutoCompleteTextView
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(Activityattendance.this,
                                    android.R.layout.simple_dropdown_item_1line, studentIDs);
                            autoCompleteTextViewStudentID.setAdapter(adapter);
                        } else {
                            Toast.makeText(Activityattendance.this, "Failed to fetch student IDs", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void fetchAttendanceData(final String studentID) {
        db.collection("attendance").document(studentID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                // Fetch attendance data for each month
                                Map<String, Object> attendanceMap = document.getData();
                                if (attendanceMap != null) {
                                    displayAttendanceData(studentID, attendanceMap);
                                } else {
                                    Toast.makeText(Activityattendance.this, "No attendance data found", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(Activityattendance.this, "No document found for student ID: " + studentID, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Activityattendance.this, "Failed to fetch attendance data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void displayAttendanceData(final String studentID, Map<String, Object> attendanceMap) {
        attendanceTable.removeAllViews();

        // Fetch total days for each month from the "totaldays" collection
        db.collection("totaldays").document("month")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                int totalDaysMonth1 = document.getLong("month1").intValue();
                                int totalDaysMonth2 = document.getLong("month2").intValue();
                                int totalDaysMonth3 = document.getLong("month3").intValue();

                                int totalAttendance = 0;
                                int totalDays = 0;

                                // Display attendance data for each month
                                for (Map.Entry<String, Object> entry : attendanceMap.entrySet()) {
                                    final String month = entry.getKey();
                                    int monthTotalDays;
                                    if (month.equals("month1")) {
                                        monthTotalDays = totalDaysMonth1;
                                    } else if (month.equals("month2")) {
                                        monthTotalDays = totalDaysMonth2;
                                    } else if (month.equals("month3")) {
                                        monthTotalDays = totalDaysMonth3;
                                    } else {
                                        // Handle unexpected month name
                                        continue;
                                    }

                                    totalAttendance += ((Long) entry.getValue()).intValue();
                                    totalDays += monthTotalDays;

                                    // Calculate attendance percentage for the month
                                    int attendance = ((Long) entry.getValue()).intValue(); // Assuming attendance is stored as integer
                                    double attendancePercentage = (attendance / (double) monthTotalDays) * 100;

                                    // Create a new row in the table
                                    LinearLayout row = new LinearLayout(Activityattendance.this);
                                    row.setLayoutParams(new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT));

                                    // Month TextView
                                    TextView textViewMonth = new TextView(Activityattendance.this);
                                    textViewMonth.setLayoutParams(new LinearLayout.LayoutParams(
                                            0,
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            1));
                                    textViewMonth.setText(month);
                                    row.addView(textViewMonth);

                                    // Attendance TextView
                                    TextView textViewAttendance = new TextView(Activityattendance.this);
                                    textViewAttendance.setLayoutParams(new LinearLayout.LayoutParams(
                                            0,
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            1));
                                    textViewAttendance.setText(String.valueOf(attendance));
                                    row.addView(textViewAttendance);

                                    // Total Days TextView
                                    TextView textViewTotalDays = new TextView(Activityattendance.this);
                                    textViewTotalDays.setLayoutParams(new LinearLayout.LayoutParams(
                                            0,
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            1));
                                    textViewTotalDays.setText(String.valueOf(monthTotalDays));
                                    row.addView(textViewTotalDays);

                                    // Percentage TextView
                                    TextView textViewPercentage = new TextView(Activityattendance.this);
                                    textViewPercentage.setLayoutParams(new LinearLayout.LayoutParams(
                                            0,
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            1));
                                    textViewPercentage.setText(String.format("%.2f", attendancePercentage) + "%");
                                    row.addView(textViewPercentage);

                                    // Add row to the table
                                    attendanceTable.addView(row);
                                }

                                // Calculate overall attendance percentage
                                double overallAttendancePercentage = (totalAttendance / (double) totalDays) * 100;
                                textViewOverallAttendance.setText("Overall Attendance: " + String.format("%.2f", overallAttendancePercentage) + "%");

                            } else {
                                Toast.makeText(Activityattendance.this, "No document found for total days", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Activityattendance.this, "Failed to fetch total days data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}