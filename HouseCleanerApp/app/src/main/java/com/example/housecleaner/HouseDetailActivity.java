package com.example.housecleaner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class HouseDetailActivity extends AppCompatActivity {

    private House currentHouse;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_detail);
        dbHelper = new DatabaseHelper(this);

        int houseId = getIntent().getIntExtra("HOUSE_ID", -1);
        currentHouse = dbHelper.getHouseById(houseId);

        ImageView ivHouse = findViewById(R.id.ivHouseDetail);
        TextView tvLocation = findViewById(R.id.tvLocation);
        TextView tvRooms = findViewById(R.id.tvRooms);
        TextView tvBathrooms = findViewById(R.id.tvBathrooms);
        TextView tvFloorType = findViewById(R.id.tvFloorType);
        TextView tvContact = findViewById(R.id.tvContact);
        Button btnAccept = findViewById(R.id.btnAcceptJob);

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

        btnAccept.setOnClickListener(v -> {
            if (currentHouse != null) {
                boolean success = dbHelper.acceptJob(currentHouse.getId());
                if (success) {
                    Toast.makeText(this, "Job Accepted!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Accept failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}