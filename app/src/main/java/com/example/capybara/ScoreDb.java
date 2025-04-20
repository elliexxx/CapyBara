package com.example.capybara;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ScoreDb extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "capybara_game.db"; // Example DB name
    public static final int DATABASE_VERSION = 1; // Example version number
    // Table name and columns
    public static final String TABLE_NAME = "scoreboard";
    public static final String COLUMN_PLAYER_NAME = "username";
    public static final String COLUMN_SCORE = "score";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    // Constructor
    public ScoreDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // This is the code to create the table in the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SCORE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_PLAYER_NAME + " TEXT,"
                + COLUMN_SCORE + " INTEGER,"
                + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";
        db.execSQL(CREATE_SCORE_TABLE);
    }

    // This method handles upgrading the database (if the schema changes)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert score into the scoreboard table
    public void insertScore(String username, int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PLAYER_NAME, username);
        values.put(COLUMN_SCORE, score);
        // Insert into the correct table
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // Insert or update high score if the new score is higher
    public void insertOrUpdateHighScore(String username, int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(" + COLUMN_SCORE + ") FROM " + TABLE_NAME + " WHERE " + COLUMN_PLAYER_NAME + " = ?", new String[]{username});

        if (cursor != null && cursor.moveToFirst()) {
            int currentHighScore = cursor.getInt(0);

            // Only update if the new score is higher than the current high score
            if (score > currentHighScore) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_PLAYER_NAME, username);
                values.put(COLUMN_SCORE, score);
                db.insert(TABLE_NAME, null, values);
            }
        } else {
            // First score entry for this player
            ContentValues values = new ContentValues();
            values.put(COLUMN_PLAYER_NAME, username);
            values.put(COLUMN_SCORE, score);
            db.insert(TABLE_NAME, null, values);
        }

        if (cursor != null) {
            cursor.close(); // Make sure to close the cursor after use
        }
        db.close();
    }

    // Get all scores (highest first)
    public List<Score> getAllScores() {
        List<Score> scoreList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query the database for player scores, ordered by score descending
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_PLAYER_NAME, COLUMN_SCORE, COLUMN_TIMESTAMP},
                null, null, null, null, COLUMN_SCORE + " DESC");

        if (cursor != null) {
            // Check if the cursor has data
            if (cursor.moveToFirst()) {
                do {
                    // Retrieve the player name, score, and timestamp from the cursor
                    @SuppressLint("Range") String username = cursor.getString(cursor.getColumnIndex(COLUMN_PLAYER_NAME));
                    @SuppressLint("Range") int score = cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE));
                    @SuppressLint("Range") String timestamp = cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP));

                    // Ensure that we got valid data before creating the Score object
                    if (username != null && !username.isEmpty()) {
                        Score scoreItem = new Score(username, score); // Assuming Score constructor takes username, score, and timestamp
                        scoreList.add(scoreItem);
                    }
                } while (cursor.moveToNext());
            }

            // Close the cursor to prevent memory leaks
            cursor.close();
        }

        db.close(); // Close the database connection
        return scoreList;
    }
}