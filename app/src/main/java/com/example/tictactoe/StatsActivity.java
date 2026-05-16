package com.example.tictactoe;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StatsActivity extends AppCompatActivity {

    private TextView tvWins;
    private TextView tvLosses;
    private TextView tvDraws;
    private StatsStorage statsStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        tvWins = findViewById(R.id.tvWins);
        tvLosses = findViewById(R.id.tvLosses);
        tvDraws = findViewById(R.id.tvDraws);
        Button btnBack = findViewById(R.id.btnBack);

        statsStorage = new StatsStorage(this);
        renderStats();

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderStats();
    }

    private void renderStats() {
        tvWins.setText("Победы: " + statsStorage.getWins());
        tvLosses.setText("Поражения: " + statsStorage.getLosses());
        tvDraws.setText("Ничьи: " + statsStorage.getDraws());
    }
}
