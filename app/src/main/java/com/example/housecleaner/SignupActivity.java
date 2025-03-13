package com.example.housecleaner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText;
    private Button btnOwner, btnCleaner, btnSignup;
    private DatabaseHelper dbHelper;
    private String selectedUserType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        btnOwner = findViewById(R.id.btnOwner);
        btnCleaner = findViewById(R.id.btnCleaner);
        btnSignup = findViewById(R.id.btnSignup);

        dbHelper = new DatabaseHelper(this);

        btnOwner.setOnClickListener(v -> {
            selectedUserType = "owner";
            btnOwner.setBackgroundTintList(getResources().getColorStateList(R.color.orange_yellow_dark));
            btnCleaner.setBackgroundTintList(getResources().getColorStateList(R.color.transparent_white));
        });

        btnCleaner.setOnClickListener(v -> {
            selectedUserType = "cleaner";
            btnCleaner.setBackgroundTintList(getResources().getColorStateList(R.color.orange_yellow_dark));
            btnOwner.setBackgroundTintList(getResources().getColorStateList(R.color.transparent_white));
        });

        btnSignup.setOnClickListener(v -> signupUser());
    }

    private void signupUser() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedUserType.isEmpty()) {
            Toast.makeText(this, "Select user type", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.insertUser(name, email, password, selectedUserType)) {
            Toast.makeText(this, "Signup successful", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Signup failed", Toast.LENGTH_SHORT).show();
        }
    }
}