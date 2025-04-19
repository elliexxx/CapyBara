package com.example.capybara;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainmenuPage extends AppCompatActivity {

    Button mediumButton, hardButton, playButton, scoreboardButton, societyButton, logoutButton, quitButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mainmenu_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Connect buttons to XML
        mediumButton = findViewById(R.id.medium);
        hardButton = findViewById(R.id.hard);
        playButton = findViewById(R.id.playButton); // Replace with your actual button ID
        scoreboardButton = findViewById(R.id.scoreboardButton);
        societyButton = findViewById(R.id.societyButton);
        logoutButton = findViewById(R.id.logoutButton);
        quitButton = findViewById(R.id.quitButton);

        //medium samp
        mediumButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainmenuPage.this, GamescreenMedium.class);
            startActivity(intent);
        });

        //hard samp
        hardButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainmenuPage.this, GamescreenHard.class);
            startActivity(intent);
        });

        // PLAY button
        playButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainmenuPage.this, GamescreenEasy.class);
            startActivity(intent);
        });

        // SCOREBOARD button
        scoreboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainmenuPage.this, Scoreboard_Page.class);
            startActivity(intent);
        });

        // CAPYBARA SOCIETY button
        societyButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainmenuPage.this, CapybarasocietyPage.class);
            startActivity(intent);
        });

        // LOGOUT button with confirmation
        logoutButton.setOnClickListener(v -> {
            new AlertDialog.Builder(MainmenuPage.this)
                    .setTitle("Log Out")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Intent intent = new Intent(MainmenuPage.this, LoginPage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // QUIT button with confirmation
        quitButton.setOnClickListener(v -> {
            new AlertDialog.Builder(MainmenuPage.this)
                    .setTitle("Quit Game")
                    .setMessage("Are you sure you want to quit?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        finishAffinity(); // closes the app
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }
}
