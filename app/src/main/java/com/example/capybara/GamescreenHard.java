package com.example.capybara;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GamescreenHard extends AppCompatActivity {
    private ImageView firstFlipped = null;
    private ImageView secondFlipped = null;
    private int flips = 0;
    private int matchedPairs = 0;
    private final int totalPairs = 10; // 20
    // cards / 2

    // Optional: map front image IDs to a tag for matching
    private Map<ImageView, String> cardTags = new HashMap<>();

    private CountDownTimer countDownTimer;
    private boolean gameWon = false;


    private TextView timerText;
    private Button pauseButton;
    private boolean isPaused = false;
    private boolean isBusy = false;
    private int flipCount = 0;
    private TextView flipCounterTextView;
    private long timeLeftInMillis = 60000;
    private int score = 0;
    private int timeRemaining = 60; // updated by timer
    private TextView scoreTextView;
    private CountDownTimer gameTimer;
    private ScoreDb dbHelper;

    private ArrayList<ImageView> frontViews = new ArrayList<>();
    private ArrayList<ImageView> backViews = new ArrayList<>();
    private ArrayList<Integer> imageResIds = new ArrayList<>();

    private ImageView firstCardFront = null;
    private ImageView firstCardBack = null;

    private Handler handler = new Handler();

    private int[] imageDrawables = {
            R.drawable.copy1, R.drawable.copy3, R.drawable.copy1, R.drawable.copy2,
            R.drawable.copy4, R.drawable.copy5, R.drawable.copy8, R.drawable.copy9,
            R.drawable.copy10, R.drawable.copy9, R.drawable.copy4, R.drawable.copy6,
            R.drawable.copy7, R.drawable.copy3, R.drawable.copy8, R.drawable.copy7,
            R.drawable.copy6, R.drawable.copy10, R.drawable.copy2, R.drawable.copy5,

    };

    private int[] frontIds = {
            R.id.cardFront1, R.id.cardFront2, R.id.cardFront3, R.id.cardFront4,
            R.id.cardFront5, R.id.cardFront6, R.id.cardFront7, R.id.cardFront8,
            R.id.cardFront9, R.id.cardFront10, R.id.cardFront11, R.id.cardFront12,
            R.id.cardFront13, R.id.cardFront14, R.id.cardFront15, R.id.cardFront16,
            R.id.cardFront17, R.id.cardFront18, R.id.cardFront19, R.id.cardFront20
    };

    private int[] backIds = {
            R.id.cardBack1, R.id.cardBack2, R.id.cardBack3, R.id.cardBack4,
            R.id.cardBack5, R.id.cardBack6, R.id.cardBack7, R.id.cardBack8,
            R.id.cardBack9, R.id.cardBack10, R.id.cardBack11, R.id.cardBack12,
            R.id.cardBack13, R.id.cardBack14, R.id.cardBack15, R.id.cardBack16,
            R.id.cardBack17, R.id.cardBack18, R.id.cardBack19, R.id.cardBack20
    };

    private void flipCard(ImageView front, ImageView back) {
        if (back.getVisibility() == View.VISIBLE) {
            back.setVisibility(View.GONE);
            front.setVisibility(View.VISIBLE);
        } else {
            front.setVisibility(View.GONE);
            back.setVisibility(View.VISIBLE);
        }
    }

    private void showWinPopup() {
        gameWon = true;

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        Dialog congratsDialog = new Dialog(GamescreenHard.this);
        congratsDialog.setContentView(R.layout.popup_congratsuwon);
        congratsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        congratsDialog.setCancelable(false);
        congratsDialog.show();
    }


    private void updateTimerText() {
        int seconds = (int) (timeLeftInMillis / 1000);
        timerText.setText(String.valueOf(seconds));
    }


    private void checkMatch(int secondIndex) {
        String tag1 = cardTags.get(firstFlipped);
        String tag2 = cardTags.get(secondFlipped);

        if (tag1.equals(tag2)) {
            // ✅ Match: Keep cards revealed
            matchedPairs++;
            if (matchedPairs == totalPairs) {
                showWinPopup();
            }
            firstFlipped = null;
            secondFlipped = null;
        } else {
            // ❌ Mismatch: Animate shake and flip back
            ImageView back1 = backViews.get(frontViews.indexOf(firstFlipped));
            ImageView back2 = backViews.get(frontViews.indexOf(secondFlipped));

            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            firstFlipped.startAnimation(shake);
            secondFlipped.startAnimation(shake);

            new Handler().postDelayed(() -> {
                firstFlipped.setVisibility(View.GONE);
                back1.setVisibility(View.VISIBLE);
                secondFlipped.setVisibility(View.GONE);
                back2.setVisibility(View.VISIBLE);
                firstFlipped = null;
                secondFlipped = null;
            }, 500);
        }
    }


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamescreen_hard);

        flipCounterTextView = findViewById(R.id.flipCounter);
        timerText = findViewById(R.id.timerText);
        pauseButton = findViewById(R.id.pauseButton);
        scoreTextView = findViewById(R.id.scoreTextView);
        dbHelper = new ScoreDb(this);


        for (int i = 0; i < frontIds.length; i++) {
            ImageView front = findViewById(frontIds[i]);
            ImageView back = findViewById(backIds[i]);
            frontViews.add(front);
            backViews.add(back);

            // Tag the front with a match ID (e.g. by filename)
            // These should be structured by real match logic
            if (i == 0 || i == 2) front.setTag("copy1");
            if (i == 1 || i == 13) front.setTag("copy2");
            if (i == 3 || i == 18) front.setTag("copy3");
            if (i == 4 || i == 10) front.setTag("copy4");
            if (i == 5 || i == 19) front.setTag("copy5");
            if (i == 6 || i == 14) front.setTag("copy6");
            if (i == 7 || i == 9) front.setTag("copy7");
            if (i == 8 || i == 17) front.setTag("copy8");
            if (i == 11 || i == 12) front.setTag("copy9");
            if (i == 15 || i == 16) front.setTag("copy10");

            cardTags.put(front, (String) front.getTag());

            final int index = i;
            back.setOnClickListener(v -> {
                if (firstFlipped != null && secondFlipped != null) return; // prevent 3rd flip
                ImageView frontCard = frontViews.get(index);
                flipCard(frontCard, back);

                flips++;
                flipCounterTextView.setText(String.valueOf(flips));

                if (firstFlipped == null) {
                    firstFlipped = frontCard;
                } else {
                    secondFlipped = frontCard;

                    // Delay check to allow 2nd card to flip visually
                    new Handler().postDelayed(() -> checkMatch(index), 500);
                }
            });

        }
        // Show all front cards for preview
        for (int i = 0; i < frontViews.size(); i++) {
            frontViews.get(i).setVisibility(View.VISIBLE);
            backViews.get(i).setVisibility(View.GONE);
        }

        pauseButton.setOnClickListener(v -> togglePause());

        handler.postDelayed(() -> {
            hideAllFronts();

            enableCards(true);
            startTimer();
        }, 5000); // preview for 5 seconds

        enableCards(false);
    }

    private void showGameOverPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GamescreenHard.this);
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.popup_gameover, null);
        builder.setView(popupView);
        builder.setCancelable(false); // Cannot dismiss by tapping outside

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

    }

//    private void setupCards() {
//        imageResIds.clear();
//        for (int resId : imageDrawables) {
//            imageResIds.add(resId);
//        }
//        Collections.shuffle(imageResIds);
//
//        for (int i = 0; i < frontViews.size(); i++) {
//            frontViews.get(i).setImageResource(imageResIds.get(i));
//            int index = i;
//            backViews.get(i).setOnClickListener(v -> {
//                if (isPaused || isBusy) return;
//
//                flipCount++;
//                flipCounterTextView.setText("Flips: " + flipCount);
//                //  updateScore();
//
//
//                flipCard(index);
//            });
//
//        }}

    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                if (matchedPairs < frontViews.size() / 2) {
                    showGameOverPopup();
                }
            }
        }.start();
    }

    private void flipCard(int index) {
        if (isBusy) return;

        ImageView front = frontViews.get(index);
        ImageView back = backViews.get(index);

        front.setVisibility(View.VISIBLE);
        back.setVisibility(View.INVISIBLE);

        if (firstCardFront == null) {
            firstCardFront = front;
            firstCardBack = back;
        } else {
            isBusy = true;

            String tag1 = (String) firstCardFront.getTag();
            String tag2 = (String) front.getTag();

            if (tag1 != null && tag1.equals(tag2)) {
                // ✅ It's a match! Leave cards visible and unclickable
                front.setClickable(false);
                firstCardFront.setClickable(false);

                matchedPairs++; // Optional: increment score here

                firstCardFront = null;
                firstCardBack = null;
                isBusy = false;
            } else {
                // ❌ Not a match: flip both back after delay
                Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
                handler.postDelayed(() -> {
                    front.setVisibility(View.INVISIBLE);
                    back.setVisibility(View.VISIBLE);
                    firstCardFront.setVisibility(View.INVISIBLE);
                    firstCardBack.setVisibility(View.VISIBLE);

                    front.startAnimation(shake);
                    firstCardFront.startAnimation(shake);

                    firstCardFront = null;
                    firstCardBack = null;
                    isBusy = false;
                }, 1000);
            }
        }
    }


    private void hideAllFronts() {
        for (ImageView front : frontViews) {
            front.setVisibility(View.INVISIBLE);
        }
        for (ImageView back : backViews) {
            back.setVisibility(View.VISIBLE);
        }
    }

    private void enableCards(boolean enable) {
        for (ImageView back : backViews) {
            back.setEnabled(enable);
        }
    }

    private void togglePause() {
        if (!isPaused) {
            isPaused = true;
            enableCards(false);
            if (countDownTimer != null) countDownTimer.cancel(); // ✅ Stop the timer
            pauseButton.setText(""); // or empty string/icon
        } else {
            isPaused = false;
            enableCards(true);
            startTimer(); // ✅ Continue from remaining time
            pauseButton.setText(""); // or empty string/icon
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}


