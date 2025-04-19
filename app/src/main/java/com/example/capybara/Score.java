package com.example.capybara;

public class Score {
    private String playerName;
    private int score;
    private String timestamp;

    public Score(String playerName, int score, String timestamp) {
        this.playerName = playerName;
        this.score = score;
        this.timestamp = timestamp;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public String getTimestamp() {
        return timestamp;
    }
}

