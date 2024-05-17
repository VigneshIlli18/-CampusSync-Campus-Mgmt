package com.example.front.facultyDashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.front.R;
import com.example.front.facultyDashboard.ActivityFacManageEvents;
import com.example.front.facultyDashboard.ActivityFacUploadEvents;

public class Activity_fac_events extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fac_man_upl_events);

        // Find buttons by their IDs
        Button buttonCreateNewEvent = findViewById(R.id.buttonCreateNewEvent);
        Button buttonAddExistingEvent = findViewById(R.id.buttonAddExistingEvent);

        // Set onClick listeners for the buttons
        buttonCreateNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to CreateNewEventActivity
                Intent intent = new Intent(Activity_fac_events.this, ActivityFacUploadEvents.class);
                startActivity(intent);
            }
        });

        buttonAddExistingEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to AddExistingEventActivity
                Intent intent = new Intent(Activity_fac_events.this, ActivityFacManageEvents.class);
                startActivity(intent);
            }
        });
    }
}