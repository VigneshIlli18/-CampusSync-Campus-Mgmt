package com.example.front.studentDashboard;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.front.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class Activityevents extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_events);

        db = FirebaseFirestore.getInstance();

        // Get reference to the container layout
        LinearLayout containerLayout = findViewById(R.id.containerLayout);

        // Fetch events from Firestore
        fetchEventsFromFirestore(containerLayout);
    }

    // Method to fetch events from Firebase Firestore
    // Method to fetch events from Firebase Firestore
    private void fetchEventsFromFirestore(final LinearLayout containerLayout) {
        db.collection("events").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        String eventName = document.getId();
                        String eventDate = document.getString("date");
                        String eventDescription = document.getString("description");
                        String eventLocation = document.getString("location");
                        String eventLink = document.getString("registration_link");
                        String subjectName = document.getString("subject_name"); // Assuming you have a field for subject name in your document

                        // Create event card view
                        View eventCardView = createEventCard(eventName, eventDate, eventDescription, eventLocation, eventLink, subjectName);
                        // Add event card view to container layout
                        containerLayout.addView(eventCardView);
                    }
                } else {
                    // Handle errors
                }
            }
        });
    }

    // Method to create an event card dynamically
    private View createEventCard(String eventName, String eventDate, String eventDescription, String eventLocation, String eventLink, String subjectName) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View eventCardView = inflater.inflate(R.layout.event_card_view, null);

        // Set event details
        TextView eventNameTextView = eventCardView.findViewById(R.id.eventName);
        TextView eventDateTextView = eventCardView.findViewById(R.id.eventDate);
        TextView eventDescriptionTextView = eventCardView.findViewById(R.id.eventDescription);
        TextView eventLocationTextView = eventCardView.findViewById(R.id.eventLocation);
        TextView eventLinkTextView = eventCardView.findViewById(R.id.eventlink);
        LinearLayout imageContainerLayout = eventCardView.findViewById(R.id.imageContainer);

        eventNameTextView.setText(eventName);
        eventDateTextView.setText(eventDate);
        eventDescriptionTextView.setText(eventDescription);
        eventLocationTextView.setText(eventLocation);
        eventLinkTextView.setText(eventLink);

        final String eventFolderName = eventName.replaceAll("\\s", "_");

        // Construct the image path based on the event name and subject name
        String imagePath = "events/" + eventFolderName + "/" ; // Adjust the path as per your Firebase Storage structure

        // Load and display event images dynamically
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(imagePath);
        storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            ImageView imageView = new ImageView(Activityevents.this);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            layoutParams.setMargins(0, 0, 0, 16); // Add margin between images
                            imageView.setLayoutParams(layoutParams);
                            imageContainerLayout.addView(imageView);
                            Picasso.get().load(uri).into(imageView);
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failures
            }
        });

        return eventCardView;
    }
}
