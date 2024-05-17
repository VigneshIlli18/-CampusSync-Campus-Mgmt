package com.example.front.studentDashboard;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.front.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class ActivityViewTimetable extends AppCompatActivity {

    private String folderName; // Variable to hold the folder name
    private FirebaseStorage storage;
    private LinearLayout containerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stu_view_notes);

        // Retrieve the folder name from the intent
        folderName = getIntent().getStringExtra("subject");

        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance();

        // Get a reference to the folder
        final StorageReference folderRef = storage.getReference().child("Btech/Timetables/"+folderName);

        // Initialize container layout
        containerLayout = findViewById(R.id.containerLayout);

        // List all items (files and subfolders) within the folder
        folderRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(com.google.firebase.storage.ListResult listResult) {
                for (StorageReference item : listResult.getItems()) {
                    // Get the file name
                    final String fileName = item.getName();

                    // Inflate the card layout
                    View cardView = LayoutInflater.from(ActivityViewTimetable.this).inflate(R.layout.cardview_layout, containerLayout, false);

                    // Set file name in the TextView
                    TextView titleTextView = cardView.findViewById(R.id.recTitle);
                    titleTextView.setText(fileName);

                    // Set onClickListener for download symbol ImageView
                    ImageView downloadIcon = cardView.findViewById(R.id.downloadIcon);
                    downloadIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Handle file download here
                            downloadFile(folderRef, fileName);
                        }
                    });

                    // Add the card view to the container layout
                    containerLayout.addView(cardView);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle any errors
                Toast.makeText(ActivityViewTimetable.this, "Failed to list items in the folder", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to handle file download
    private void downloadFile(StorageReference folderRef, String fileName) {
        // Create a reference to the file
        StorageReference fileRef = folderRef.child(fileName);

        // Specify the directory where you want to save the downloaded file in external storage
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        // Create the directory if it doesn't exist
        if (!directory.exists()) {
            directory.mkdirs(); // Create the directory and any necessary parent directories
        }

        // Create a File object with the desired file path and name in external storage
        final File localFile = new File(directory, fileName);

        // Set up progress bar
        final ProgressBar progressBar = new ProgressBar(ActivityViewTimetable.this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setIndeterminate(false);
        progressBar.setMax(100);

        // Add progress bar to layout
        containerLayout.addView(progressBar);

        // Perform file download with progress tracking
        fileRef.getFile(localFile).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Calculate progress percentage
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                // Update progress bar
                progressBar.setProgress((int) progress);
            }
        }).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // File downloaded successfully, now move it to the final location
                try {
                    // Remove progress bar after download
                    containerLayout.removeView(progressBar);

                    // Display toast message
                    Toast.makeText(ActivityViewTimetable.this, "File downloaded successfully", Toast.LENGTH_SHORT).show();

                    // Log success
                    Log.d("Download", "File downloaded successfully");

                    // Further actions if needed, such as opening the file
                } catch (Exception e) {
                    // Log error
                    Log.e("Download", "Error moving file: " + e.getMessage());

                    // Display toast message for error
                    Toast.makeText(ActivityViewTimetable.this, "Error moving file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(ActivityViewTimetable.this, "Failed to download file: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                // Remove progress bar if download fails
                containerLayout.removeView(progressBar);
            }
        });
    }

}
