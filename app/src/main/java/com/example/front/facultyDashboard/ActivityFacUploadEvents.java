package com.example.front.facultyDashboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.front.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ActivityFacUploadEvents extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextEventName;
    private EditText editTextEventDescription;
    private EditText editTextEventLocation;
    private EditText editTextRegistrationLink;
    private DatePicker datePicker;
    private ImageView imageViewEvent;
    private Button buttonCreateEvent;

    private Uri imageUri;
    private ProgressDialog progressDialog;

    private StorageReference eventsStorageReference;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fac_events);

        editTextEventName = findViewById(R.id.editTextEventName);
        editTextEventDescription = findViewById(R.id.editTextEventDescription);
        editTextEventLocation = findViewById(R.id.editTextEventLocation);
        editTextRegistrationLink = findViewById(R.id.editTextRegistrationLink);
        datePicker = findViewById(R.id.datePicker);
        imageViewEvent = findViewById(R.id.imageViewEvent);
        buttonCreateEvent = findViewById(R.id.buttonCreateEvent);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating event...");
        progressDialog.setCancelable(false);

        eventsStorageReference = FirebaseStorage.getInstance().getReference("events");
        db = FirebaseFirestore.getInstance();

        imageViewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        buttonCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    createEvent();
                }
            }
        });
    }

    private boolean validateInputs() {
        String eventName = editTextEventName.getText().toString().trim();
        String eventDescription = editTextEventDescription.getText().toString().trim();
        String eventLocation = editTextEventLocation.getText().toString().trim();
        String registrationLink = editTextRegistrationLink.getText().toString().trim();

        if (TextUtils.isEmpty(eventName) || TextUtils.isEmpty(eventDescription)
                || TextUtils.isEmpty(eventLocation) || TextUtils.isEmpty(registrationLink) || imageUri == null) {
            Toast.makeText(this, "Please fill in all fields and select an image", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewEvent.setImageURI(imageUri);
        }
    }

    private void createEvent() {
        progressDialog.show();

        String eventName = editTextEventName.getText().toString().trim();
        String eventDescription = editTextEventDescription.getText().toString().trim();
        String eventLocation = editTextEventLocation.getText().toString().trim();
        String registrationLink = editTextRegistrationLink.getText().toString().trim();
        String eventDate = formatDate(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());

        final String eventFolderName = eventName.replaceAll("\\s", "_");

        final StorageReference eventFolderReference = eventsStorageReference.child(eventFolderName);
        final StorageReference imageReference = eventFolderReference.child("event_image.jpg");

        UploadTask uploadTask = imageReference.putFile(imageUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                saveEventDetails(eventName, eventDescription, eventLocation, registrationLink, eventDate, eventFolderName);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(ActivityFacUploadEvents.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveEventDetails(String eventName, String eventDescription, String eventLocation, String registrationLink, String eventDate, final String eventFolderName) {
        Map<String, Object> eventDetails = new HashMap<>();
        eventDetails.put("name", eventName);
        eventDetails.put("description", eventDescription);
        eventDetails.put("location", eventLocation);
        eventDetails.put("registration_link", registrationLink);
        eventDetails.put("date", eventDate);

        db.collection("events").document(eventName)
                .set(eventDetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(ActivityFacUploadEvents.this, "Event created successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ActivityFacUploadEvents.this, "Failed to create event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String formatDate(int year, int month, int dayOfMonth) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        return sdf.format(calendar.getTime());
    }
}