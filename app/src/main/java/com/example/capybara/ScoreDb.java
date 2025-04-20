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

    // Insert a new score into the scoreboard table
    public void insertScore(int score, String timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SCORE, score);
        values.put(COLUMN_TIMESTAMP, timestamp);

        // Insert into the table
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // Get all scores (ordered by score in descending order)
    public List<Score> getAllScores() {
        List<Score> scoreList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Query the database for scores, ordered by score descending
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_SCORE, COLUMN_TIMESTAMP},
                null, null, null, null, COLUMN_SCORE + " DESC");

        if (cursor != null) {
            // Check if the cursor has data
            if (cursor.moveToFirst()) {
                do {
                    // Retrieve the score and timestamp from the cursor
                    @SuppressLint("Range") int score = cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE));
                    @SuppressLint("Range") String timestamp = cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP));

                    // Ensure that we got valid data before creating the Score object
                    if (timestamp != null) {
                        Score scoreItem = new Score(score, timestamp); // Assuming Score constructor takes score and timestamp
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
