package com.example.housecleaner;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.SharedPreferences;
import java.util.List;

public class HouseDetailActivity extends AppCompatActivity {
    private House currentHouse;
    private DatabaseHelper dbHelper;
    private EditText etComment;
    private CommentAdapter commentAdapter;
    private int houseId;
    private RecyclerView rvComments;
    private TextView tvRooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_detail);
        dbHelper = DatabaseHelper.getInstance(this);
        houseId = getIntent().getIntExtra("HOUSE_ID", -1);
        currentHouse = dbHelper.getHouseById(houseId);

        // Initialize views
        ImageView ivHouse = findViewById(R.id.ivHouseDetail);
        TextView tvLocation = findViewById(R.id.tvLocation);
        tvRooms = findViewById(R.id.tvRooms);
        TextView tvBathrooms = findViewById(R.id.tvBathrooms);
        TextView tvFloorType = findViewById(R.id.tvFloorType);
        TextView tvContact = findViewById(R.id.tvContact);
        Button btnAccept = findViewById(R.id.btnAcceptJob);
        etComment = findViewById(R.id.etComment);
        rvComments = findViewById(R.id.rvComments);

        // Setup RecyclerView
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter();
        rvComments.setAdapter(commentAdapter);

        // Load house details
        if (currentHouse != null) {
            tvLocation.setText(currentHouse.getLocation());
            tvRooms.setText("Rooms: " + currentHouse.getRooms());
            tvBathrooms.setText("Bathrooms: " + currentHouse.getBathrooms());
            tvFloorType.setText("Floor: " + currentHouse.getFloorType());
            tvContact.setText("Contact: " + currentHouse.getContact());

            if (currentHouse.getImage() != null) {
                byte[] decodedString = Base64.decode(currentHouse.getImage(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                ivHouse.setImageBitmap(decodedByte);
            }
        }

        // Set click listeners
        btnAccept.setOnClickListener(v -> handleJobAcceptance());
        findViewById(R.id.btnPostComment).setOnClickListener(v -> postComment());

        // Initial comments load
        refreshComments();
    }

    private void postComment() {
        String commentText = etComment.getText().toString().trim();
        if (!commentText.isEmpty()) {
            new Thread(() -> {
                int userId = dbHelper.getUserId(getCurrentUserEmail());
                boolean success = dbHelper.addComment(houseId, userId, commentText, 0);

                runOnUiThread(() -> {
                    if (success) {
                        etComment.setText("");
                        refreshComments();
                    } else {
                        Toast.makeText(this, "Failed to post comment", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        }
    }

    private void refreshComments() {
        new Thread(() -> {
            List<Comment> updatedComments = dbHelper.getCommentsByHouseId(houseId);
            runOnUiThread(() -> {
                commentAdapter.updateComments(updatedComments);
                if (updatedComments.isEmpty()) {
                    Toast.makeText(this, "No comments yet", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void handleJobAcceptance() {
        if (currentHouse != null) {
            boolean success = dbHelper.acceptJob(currentHouse.getId());
            if (success) {
                Toast.makeText(this, "Job Accepted!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Accept failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getCurrentUserEmail() {
        SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
        return prefs.getString("USER_EMAIL", "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshComments();
    }
}