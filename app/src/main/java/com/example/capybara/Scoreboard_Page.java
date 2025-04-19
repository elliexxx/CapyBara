package com.example.capybara;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Scoreboard_Page extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ScoreAdapter adapter;
    private ScoreDb dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard_page);

        recyclerView = findViewById(R.id.recycler_scores);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new ScoreDb(this);
        List<Score> scores = dbHelper.getAllScores();

        adapter = new ScoreAdapter(scores);
        recyclerView.setAdapter(adapter);
    }
}


