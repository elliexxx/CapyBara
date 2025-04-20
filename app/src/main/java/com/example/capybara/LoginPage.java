package com.example.capybara;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginPage extends AppCompatActivity {

    Button backButton, loginButton;
    EditText editUsername, editPassword;

    DatabaseHelper dbHelper; // Assuming you created this class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        backButton = findViewById(R.id.loginback);
        loginButton = findViewById(R.id.loginmainmenu);
        editUsername = findViewById(R.id.editUsername); // Add this to your XML
        editPassword = findViewById(R.id.editPassword); // Add this to your XML

        dbHelper = new DatabaseHelper(this);

        // Back to Title Page
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginPage.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Login validation
        loginButton.setOnClickListener(v -> {
            String username = editUsername.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginPage.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            } else {
                boolean isValid = dbHelper.validateUser(username, password);
                if (isValid) {
                    Toast.makeText(LoginPage.this, "Login successful", Toast.LENGTH_SHORT).show();

                    // ✅ Pass username to MainmenuPage
                    Intent intent = new Intent(LoginPage.this, MainmenuPage.class);
                    intent.putExtra("USERNAME", username); // 👈 this line sends the username
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginPage.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}