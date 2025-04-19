package com.example.capybara;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ScoreDb extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ScoreDb.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "scores";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PLAYER_NAME = "player_name";
    public static final String COLUMN_SCORE = "score";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    public ScoreDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PLAYER_NAME + " TEXT, "
                + COLUMN_SCORE + " INTEGER, "
                + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // ❌ FIXED insertScore: was inserting into wrong table
    public void insertScore(String username, int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PLAYER_NAME, username);
        values.put(COLUMN_SCORE, score);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // ✅ NEW METHOD: insert or update only if score is higher
    public void insertOrUpdateHighScore(String username, int score) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT MAX(" + COLUMN_SCORE + ") FROM " + TABLE_NAME + " WHERE " + COLUMN_PLAYER_NAME + " = ?", new String[]{username});
        if (cursor.moveToFirst()) {
            int currentHighScore = cursor.getInt(0);

            if (score > currentHighScore) {
                // Insert new high score
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

        cursor.close();
        db.close();
    }

    // Get all scores (highest first)
    public List<Score> getAllScores() {
        List<Score> scoreList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, COLUMN_SCORE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLAYER_NAME));
                int score = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP));
                scoreList.add(new Score(name, score, timestamp));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return scoreList;
    }
}
