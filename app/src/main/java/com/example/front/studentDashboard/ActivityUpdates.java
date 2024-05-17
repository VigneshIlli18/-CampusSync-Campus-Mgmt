package com.example.front.studentDashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.front.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ActivityUpdates extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_updates);

        db = FirebaseFirestore.getInstance();

        // Get reference to the container layout
        LinearLayout containerLayout = findViewById(R.id.containerLayout);

        // Fetch notices from Firestore
        fetchNoticesFromFirestore(containerLayout);
    }

    // Method to fetch notices from Firebase Firestore
    private void fetchNoticesFromFirestore(final LinearLayout containerLayout) {
        db.collection("notices").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        String department = document.getString("department");
                        String date = document.getString("date");
                        String time = document.getString("time");
                        String title = document.getString("title");
                        String description = document.getString("description");

                        // Create notice card view
                        View noticeCardView = createNoticeCard(department, date, time, title, description);
                        // Add notice card view to container layout
                        containerLayout.addView(noticeCardView);
                    }
                } else {
                    // Handle errors
                    Toast.makeText(ActivityUpdates.this, "Failed to fetch notices", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Method to create a notice card dynamically
    private View createNoticeCard(String department, String date, String time, String title, String description) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View noticeCardView = inflater.inflate(R.layout.activity_updates_card, null);

        // Set notice details
        TextView departmentTextView = noticeCardView.findViewById(R.id.noticeDepartmentTextView);
        TextView dateTextView = noticeCardView.findViewById(R.id.noticeDateTextView);
        TextView timeTextView = noticeCardView.findViewById(R.id.noticeTimeTextView);
        TextView titleTextView = noticeCardView.findViewById(R.id.noticeTitleTextView);
        TextView descriptionTextView = noticeCardView.findViewById(R.id.noticeDescriptionTextView);

        departmentTextView.setText("department: " + department);
        dateTextView.setText("date: " + date);
        timeTextView.setText("time: " + time);
        titleTextView.setText(title);
        descriptionTextView.setText(description);

        return noticeCardView;
    }
}