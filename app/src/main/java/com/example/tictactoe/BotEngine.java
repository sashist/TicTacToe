package com.example.tictactoe;

import java.util.ArrayList;
import java.util.List;

public final class BotEngine {

    private static final char AI = 'O';
    private static final char HUMAN = 'X';
    private static final int WIN_SCORE = 100_000;
    private static final int HEURISTIC_DEPTH = 2;

    private BotEngine() {
    }

    public static int[] chooseMove(char[][] board, int winLength) {
        int size = board.length;
        if (size == 3 && winLength == 3) {
            return chooseMoveMinimax(board, winLength);
        }
        return chooseMoveHeuristic(board, winLength);
    }

    private static int[] chooseMoveMinimax(char[][] board, int winLength) {
        List<int[]> freeCells = getFreeCells(board);
        int[] bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        for (int[] move : freeCells) {
            board[move[0]][move[1]] = AI;
            int score = minimax(board, false, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, winLength);
            board[move[0]][move[1]] = '\0';

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private static int minimax(char[][] board, boolean maximizing, int depth, int alpha, int beta, int winLength) {
        char winner = getWinner(board, winLength);
        if (winner == AI) return 1_000 - depth;
        if (winner == HUMAN) return depth - 1_000;
        if (winner == 'D') return 0;

        List<int[]> freeCells = getFreeCells(board);
        if (maximizing) {
            int best = Integer.MIN_VALUE;
            for (int[] move : freeCells) {
                board[move[0]][move[1]] = AI;
                int score = minimax(board, false, depth + 1, alpha, beta, winLength);
                board[move[0]][move[1]] = '\0';

                best = Math.max(best, score);
                alpha = Math.max(alpha, best);
                if (beta <= alpha) break;
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int[] move : freeCells) {
                board[move[0]][move[1]] = HUMAN;
                int score = minimax(board, true, depth + 1, alpha, beta, winLength);
                board[move[0]][move[1]] = '\0';

                best = Math.min(best, score);
                beta = Math.min(beta, best);
                if (beta <= alpha) break;
            }
            return best;
        }
    }

    private static int[] chooseMoveHeuristic(char[][] board, int winLength) {
        int[] winningMove = findImmediateWinningMove(board, AI, winLength);
        if (winningMove != null) return winningMove;

        int[] blockingMove = findImmediateWinningMove(board, HUMAN, winLength);
        if (blockingMove != null) return blockingMove;

        List<int[]> candidates = getCandidateMoves(board);
        int[] bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        for (int[] move : candidates) {
            board[move[0]][move[1]] = AI;
            int score = minimaxHeuristic(board, HEURISTIC_DEPTH - 1, false, Integer.MIN_VALUE, Integer.MAX_VALUE, winLength);
            score += getPositionalBonus(move[0], move[1], board.length);
            board[move[0]][move[1]] = '\0';

            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private static int minimaxHeuristic(char[][] board, int depth, boolean maximizing, int alpha, int beta, int winLength) {
        char winner = getWinner(board, winLength);
        if (winner == AI) return WIN_SCORE + depth;
        if (winner == HUMAN) return -WIN_SCORE - depth;
        if (winner == 'D') return 0;
        if (depth == 0) return evaluateBoard(board, winLength);

        List<int[]> moves = getCandidateMoves(board);
        if (moves.isEmpty()) return evaluateBoard(board, winLength);

        if (maximizing) {
            int best = Integer.MIN_VALUE;
            for (int[] move : moves) {
                board[move[0]][move[1]] = AI;
                int score = minimaxHeuristic(board, depth - 1, false, alpha, beta, winLength);
                board[move[0]][move[1]] = '\0';

                best = Math.max(best, score);
                alpha = Math.max(alpha, best);
                if (beta <= alpha) break;
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int[] move : moves) {
                board[move[0]][move[1]] = HUMAN;
                int score = minimaxHeuristic(board, depth - 1, true, alpha, beta, winLength);
                board[move[0]][move[1]] = '\0';

                best = Math.min(best, score);
                beta = Math.min(beta, best);
                if (beta <= alpha) break;
            }
            return best;
        }
    }

    private static int evaluateBoard(char[][] board, int winLength) {
        int size = board.length;
        int score = 0;
        int[][] directions = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                for (int[] direction : directions) {
                    int endRow = row + direction[0] * (winLength - 1);
                    int endCol = col + direction[1] * (winLength - 1);
                    if (endRow < 0 || endRow >= size || endCol < 0 || endCol >= size) continue;

                    int aiCount = 0;
                    int humanCount = 0;
                    for (int step = 0; step < winLength; step++) {
                        char cell = board[row + direction[0] * step][col + direction[1] * step];
                        if (cell == AI) aiCount++;
                        if (cell == HUMAN) humanCount++;
                    }

                    score += evaluateLine(aiCount, humanCount, winLength);
                }
            }
        }

        return score;
    }

    private static int evaluateLine(int aiCount, int humanCount, int winLength) {
        if (aiCount > 0 && humanCount > 0) return 0;
        if (aiCount == 0 && humanCount == 0) return 0;
        if (aiCount == winLength) return WIN_SCORE;
        if (humanCount == winLength) return -WIN_SCORE;

        if (aiCount > 0) return lineWeight(aiCount);
        return -lineWeight(humanCount);
    }

    private static int lineWeight(int marks) {
        int value = 1;
        for (int i = 0; i < marks; i++) value *= 10;
        return value;
    }

    private static int[] findImmediateWinningMove(char[][] board, char player, int winLength) {
        List<int[]> moves = getCandidateMoves(board);
        for (int[] move : moves) {
            board[move[0]][move[1]] = player;
            boolean wins = hasWon(board, player, winLength);
            board[move[0]][move[1]] = '\0';
            if (wins) return move;
        }
        return null;
    }

    private static int getPositionalBonus(int row, int col, int size) {
        int centerLeft = (size - 1) / 2;
        int centerRight = size / 2;
        int distanceToCenter = Math.min(
                Math.abs(row - centerLeft) + Math.abs(col - centerLeft),
                Math.abs(row - centerRight) + Math.abs(col - centerRight)
        );
        return (size * 2) - distanceToCenter;
    }

    private static char getWinner(char[][] board, int winLength) {
        if (hasWon(board, AI, winLength)) return AI;
        if (hasWon(board, HUMAN, winLength)) return HUMAN;
        if (isBoardFull(board)) return 'D';
        return '\0';
    }

    private static boolean hasWon(char[][] board, char player, int winLength) {
        int size = board.length;
        int[][] directions = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board[row][col] != player) continue;

                for (int[] direction : directions) {
                    boolean win = true;
                    for (int step = 0; step < winLength; step++) {
                        int newRow = row + direction[0] * step;
                        int newCol = col + direction[1] * step;
                        if (newRow < 0 || newRow >= size || newCol < 0 || newCol >= size || board[newRow][newCol] != player) {
                            win = false;
                            break;
                        }
                    }
                    if (win) return true;
                }
            }
        }
        return false;
    }

    private static List<int[]> getFreeCells(char[][] board) {
        List<int[]> freeCells = new ArrayList<>();
        int size = board.length;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board[row][col] == '\0') {
                    freeCells.add(new int[]{row, col});
                }
            }
        }
        return freeCells;
    }

    private static List<int[]> getCandidateMoves(char[][] board) {
        List<int[]> candidates = new ArrayList<>();
        int size = board.length;
        boolean hasMarks = false;

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board[row][col] != '\0') {
                    hasMarks = true;
                    break;
                }
            }
            if (hasMarks) break;
        }

        if (!hasMarks) return getFreeCells(board);

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board[row][col] != '\0') continue;
                if (hasNeighbor(board, row, col)) {
                    candidates.add(new int[]{row, col});
                }
            }
        }

        return candidates.isEmpty() ? getFreeCells(board) : candidates;
    }

    private static boolean hasNeighbor(char[][] board, int row, int col) {
        int size = board.length;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int nr = row + dr;
                int nc = col + dc;
                if (nr < 0 || nr >= size || nc < 0 || nc >= size) continue;
                if (board[nr][nc] != '\0') return true;
            }
        }
        return false;
    }

    private static boolean isBoardFull(char[][] board) {
        int size = board.length;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board[row][col] == '\0') return false;
            }
        }
        return true;
    }
}
