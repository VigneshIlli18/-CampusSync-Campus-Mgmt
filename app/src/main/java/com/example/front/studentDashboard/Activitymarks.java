package com.example.front.studentDashboard;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.front.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class Activitymarks extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteTextViewPIN;
    private TableLayout tableLayoutMarks;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_marks);

        autoCompleteTextViewPIN = findViewById(R.id.autoCompleteTextViewPIN);
        tableLayoutMarks = findViewById(R.id.tableLayoutMarks);

        db = FirebaseFirestore.getInstance();

        // Fetch student PIN numbers for AutoCompleteTextView
        fetchPins();

        // Set onClickListener for the search button
        findViewById(R.id.btnSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pin = autoCompleteTextViewPIN.getText().toString().trim();
                if (!pin.isEmpty()) {
                    fetchMarks(pin);
                } else {
                    Toast.makeText(Activitymarks.this, "Please enter a PIN number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchPins() {
        db.collection("marks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<com.google.firebase.firestore.QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.firebase.firestore.QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Create a list to store PIN numbers
                            final ArrayAdapter<String> adapter = new ArrayAdapter<>(Activitymarks.this,
                                    android.R.layout.simple_dropdown_item_1line);

                            for (DocumentSnapshot document : task.getResult()) {
                                String pin = document.getId();
                                adapter.add(pin);
                            }
                            autoCompleteTextViewPIN.setAdapter(adapter);
                        } else {
                            Toast.makeText(Activitymarks.this, "Failed to fetch PIN numbers", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void fetchMarks(String pin) {
        db.collection("marks").document(pin)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                displayMarks(document.getData());
                            } else {
                                Toast.makeText(Activitymarks.this, "No document found for PIN: " + pin, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Activitymarks.this, "Failed to fetch marks", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void displayMarks(Map<String, Object> marksMap) {
        // Clear existing rows
        tableLayoutMarks.removeAllViews();

        // Iterate over subjects and display marks
        for (Map.Entry<String, Object> entry : marksMap.entrySet()) {
            String subject = entry.getKey();
            Map<String, Object> subjectMarks = (Map<String, Object>) entry.getValue();

            // Create a new row for each subject
            TableRow row = new TableRow(this);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            row.setLayoutParams(layoutParams);

            // Add subject name to the row
            TextView tvSubject = new TextView(this);
            tvSubject.setText(subject);
            tvSubject.setPadding(8, 8, 8, 8);
            row.addView(tvSubject);

            // Calculate overall marks for the subject
            int assignment1 = getInteger(subjectMarks.get("assignment1"));
            int assignment2 = getInteger(subjectMarks.get("assignment2"));
            int mid1 = getInteger(subjectMarks.get("mid1"));
            int mid2 = getInteger(subjectMarks.get("mid2"));

            // Calculate overall marks
            int overallMarks = (int) ((Math.max(mid1, mid2) * 0.75) + (Math.min(mid1, mid2) * 0.25)
                    + ((assignment1 + assignment2) / 2));

            // Add marks for each component to the row
            addTextViewToRow(row, String.valueOf(assignment1));
            addTextViewToRow(row, String.valueOf(assignment2));
            addTextViewToRow(row, String.valueOf(mid1));
            addTextViewToRow(row, String.valueOf(mid2));
            addTextViewToRow(row, String.valueOf(overallMarks));

            // Add row to the table layout
            tableLayoutMarks.addView(row);
        }
    }

    // Helper method to add a TextView to a TableRow
    private void addTextViewToRow(TableRow row, String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        row.addView(textView);
    }

    // Helper method to safely get an Integer value from an Object
    private int getInteger(Object obj) {
        if (obj instanceof Long) {
            return ((Long) obj).intValue();
        } else if (obj instanceof Integer) {
            return (int) obj;
        } else {
            return 0; // Default value if object is not Long or Integer
        }
    }
}