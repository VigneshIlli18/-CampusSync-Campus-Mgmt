package com.example.front.facultyDashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Activityentermarks extends AppCompatActivity {

    private String subject;
    private String term;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_marks);

        // Get subject and term values passed from previous activity
        subject = getIntent().getStringExtra("subject");
        term = getIntent().getStringExtra("term");

        // Query Firestore to get the list of student IDs and marks for the selected subject and term
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference marksCollection = db.collection("marks");

        marksCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        // Get student ID
                        String studentID = document.getId();

                        // Inflate EditText layout
                        LinearLayout layout = findViewById(R.id.layoutContainer);
                        LayoutInflater inflater = LayoutInflater.from(Activityentermarks.this);
                        LinearLayout editTextLayout = (LinearLayout) inflater.inflate(R.layout.edittext_item, layout, false);

                        // Set student ID
                        TextView textViewRegNum = editTextLayout.findViewById(R.id.textViewRegNum);
                        textViewRegNum.setText("Regd Num: " + studentID);

                        // Add EditText layout to the container
                        layout.addView(editTextLayout);
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
                submitMarks();
            }
        });
    }

    // Method to submit marks
    private void submitMarks() {
        LinearLayout layout = findViewById(R.id.layoutContainer);
        int childCount = layout.getChildCount();

        for (int i = 0; i < childCount; i++) {
            LinearLayout childLayout = (LinearLayout) layout.getChildAt(i);
            EditText editText = childLayout.findViewById(R.id.editTextItem);
            int mark = 0;
            try {
                mark = Integer.parseInt(editText.getText().toString().trim());

                // Update Firestore with entered marks
                String studentID = ((TextView) childLayout.findViewById(R.id.textViewRegNum)).getText().toString().replace("Regd Num: ", "");
                updateMarks(studentID, mark);
                showToast("Marks for student " + studentID + " submitted successfully!");
            } catch (NumberFormatException e) {
                showToast("Please enter a valid mark for student " + (i + 1));
                return; // Exit the method if any mark is invalid
            }
        }

        showToast("All marks submitted successfully!");
    }

    // Method to update Firestore with entered marks
    private void updateMarks(String studentID, int mark) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference marksCollection = db.collection("marks");
        marksCollection.document(studentID)
                .update(subject + "." + term, mark)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            showToast("Error updating marks for student: " + studentID);
                        }
                    }
                });
    }

    // Method to show Toast message
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}