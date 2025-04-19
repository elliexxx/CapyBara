package com.example.capybara;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignupPage extends AppCompatActivity {

    Button signupButton, backButton;
    EditText editUsername, editPassword;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        signupButton = findViewById(R.id.button4);  // SIGN-UP button
        backButton = findViewById(R.id.button3);    // BACK button
        editUsername = findViewById(R.id.editUsername);  // Add to XML
        editPassword = findViewById(R.id.editPassword);  // Add to XML

        dbHelper = new DatabaseHelper(this);

        signupButton.setOnClickListener(v -> {
            String username = editUsername.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignupPage.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (dbHelper.checkUserExists(username)) {
                Toast.makeText(SignupPage.this, "Username already exists", Toast.LENGTH_SHORT).show();
            } else {
                boolean inserted = dbHelper.addUser(username, password);
                if (inserted) {
                    Toast.makeText(SignupPage.this, "Signed up successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignupPage.this, LoginPage.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SignupPage.this, "Signup failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(SignupPage.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}

