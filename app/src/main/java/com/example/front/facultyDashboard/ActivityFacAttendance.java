package com.example.front.facultyDashboard;// MainActivity.java

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.front.R;

public class ActivityFacAttendance extends AppCompatActivity {

    private Spinner dropdownAttendanceField;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fac_att_one);

        dropdownAttendanceField = findViewById(R.id.dropdown_attendance_field);
        btnSend = findViewById(R.id.btn_send);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedField = dropdownAttendanceField.getSelectedItem().toString();
                if (selectedField.equals("select month")) {
                    // If "Select the month" is selected, show a toast and return
                    Toast.makeText(ActivityFacAttendance.this, "Please select a month", Toast.LENGTH_SHORT).show();
                } else {
                    // If a month is selected, proceed to send it to the next activity
                    Intent intent = new Intent(ActivityFacAttendance.this, ActivityAttendance.class);
                    intent.putExtra("selectedField", selectedField);
                    startActivity(intent);
                }
            }
        });
    }
}