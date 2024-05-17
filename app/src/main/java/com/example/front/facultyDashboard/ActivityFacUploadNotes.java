package com.example.front.facultyDashboard;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.front.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ActivityFacUploadNotes extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_FILE = 123;
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fac_upload_notes);

        // Set up AutoCompleteTextView with recommended subject names
        String[] recommendedSubjects = getResources().getStringArray(R.array.Subject); // Replace with your array resource
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, recommendedSubjects);
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.syllabusAutoCompleteTextView);
        autoCompleteTextView.setAdapter(adapter);
        EditText filenameEditText = findViewById(R.id.edittext);

        ImageView uploadImage = findViewById(R.id.uploadImage);
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });

        Button uploadButton = findViewById(R.id.uploadbutton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInputValid()) {
                    uploadFile(filenameEditText.getText().toString().trim());
                }
            }
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Allow all file types
        startActivityForResult(intent, REQUEST_CODE_PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();
            // Handle the selected file URI here if needed
        }
    }

    private boolean isInputValid() {
        String subjectName = ((AutoCompleteTextView) findViewById(R.id.syllabusAutoCompleteTextView)).getText().toString();
        String filename = ((EditText) findViewById(R.id.edittext)).getText().toString().trim();
        if (subjectName.isEmpty() || filename.isEmpty() || fileUri == null) {
            Toast.makeText(this, "Please ensure all fields are filled", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void uploadFile(String filename) {
        // Get the subject name from AutoCompleteTextView
        String subjectName = ((AutoCompleteTextView) findViewById(R.id.syllabusAutoCompleteTextView)).getText().toString();

        // Get reference to Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create reference with subject name and filename as part of the path
        StorageReference subjectRef = storage.getReference().child("Btech/Studymaterials/" + subjectName + "/" + filename);

        // Upload the file to Firebase Storage
        subjectRef.putFile(fileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Handle successful upload
                        Toast.makeText(ActivityFacUploadNotes.this, "File uploaded successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failed upload
                        Toast.makeText(ActivityFacUploadNotes.this, "Failed to upload file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}