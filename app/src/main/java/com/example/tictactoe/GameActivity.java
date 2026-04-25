package com.example.tictactoe;

import android.os.Bundle;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private GridLayout gridBoard;
    private TextView tvTurn;
    private TextView tvMode;
    private TextView tvSize;
    private int size;
    private String mode;
    private int winLength;

    private char[][] board;
    private Button[][] buttons;

    private char currentPlayer = 'X';
    private boolean gameOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.gameRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(dpToPx(16), dpToPx(16) + systemBars.top, dpToPx(16), dpToPx(16));
            return insets;
        });

        gridBoard = findViewById(R.id.gridBoard);
        tvTurn = findViewById(R.id.tvTurn);
        tvMode = findViewById(R.id.tvMode);
        tvSize = findViewById(R.id.tvSize);
        Button btnNewGame = findViewById(R.id.btnNewGame);
        Button btnMainMenu = findViewById(R.id.btnMainMenu);

        mode = getIntent().getStringExtra("mode");
        size = getIntent().getIntExtra("size", 3);
        winLength = (size == 5) ? 4 : 3;

        startNewGame();

        btnNewGame.setOnClickListener(v -> startNewGame());
        btnMainMenu.setOnClickListener(v -> finish());
    }

    private void startNewGame() {
        board = new char[size][size];
        buttons = new Button[size][size];
        currentPlayer = 'X';
        gameOver = false;

        updateTopInfo();

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

                if (size == 3) {
                    cell.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                } else if (size == 4) {
                    cell.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                } else {
                    cell.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                }
                cell.setGravity(Gravity.CENTER);
                cell.setPadding(0, 0, 0, 0);
                cell.setMinWidth(0);
                cell.setMinHeight(0);
                cell.setMinimumWidth(0);
                cell.setMinimumHeight(0);

                final int r = row;
                final int c = col;
                cell.setOnClickListener(v -> onCellClick(r, c));

                buttons[row][col] = cell;
                gridBoard.addView(cell);
            }
        }
    }

    private void onCellClick(int row, int col) {
        if (gameOver) return;
        if (board[row][col] != '\0') return;

        makeMove(row, col, currentPlayer);

        if (gameOver) return;

        if ("PvE".equals(mode) && currentPlayer == 'O') {
            makeComputerMove();
        }
    }

    private void makeMove(int row, int col, char player) {
        board[row][col] = player;
        buttons[row][col].setText(String.valueOf(player));

        List<int[]> winCells = getWinningCells(player);
        if (winCells != null) {
            gameOver = true;
            String text = player == 'X' ? "Победили крестики" : "Победили нолики";
            highlightWinningCells(winCells);
            tvTurn.setText(text);
            tvMode.setText("");
            tvSize.setText("");
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
            return;
        }

        if (isBoardFull()) {
            gameOver = true;
            tvTurn.setText("Ничья");
            tvMode.setText("");
            tvSize.setText("");
            Toast.makeText(this, "Ничья", Toast.LENGTH_SHORT).show();
            return;
        }

        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
        updateTopInfo();
    }

    private void makeComputerMove() {
        List<int[]> freeCells = new ArrayList<>();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board[row][col] == '\0') {
                    freeCells.add(new int[]{row, col});
                }
            }
        }

        if (freeCells.isEmpty()) return;

        Random random = new Random();
        int[] move = freeCells.get(random.nextInt(freeCells.size()));
        makeMove(move[0], move[1], currentPlayer);
    }

    private List<int[]> getWinningCells(char player) {
        int[][] directions = {
                {0, 1},
                {1, 0},
                {1, 1},
                {1, -1}
        };

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board[row][col] != player) {
                    continue;
                }

                for (int[] direction : directions) {
                    List<int[]> cells = new ArrayList<>();
                    boolean win = true;

                    for (int step = 0; step < winLength; step++) {
                        int newRow = row + direction[0] * step;
                        int newCol = col + direction[1] * step;

                        if (newRow < 0 || newRow >= size || newCol < 0 || newCol >= size) {
                            win = false;
                            break;
                        }

                        if (board[newRow][newCol] != player) {
                            win = false;
                            break;
                        }

                        cells.add(new int[]{newRow, newCol});
                    }

                    if (win) {
                        return cells;
                    }
                }
            }
        }

        return null;
    }

    private void highlightWinningCells(List<int[]> cells) {
        for (int[] cell : cells) {
            int row = cell[0];
            int col = cell[1];
            buttons[row][col].setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#66BB6A")));
        }
    }

    private boolean isBoardFull() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board[row][col] == '\0') {
                    return false;
                }
            }
        }
        return true;
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private void updateTopInfo() {
        tvTurn.setText("Ход: " + currentPlayer);
        tvMode.setText(mode);
        tvSize.setText(size + "x" + size);
    }
}
