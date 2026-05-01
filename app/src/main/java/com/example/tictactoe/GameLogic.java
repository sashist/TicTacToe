package com.example.tictactoe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameLogic {

    private int size;
    private String mode;
    private int winLength;

    private char[][] board;
    private char currentPlayer;
    private boolean gameOver;

    private List<int[]> winningCells;
    private String endMessage;

    public GameLogic(int size, String mode) {
        this.size = size;
        this.mode = mode;
        this.winLength = (size == 5) ? 4 : 3;
        reset();
    }

    public void reset() {
        board = new char[size][size];
        currentPlayer = 'X';
        gameOver = false;
        winningCells = null;
        endMessage = null;
    }

    public boolean makeMove(int row, int col) {
        if (gameOver || board[row][col] != '\0') return false;

        board[row][col] = currentPlayer;

        winningCells = getWinningCells(currentPlayer);
        if (winningCells != null) {
            gameOver = true;
            endMessage = (currentPlayer == 'X') ? "Победили крестики" : "Победили нолики";
            return true;
        }

        if (isBoardFull()) {
            gameOver = true;
            endMessage = "Ничья";
            return true;
        }

        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';

        if ("PvE".equals(mode) && currentPlayer == 'O') {
            makeComputerMove();
        }

        return true;
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

        board[move[0]][move[1]] = currentPlayer;

        winningCells = getWinningCells(currentPlayer);
        if (winningCells != null) {
            gameOver = true;
            endMessage = "Победили нолики";
            return;
        }

        if (isBoardFull()) {
            gameOver = true;
            endMessage = "Ничья";
            return;
        }

        currentPlayer = 'X';
    }

    private List<int[]> getWinningCells(char player) {
        int[][] directions = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board[row][col] != player) continue;

                for (int[] direction : directions) {
                    List<int[]> cells = new ArrayList<>();
                    boolean win = true;

                    for (int step = 0; step < winLength; step++) {
                        int newRow = row + direction[0] * step;
                        int newCol = col + direction[1] * step;

                        if (newRow < 0 || newRow >= size || newCol < 0 || newCol >= size || board[newRow][newCol] != player) {
                            win = false;
                            break;
                        }
                        cells.add(new int[]{newRow, newCol});
                    }

                    if (win) return cells;
                }
            }
        }
        return null;
    }

    private boolean isBoardFull() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board[row][col] == '\0') return false;
            }
        }
        return true;
    }

    public int getSize() { return size; }
    public String getMode() { return mode; }
    public char[][] getBoard() { return board; }
    public char getCurrentPlayer() { return currentPlayer; }
    public boolean isGameOver() { return gameOver; }
    public List<int[]> getWinningCells() { return winningCells; }
    public String getEndMessage() { return endMessage; }
}
