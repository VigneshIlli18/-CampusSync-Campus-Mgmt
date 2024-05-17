package com.example.front.facultyDashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.front.R;

public class Activity_marks extends AppCompatActivity {

    private Spinner subjectSpinner;
    private Spinner termSpinner;
    private Button proceedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fac_marks);

        subjectSpinner = findViewById(R.id.subjectSpinner);
        termSpinner = findViewById(R.id.termSpinner);
        proceedButton = findViewById(R.id.proceedButton);

        ArrayAdapter<CharSequence> subjectAdapter = ArrayAdapter.createFromResource(
                this, R.array.marksSub, android.R.layout.simple_spinner_item);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectSpinner.setAdapter(subjectAdapter);

        ArrayAdapter<CharSequence> termAdapter = ArrayAdapter.createFromResource(
                this, R.array.term_options, android.R.layout.simple_spinner_item);
        termAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        termSpinner.setAdapter(termAdapter);

        termSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean isSubjectSelected = subjectSpinner.getSelectedItemPosition() != 0;
                boolean isTermSelected = position != 0;
                proceedButton.setEnabled(isSubjectSelected && isTermSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean isSubjectSelected = position != 0;
                boolean isTermSelected = termSpinner.getSelectedItemPosition() != 0;
                proceedButton.setEnabled(isSubjectSelected && isTermSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedSubject = subjectSpinner.getSelectedItem().toString();
                String selectedTerm = termSpinner.getSelectedItem().toString();

                // Start next activity passing selected subject and term
                Intent intent = new Intent(Activity_marks.this, Activityentermarks.class);
                intent.putExtra("subject", selectedSubject);
                intent.putExtra("term", selectedTerm);
                startActivity(intent);
            }
        });
    }
}