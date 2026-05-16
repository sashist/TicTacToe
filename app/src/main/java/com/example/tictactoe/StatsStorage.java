package com.example.tictactoe;

import android.content.Context;
import android.content.SharedPreferences;

public class StatsStorage {

    private static final String PREFS_NAME = "game_stats";
    private static final String KEY_WINS = "wins";
    private static final String KEY_LOSSES = "losses";
    private static final String KEY_DRAWS = "draws";

    private final SharedPreferences prefs;

    public StatsStorage(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public int getWins() {
        return prefs.getInt(KEY_WINS, 0);
    }

    public int getLosses() {
        return prefs.getInt(KEY_LOSSES, 0);
    }

    public int getDraws() {
        return prefs.getInt(KEY_DRAWS, 0);
    }

    public void recordWin() {
        prefs.edit().putInt(KEY_WINS, getWins() + 1).apply();
    }

    public void recordLoss() {
        prefs.edit().putInt(KEY_LOSSES, getLosses() + 1).apply();
    }

    public void recordDraw() {
        prefs.edit().putInt(KEY_DRAWS, getDraws() + 1).apply();
    }

}
