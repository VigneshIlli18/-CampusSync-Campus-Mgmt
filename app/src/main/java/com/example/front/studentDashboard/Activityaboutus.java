package com.example.front.studentDashboard;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.front.R;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Activityaboutus extends AppCompatActivity {

    private RecyclerView imageRecyclerView;
    private ArrayList<Integer> images = new ArrayList<>();
    private int currentIndex = 0;
    private Timer timer;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);

        // Initialize RecyclerView
        imageRecyclerView = findViewById(R.id.imageRecyclerView);
        imageRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Add images to the list
        images.add(R.drawable.rgm1);
        images.add(R.drawable.rgm2);
        images.add(R.drawable.rgm3);
        images.add(R.drawable.rgm4);

        // Set up the adapter with the list of images
        ImageAdapter adapter = new ImageAdapter(images);
        imageRecyclerView.setAdapter(adapter);

        // Start auto rotation
        startAutoRotation();
    }

    private void startAutoRotation() {
        handler = new Handler(Looper.getMainLooper());
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Increment index and loop back to 0 if reached end
                        currentIndex = (currentIndex + 1) % images.size();
                        // Smoothly scroll to the next item
                        imageRecyclerView.smoothScrollToPosition(currentIndex);
                    }
                });
            }
        }, 0, 3000); // Change image every 3 seconds (adjust as needed)
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cancel the timer when the activity is destroyed
        timer.cancel();
    }

    // Nested class for the ImageAdapter
    private class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

        private ArrayList<Integer> images;

        public ImageAdapter(ArrayList<Integer> images) {
            this.images = images;
        }

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.item_image_forabout, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
            holder.bind(images.get(position));
        }

        @Override
        public int getItemCount() {
            return images.size();
        }

        class ImageViewHolder extends RecyclerView.ViewHolder {
            private ImageView imageView;

            ImageViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView);
            }

            void bind(int imageResId) {
                imageView.setImageResource(imageResId);
            }
        }
    }
}