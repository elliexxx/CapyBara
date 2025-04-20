package com.example.capybara;

public class Score {
    private String playerName;
    private int score;


    public Score(String playerName, int score, String timestamp) {
        this.playerName = playerName;
        this.score = score;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;

    }
}
