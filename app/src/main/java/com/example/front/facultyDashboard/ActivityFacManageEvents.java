package com.example.front.facultyDashboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ActivityFacManageEvents extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextRegistrationLink;
    private ImageView imageViewAdditionalImage;
    private Button buttonAddRegistrationLink;
    private Button buttonUploadAdditionalImage;
    private Uri additionalImageUri;
    private AutoCompleteTextView autoCompleteEvent;
    private FirebaseFirestore db;
    private StorageReference additionalImagesStorageReference;

    private List<String> eventNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fac_manage_events);

        editTextRegistrationLink = findViewById(R.id.editTextRegistrationLink);
        imageViewAdditionalImage = findViewById(R.id.imageViewAdditionalImage);
        buttonAddRegistrationLink = findViewById(R.id.buttonAddRegistrationLink);
        buttonUploadAdditionalImage = findViewById(R.id.buttonUploadAdditionalImage);
        autoCompleteEvent = findViewById(R.id.manageactv);
        db = FirebaseFirestore.getInstance();
        additionalImagesStorageReference = FirebaseStorage.getInstance().getReference("events");

        // Set up AutoCompleteTextView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, eventNames);
        autoCompleteEvent.setAdapter(adapter);

        // Fetch event names from Firestore and populate the list
        CollectionReference eventsCollectionRef = db.collection("events");
        eventsCollectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String eventName = documentSnapshot.getId();
                    eventNames.add(eventName);
                }
                adapter.notifyDataSetChanged(); // Update AutoCompleteTextView suggestions
            }
        });

        buttonAddRegistrationLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isACTVFilled()) {
                    addRegistrationLink();
                } else {
                    Toast.makeText(ActivityFacManageEvents.this, "Please select an event", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonUploadAdditionalImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadAdditionalImage();
            }
        });

        imageViewAdditionalImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageFileChooser();
            }
        });
    }

    private boolean isACTVFilled() {
        return !autoCompleteEvent.getText().toString().isEmpty();
    }

    private void addRegistrationLink() {
        final String selectedEventName = autoCompleteEvent.getText().toString().trim();
        final String registrationLink = editTextRegistrationLink.getText().toString().trim();

        CollectionReference eventsCollectionRef = db.collection("events");
        DocumentReference eventDocRef = eventsCollectionRef.document(selectedEventName);

        eventDocRef
                .update("registration_link", registrationLink)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ActivityFacManageEvents.this, "Registration link added for event: " + selectedEventName, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ActivityFacManageEvents.this, "Failed to add registration link for event: " + selectedEventName + " Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadAdditionalImage() {
        if (additionalImageUri != null) {
            String imageName = UUID.randomUUID().toString();
            String selectedEventname = autoCompleteEvent.getText().toString().trim();
            final String SelectedEventname = selectedEventname.replaceAll("\\s", "_");
            FirebaseStorage storage = FirebaseStorage.getInstance();

            // Create reference with subject name and filename as part of the path
            StorageReference imageReference = storage.getReference().child("events/" + SelectedEventname + "/" + imageName);

            imageReference.putFile(additionalImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Toast.makeText(ActivityFacManageEvents.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                    // Additional image uploaded, perform any necessary actions
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ActivityFacManageEvents.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Please select an additional image", Toast.LENGTH_SHORT).show();
        }
    }

    private void openImageFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            additionalImageUri = data.getData();
            imageViewAdditionalImage.setImageURI(additionalImageUri);
        }
    }
}