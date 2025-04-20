package com.example.capybara;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CapybarasocietyPage extends AppCompatActivity {

    View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_capybarasociety_page);

        rootView = findViewById(R.id.main); // Ensure your root layout has android:id="@+id/main"

        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Register all your character buttons here
        setupCharacterClick(R.id.button14, R.layout.popup_scootie);
        setupCharacterClick(R.id.button15, R.layout.popup_shypie);
        setupCharacterClick(R.id.button16, R.layout.popup_boopie);
        setupCharacterClick(R.id.button17, R.layout.popup_florabelle);
        setupCharacterClick(R.id.button18, R.layout.popup_clicko);
        setupCharacterClick(R.id.button19, R.layout.popup_snapsy);
        setupCharacterClick(R.id.button20, R.layout.popup_copybara);
        setupCharacterClick(R.id.button21, R.layout.popup_luloo);
        setupCharacterClick(R.id.button22, R.layout.popup_strumstrum);
        setupCharacterClick(R.id.button23, R.layout.popup_winky);
        setupCharacterClick(R.id.button24, R.layout.popup_duckydot);
        setupCharacterClick(R.id.button25, R.layout.popup_tidytubs);
        setupCharacterClick(R.id.button26, R.layout.popup_bubba);
        // 🔁 You can add more buttons and popups like this:
        // setupCharacterClick(R.id.button15, R.layout.popup_anothercharacter);

        // HOME button logic (button11)
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button homeButton = findViewById(R.id.homebutton);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(CapybarasocietyPage.this, MainmenuPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish(); // Optional: closes current activity
        });
    }

    private void setupCharacterClick(int characterButtonId, int popupLayoutId) {
        View character = findViewById(characterButtonId); // changed from ImageView to View
        character.setOnClickListener(v -> showCharacterPopup(popupLayoutId));
    }

    private void showCharacterPopup(int popupLayoutId) {
        rootView.setAlpha(0.5f);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.TransparentDialog);
        View popupView = getLayoutInflater().inflate(popupLayoutId, null);
        builder.setView(popupView);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        Button closeBtn = popupView.findViewById(R.id.btnclose);
        closeBtn.setOnClickListener(v -> {
            dialog.dismiss();
            rootView.setAlpha(1.0f);
        });


    }
}
