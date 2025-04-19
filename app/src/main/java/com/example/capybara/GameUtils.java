package com.example.capybara;

import android.content.Context;
import android.content.SharedPreferences;

public class GameUtils {
    public static final String PREFS_NAME = "MemoryGamePrefs";

    public static boolean isLevelUnlocked(Context context, String level) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (level.equals("Easy")) return true;
        if (level.equals("Medium")) return prefs.getBoolean("MediumUnlocked", false);
        if (level.equals("Hard")) return prefs.getBoolean("HardUnlocked", false);
        return false;
    }

    public static void unlockNextLevel(Context context, String currentLevel) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        if (currentLevel.equals("Easy")) {
            editor.putBoolean("MediumUnlocked", true);
        } else if (currentLevel.equals("Medium")) {
            editor.putBoolean("HardUnlocked", true);
        }
        editor.apply();
    }

    public static void updateScoreboard(Context context, String difficulty, int timeInSeconds) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int bestTime = prefs.getInt(difficulty + "_bestTime", Integer.MAX_VALUE);

        if (timeInSeconds < bestTime) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(difficulty + "_bestTime", timeInSeconds);
            editor.apply();
        }
    }
}
