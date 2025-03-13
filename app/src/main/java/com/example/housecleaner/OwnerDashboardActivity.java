package com.example.housecleaner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class OwnerDashboardActivity extends AppCompatActivity implements HouseAdapter.OnHouseListener {

    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private HouseAdapter adapter;
    private ImageView ivHouse;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_dashboard);

        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Get the logged-in user's ID from the intent
        userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load houses for the logged-in user
        loadHouses();

        // Set up FloatingActionButton to add a new house
        FloatingActionButton fabAddHouse = findViewById(R.id.fabAddHouse);
        fabAddHouse.setOnClickListener(v -> showAddHouseDialog());
    }

    private void loadHouses() {
        List<House> houses = dbHelper.getHousesByUserId(userId);
        adapter = new HouseAdapter(houses, this, dbHelper); // Pass dbHelper to adapter
        recyclerView.setAdapter(adapter);
    }

    private void showAddHouseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_house, null);

        EditText etLocation = view.findViewById(R.id.etLocation);
        EditText etContact = view.findViewById(R.id.etContact);
        NumberPicker npRooms = view.findViewById(R.id.npRooms);
        NumberPicker npBathrooms = view.findViewById(R.id.npBathrooms);
        Spinner spinnerFloorType = view.findViewById(R.id.spinnerFloorType);
        ivHouse = view.findViewById(R.id.ivHouse);
        Button btnUpload = view.findViewById(R.id.btnUpload);

        // Set up NumberPicker values
        npRooms.setMinValue(1);
        npRooms.setMaxValue(10);
        npBathrooms.setMinValue(1);
        npBathrooms.setMaxValue(5);

        // Set up Spinner for floor types
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.floor_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFloorType.setAdapter(adapter);

        // Handle image upload
        btnUpload.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
        });

        // Handle form submission
        builder.setView(view)
                .setTitle("Add New House")
                .setPositiveButton("Submit", (dialog, which) -> {
                    if (etLocation.getText().toString().isEmpty() ||
                            etContact.getText().toString().isEmpty() ||
                            ivHouse.getDrawable() == null) {
                        Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Create a new House object
                    House house = new House(
                            etLocation.getText().toString(),
                            String.valueOf(npRooms.getValue()),
                            String.valueOf(npBathrooms.getValue()),
                            spinnerFloorType.getSelectedItem().toString(),
                            etContact.getText().toString(),
                            getImageString(ivHouse)
                    );

                    // Add the house to the database
                    if (dbHelper.addHouse(house, userId)) {
                        loadHouses(); // Refresh the list
                        Toast.makeText(this, "House added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to add house", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private String getImageString(ImageView imageView) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                ivHouse.setImageBitmap(bitmap);
            } catch (Exception e) {
                Toast.makeText(this, "Image error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.owner_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else if (item.getItemId() == R.id.menu_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onHouseDelete(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Delete this listing?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    House house = adapter.getHouseAt(position);
                    if (dbHelper.deleteHouse(house.getId())) {
                        adapter.removeHouse(position);
                        Toast.makeText(this, "House deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to delete house", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}