package com.example.capybara;

import static androidx.core.content.ContextCompat.startActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GamescreenEasy extends AppCompatActivity {

    private ImageView firstFlipped = null;
    private ImageView secondFlipped = null;
    private int flips = 0;
    private int matchedPairs = 0;
    private final int totalPairs = 6; // 12 cards / 2

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
    private CountDownTimer countdownTimer;
    private long timeLeftInMillis = 30000;
    private int score = 0;
    private int timeRemaining = 30; // updated by timer
    private TextView scoreTextView;
    private CountDownTimer gameTimer;
    private ScoreDb dbHelper;

    private ArrayList<ImageView> frontViews = new ArrayList<>();
    private ArrayList<ImageView> backViews = new ArrayList<>();
    private ArrayList<Integer> imageResIds = new ArrayList<>();

    private ImageView firstCardFront = null;
    private ImageView firstCardBack = null;

    private Handler handler = new Handler();

    Button homebtn;
    Dialog qDialog;
//    public static final String EXTRA_USERNAME = "USERNAME";




// Then when saving score:
// or insertOrUpdateHighScore(username, score);


    private int[] imageDrawables = {
            R.drawable.bisekelcard, R.drawable.card2, R.drawable.guitaristcard,
            R.drawable.card4, R.drawable.card5, R.drawable.card6,
            R.drawable.bisekelcard, R.drawable.card2, R.drawable.guitaristcard,
            R.drawable.card4, R.drawable.card5, R.drawable.card6
    };

    private int[] frontIds = {
            R.id.cardFront1, R.id.cardFront2, R.id.cardFront3, R.id.cardFront4,
            R.id.cardFront5, R.id.cardFront6, R.id.cardFront7, R.id.cardFront8,
            R.id.cardFront9, R.id.cardFront10, R.id.cardFront11, R.id.cardFront12
    };

    private int[] backIds = {
            R.id.cardBack1, R.id.cardBack2, R.id.cardBack3, R.id.cardBack4,
            R.id.cardBack5, R.id.cardBack6, R.id.cardBack7, R.id.cardBack8,
            R.id.cardBack9, R.id.cardBack10, R.id.cardBack11, R.id.cardBack12
    };



    private void showWinPopup() {
        gameWon = true;

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        int finalScore = calculateScore();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

//        dbHelper.insertScore(finalScore, timestamp);


        Dialog congratsDialog = new Dialog(GamescreenEasy.this);
        congratsDialog.setContentView(R.layout.popup_congratsuwon);
        congratsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        congratsDialog.setCancelable(false);

        TextView scoreText = congratsDialog.findViewById(R.id.scoreText);
        if (scoreText != null) {
            scoreText.setText("Score: " + finalScore);
        }

        // ✅ Home button logic
        Button homeBtn = congratsDialog.findViewById(R.id.homeBtn);
        if (homeBtn != null) {
            homeBtn.setOnClickListener(v -> {
                Intent intent = new Intent(GamescreenEasy.this, MainmenuPage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        }

        // ✅ Reset button logic
        Button resetBtn = congratsDialog.findViewById(R.id.resetButton); // Make sure this ID exists in popup_congratsuwon.xml
        if (resetBtn != null) {
            resetBtn.setOnClickListener(v -> {
                Intent intent = new Intent(GamescreenEasy.this, GamescreenEasy.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // closes current game activity
            });
        }

        congratsDialog.show();
    }


    private void updateTimerText() {
        int seconds = (int) (timeLeftInMillis / 1000);
        timerText.setText(String.valueOf(seconds));
    }

    private Map<Integer, String> imageIdToTag = new HashMap<>();

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamescreen_easy);
//        String username = getIntent().getStringExtra(EXTRA_USERNAME);

        flipCounterTextView = findViewById(R.id.flipCounter);
        timerText = findViewById(R.id.timerText);
        pauseButton = findViewById(R.id.pauseButton);
        homebtn = findViewById(R.id.homebtn);
        homebtn.setOnClickListener(v -> showQuitPopup());
        scoreTextView = findViewById(R.id.scoreTextView);
        dbHelper = new ScoreDb(this);
        // Declare and initialize scoreDb
        ScoreDb scoreDb = new ScoreDb(this);



        for (int i = 0; i < frontViews.size(); i++) {
            ImageView front = frontViews.get(i);
            int resId = imageResIds.get(i);
            front.setImageResource(resId);

            String tag = imageIdToTag.get(resId);
            cardTags.put(front, tag); // ✅ dynamic tagging
        }
        imageIdToTag.put(R.drawable.bisekelcard, "bisekel");
        imageIdToTag.put(R.drawable.card2, "card2");
        imageIdToTag.put(R.drawable.guitaristcard, "guitarist");
        imageIdToTag.put(R.drawable.card4, "card4");
        imageIdToTag.put(R.drawable.card5, "card5");
        imageIdToTag.put(R.drawable.card6, "card6");
// Initialize card views
        for (int i = 0; i < frontIds.length; i++) {
            ImageView front = findViewById(frontIds[i]);
            ImageView back = findViewById(backIds[i]);

            frontViews.add(front);
            backViews.add(back);

            final int index = i;
            back.setOnClickListener(v -> {
                if (isBusy || isPaused || front.getVisibility() == View.VISIBLE) return;
                flipCard(index);
                flips++;
                flipCounterTextView.setText(String.valueOf(flips));
            });
        }

        setupCards();
        // Show all front cards for preview
        for (int i = 0; i < frontViews.size(); i++) {
            frontViews.get(i).setVisibility(View.VISIBLE);
            backViews.get(i).setVisibility(View.GONE);
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

            }
        });

        pauseButton.setOnClickListener(v -> togglePause());

        handler.postDelayed(() -> {
            hideAllFronts();

            enableCards(true);
            startTimer();
        }, 5000); // preview for 5 seconds

        enableCards(false);
    }


    private void showGameOverPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GamescreenEasy.this);
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.popup_gameover, null);
        builder.setView(popupView);
        builder.setCancelable(false); // Cannot dismiss by tapping outside

        // ✅ Access buttons from popupView, not AlertDialog class
        Button homebtn = popupView.findViewById(R.id.homebtn);
        Button resetBtn = popupView.findViewById(R.id.resetButton);

        AlertDialog dialog = builder.create(); // create first before setting button actions

        if (homebtn != null) {
            homebtn.setOnClickListener(v -> {
                dialog.dismiss();
                Intent intent = new Intent(GamescreenEasy.this, MainmenuPage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        }

        if (resetBtn != null) {
            resetBtn.setOnClickListener(v -> {
                dialog.dismiss();
                Intent intent = new Intent(GamescreenEasy.this, GamescreenEasy.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        }

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }


    private void setupCards() {
        imageResIds.clear();

        for (int resId : imageDrawables) {
            imageResIds.add(resId);
        }

        Collections.shuffle(imageResIds); // ✅ This is what makes it random

        for (int i = 0; i < frontViews.size(); i++) {
            frontViews.get(i).setImageResource(imageResIds.get(i));
        }

        // Clear any previous flipped/matched states
        firstFlipped = null;
        secondFlipped = null;
        matchedPairs = 0;
        flips = 0;
        flipCounterTextView.setText("0");

        hideAllFronts(); // Reset views: show backs
    }


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
        ScoreDb scoreDb = new ScoreDb(this);
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        ImageView front = frontViews.get(index);
        ImageView back = backViews.get(index);

        front.setVisibility(View.VISIBLE);
        back.setVisibility(View.INVISIBLE);

        if (firstCardFront == null) {
            firstCardFront = front;
            firstCardBack = back;
        } else {
            isBusy = true;

            boolean isMatch = firstCardFront.getDrawable().getConstantState()
                    .equals(front.getDrawable().getConstantState());

            if (isMatch) {
                matchedPairs++;
                firstCardFront = null;
                firstCardBack = null;
                isBusy = false;

                if (matchedPairs == totalPairs) {
                    int score = calculateScore(); // however you're computing score
                    scoreDb.insertScore(score, timestamp);

                    showWinPopup(); // 🎉 Show the popup when all matched
                }

            } else {
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

    private int calculateScore() {
        int timeBonus = (int) (timeLeftInMillis / 1000); // remaining seconds
        int flipPenalty = flips;
        return (timeBonus * 10) - (flipPenalty * 2); // example formula
    }
    private void showQuitPopup() {
        qDialog = new Dialog(GamescreenEasy.this);
        qDialog.setContentView(R.layout.popup_quitgame);
        qDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        qDialog.setCancelable(false);

        Button confirmBtn = qDialog.findViewById(R.id.confirmbtn);
        Button cancelBtn = qDialog.findViewById(R.id.cancelbtn);

        confirmBtn.setOnClickListener(v -> {
            Intent intent = new Intent(GamescreenEasy.this, MainmenuPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            qDialog.dismiss();
        });

        cancelBtn.setOnClickListener(v -> {
            qDialog.dismiss();
            // Resume game logic if paused
            if (isPaused) {
                togglePause();  // Unpause the game
            }
        });

        // Pause the game when the dialog shows
        if (!isPaused) {
            togglePause(); // Pause the game
        }

        qDialog.show();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countdownTimer != null) countdownTimer.cancel();
    }
}

