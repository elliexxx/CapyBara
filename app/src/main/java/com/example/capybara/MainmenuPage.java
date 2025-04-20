package com.example.capybara;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainmenuPage extends AppCompatActivity {

    Button playButton, scoreboardButton, societyButton, logoutButton, quitButton;
    Dialog popupDifficulty;

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

        // Connect buttons
        playButton = findViewById(R.id.playButton);
        scoreboardButton = findViewById(R.id.scoreboardButton);
        societyButton = findViewById(R.id.societyButton);
        logoutButton = findViewById(R.id.logoutButton);
        quitButton = findViewById(R.id.quitButton);

        // PLAY button shows difficulty popup
        playButton.setOnClickListener(v -> showDifficultyPopup());

        // SCOREBOARD
        scoreboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainmenuPage.this, Scoreboard_Page.class);

            // Get the username from intent and pass it along
            String username = getIntent().getStringExtra("username");
            intent.putExtra("username", username);

            startActivity(intent);
        });


        // CAPYBARA SOCIETY
        societyButton.setOnClickListener(v -> {
            startActivity(new Intent(MainmenuPage.this, CapybarasocietyPage.class));
        });

        // LOGOUT
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

        // QUIT
        quitButton.setOnClickListener(v -> {
            new AlertDialog.Builder(MainmenuPage.this)
                    .setTitle("Quit Game")
                    .setMessage("Are you sure you want to quit?")
                    .setPositiveButton("Yes", (dialog, which) -> finishAffinity())
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void showDifficultyPopup() {
        popupDifficulty = new Dialog(this);
        popupDifficulty.setContentView(R.layout.popup_categories);
        popupDifficulty.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button btnEasy = popupDifficulty.findViewById(R.id.easybtn);
        Button btnMedium = popupDifficulty.findViewById(R.id.medbtn);
        Button btnHard = popupDifficulty.findViewById(R.id.hrdbtn);
        Button btnClose = popupDifficulty.findViewById(R.id.exitbtn);

        btnEasy.setOnClickListener(v -> {
            startActivity(new Intent(MainmenuPage.this, GamescreenEasy.class));
            popupDifficulty.dismiss();
        });

        btnMedium.setOnClickListener(v -> {
            startActivity(new Intent(MainmenuPage.this, GamescreenMedium.class));
            popupDifficulty.dismiss();
        });

        btnHard.setOnClickListener(v -> {
            startActivity(new Intent(MainmenuPage.this, GamescreenHard.class));
            popupDifficulty.dismiss();
        });

        btnClose.setOnClickListener(v -> popupDifficulty.dismiss());

        popupDifficulty.show();
    }
}
