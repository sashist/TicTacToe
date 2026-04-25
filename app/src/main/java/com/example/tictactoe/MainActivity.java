package com.example.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner modeSpinner = findViewById(R.id.spinnerMode);
        Spinner sizeSpinner = findViewById(R.id.spinnerSize);
        Button startButton = findViewById(R.id.btnStartGame);

        String[] modes = {"PvP", "PvE"};
        String[] sizes = {"3x3", "4x4", "5x5"};

        ArrayAdapter<String> modeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, modes);
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSpinner.setAdapter(modeAdapter);

        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sizes);
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sizeSpinner.setAdapter(sizeAdapter);

        startButton.setOnClickListener(v -> {
            String mode = modeSpinner.getSelectedItem().toString();
            String sizeText = sizeSpinner.getSelectedItem().toString();
            int size = Integer.parseInt(sizeText.substring(0, 1));

            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra("mode", mode);
            intent.putExtra("size", size);
            startActivity(intent);
        });
    }
}
