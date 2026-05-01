package com.example.tictactoe;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class GameActivity extends AppCompatActivity {

    private GridLayout gridBoard;
    private TextView tvTurn;
    private TextView tvMode;
    private TextView tvSize;

    private Button[][] buttons;
    private GameLogic game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gridBoard = findViewById(R.id.gridBoard);
        tvTurn = findViewById(R.id.tvTurn);
        tvMode = findViewById(R.id.tvMode);
        tvSize = findViewById(R.id.tvSize);
        Button btnNewGame = findViewById(R.id.btnNewGame);
        Button btnMainMenu = findViewById(R.id.btnMainMenu);

        String mode = getIntent().getStringExtra("mode");
        int size = getIntent().getIntExtra("size", 3);
        game = new GameLogic(size, mode);

        createBoardUI();
        updateUI();

        btnNewGame.setOnClickListener(v -> {
            game.reset();
            updateUI();
        });
        
        btnMainMenu.setOnClickListener(v -> finish());
    }

    private void createBoardUI() {
        int size = game.getSize();
        buttons = new Button[size][size];
        
        gridBoard.removeAllViews();
        gridBoard.setColumnCount(size);
        gridBoard.setRowCount(size);

        int boardSize = getResources().getDisplayMetrics().widthPixels - dpToPx(32);
        int totalGaps = dpToPx(4) * (size - 1);
        int cellSize = (boardSize - totalGaps) / size;

        ConstraintLayout.LayoutParams boardParams = (ConstraintLayout.LayoutParams) gridBoard.getLayoutParams();
        boardParams.width = boardSize;
        boardParams.height = boardSize;
        gridBoard.setLayoutParams(boardParams);

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Button cell = new Button(this);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = cellSize;
                params.height = cellSize;
                params.columnSpec = GridLayout.spec(col);
                params.rowSpec = GridLayout.spec(row);
                params.setMargins(0, 0, 0, 0);
                cell.setLayoutParams(params);

                cell.setTextSize(TypedValue.COMPLEX_UNIT_PX, cellSize * 0.4f);
                cell.setGravity(Gravity.CENTER);
                cell.setPadding(0, 0, 0, 0);
                cell.setMinWidth(0);
                cell.setMinHeight(0);
                cell.setMinimumWidth(0);
                cell.setMinimumHeight(0);

                final int r = row;
                final int c = col;
                cell.setOnClickListener(v -> {
                    if (game.makeMove(r, c)) {
                        updateUI();
                        if (game.isGameOver()) {
                            Toast.makeText(this, game.getEndMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                buttons[row][col] = cell;
                gridBoard.addView(cell);
            }
        }
    }

    private void updateUI() {
        int size = game.getSize();
        char[][] board = game.getBoard();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                char c = board[row][col];
                buttons[row][col].setText(c == '\0' ? "" : String.valueOf(c));
                buttons[row][col].setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.cell_bg_color, getTheme())));
                if (c == 'X') {
                    buttons[row][col].setTextColor(getResources().getColor(R.color.x_color, getTheme()));
                } else if (c == 'O') {
                    buttons[row][col].setTextColor(getResources().getColor(R.color.o_color, getTheme()));
                }
            }
        }

        if (game.isGameOver() && game.getWinningCells() != null) {
            for (int[] cell : game.getWinningCells()) {
                buttons[cell[0]][cell[1]].setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.win_color, getTheme())));
            }
        }

        if (game.isGameOver()) {
            tvTurn.setText(game.getEndMessage());
            tvMode.setText("");
            tvSize.setText("");
        } else {
            tvTurn.setText("Ход: " + game.getCurrentPlayer());
            tvMode.setText(game.getMode());
            tvSize.setText(size + "x" + size);
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}