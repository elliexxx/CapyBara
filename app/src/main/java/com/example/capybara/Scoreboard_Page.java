package com.example.capybara;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Scoreboard_Page extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ScoreAdapter adapter;
    private ScoreDb dbHelper;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard_page);

        recyclerView = findViewById(R.id.recycler_scores);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the ScoreDb helper
        dbHelper = new ScoreDb(this);

        // Initialize the ExecutorService
        executorService = Executors.newSingleThreadExecutor();

        // Fetch scores from the database in the background
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                List<Score> scores = dbHelper.getAllScores(); // Fetch scores in the background

                // Update the RecyclerView on the main thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new ScoreAdapter(Scoreboard_Page.this, scores);
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        });
    }
}
