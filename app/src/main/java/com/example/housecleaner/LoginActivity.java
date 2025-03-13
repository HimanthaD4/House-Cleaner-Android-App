package com.example.housecleaner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnSignup;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);

        dbHelper = new DatabaseHelper(this);

        btnLogin.setOnClickListener(v -> loginUser());

        btnSignup.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String userType = dbHelper.getUserType(email);

        if (userType == null) {
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.checkUser(email, password)) {
            int userId = dbHelper.getUserId(email);

            Intent intent;
            if (userType.equalsIgnoreCase("owner")) {
                intent = new Intent(this, OwnerDashboardActivity.class);
            } else if (userType.equalsIgnoreCase("cleaner")) {
                intent = new Intent(this, CleanerDashboardActivity.class);
            } else {
                Toast.makeText(this, "Invalid user type", Toast.LENGTH_SHORT).show();
                return;
            }

            intent.putExtra("USER_ID", userId);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid email/password", Toast.LENGTH_SHORT).show();
        }
    }
}