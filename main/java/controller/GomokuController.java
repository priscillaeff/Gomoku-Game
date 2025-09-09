//GomokuController class handles game rules, move history, win conditions,
//and UI updates while maintaining game state
package controller;

import logic.Board;
import logic.Move;
import view.GomokuGameFX;
import view.InvalidMove;
import view.StatusBar;
import java.util.Stack;

public class GomokuController {
    private final Board board;
    private final StatusBar statusBar;
    private int currentPlayer;
    private boolean gameOver;
    private int blackMoves;
    private int whiteMoves;
    private int maxBlackRow = 0;
    private int maxWhiteRow = 0;
    private InvalidMove invalidMove;
    private final Stack<Move> moveHistory = new Stack<>(); //stores history for undo
    private final Stack<Move> redoHistory = new Stack<>(); //stores undone moves for redo
    private final GomokuGameFX gameView;

    public GomokuController(Board board, StatusBar statusBar, InvalidMove invalidMove, GomokuGameFX gameView) {
        this.board = board;
        this.statusBar = statusBar;
        this.currentPlayer = 1; //black starts first (1 = black, 2 = white)
        this.gameOver = false;
        this.invalidMove = invalidMove;
        this.gameView = gameView;
    }

    public Board getBoard() {
        return this.board;
    }

    public int getBlackMoves() {
        return blackMoves;
    }

    public int getWhiteMoves() {
        return whiteMoves;
    }

    //make a move at a given row and column
    public boolean makeMove(int row, int col) {
        //reject move is game over or invalid position or already occupied
        if (gameOver || !board.isValidPos(row, col) || board.getCell(row, col) != 0) {
            return false;
        }

        invalidMove.hideWarning(); //hide previous warning if any

        //place stone and track move
        board.placeStone(row, col, currentPlayer);
        moveHistory.push(new Move(row, col, currentPlayer));
        redoHistory.clear(); //clear redo stack since a new move is made

        //update move count for current player
        if (currentPlayer == 1) {
            blackMoves++;
        } else {
            whiteMoves++;
        }

        //check if the move wins the game
        if (board.checkWin(row, col, currentPlayer)) {
            gameOver = true;
            statusBar.updateStatus(currentPlayer, true, currentPlayer); //show winner
            gameView.animateWinningStones(); //play win animation
        } else if (board.isFull()) {
            gameOver = true;
            statusBar.updateStatus(currentPlayer, true, 0); //it's a draw
            gameView.animateWinningStones();
        } else {
            updateMax(row, col); //update the longest row of stones stats
            switchPlayer(); //pass turn to next player
            statusBar.updateStatus(currentPlayer, false, 0); //update UI
        }
        return true;
    }

    //update max length for current player
    private void updateMax(int row, int col) {
        int currentMax = board.getMaxLength(row, col, currentPlayer);

        if (currentPlayer == 1) {  // Black player
            if (currentMax > maxBlackRow) {
                maxBlackRow = currentMax;
            }
        } else {  // White player
            if (currentMax > maxWhiteRow) {
                maxWhiteRow = currentMax;
            }
        }
    }

    public int getMaxBlackRow() {
        return maxBlackRow;
    }

    public int getMaxWhiteRow() {
        return maxWhiteRow;
    }

    //switch black and white
    public void switchPlayer() {
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    //return winner (1 or 2), or 0 if no winner
    public int getWinner() {
        return gameOver ? currentPlayer : 0;
    }

    //UNDO
    public boolean undoMove() {
        if (moveHistory.isEmpty() || gameOver) {
            return false;
        }

        Move lastMove = moveHistory.pop();
        board.removeStone(lastMove.getRow(), lastMove.getCol()); //remove stone
        redoHistory.push(lastMove); //push to redo stack

        //update move count
        if (lastMove.getPlayer() == 1) {
            blackMoves--;
        } else {
            whiteMoves--;
        }

        //recalculate longest line stats
        maxBlackRow = recalculateMaxRow(1);
        maxWhiteRow = recalculateMaxRow(2);

        //switch player back to previous players
        switchPlayer();
        statusBar.updateStatus(currentPlayer, false, 0);
        return true;
    }

    //REDO
    public boolean redoMove() {
        if (redoHistory.isEmpty() || gameOver) {
            return false;
        }

        Move nextMove = redoHistory.pop();
        board.placeStone(nextMove.getRow(), nextMove.getCol(), nextMove.getPlayer());
        moveHistory.push(nextMove); //track the move again

        //update move count
        if (nextMove.getPlayer() == 1) {
            blackMoves++;
        } else {
            whiteMoves++;
        }

        //recalculate longest line stats
        maxBlackRow = recalculateMaxRow(1);
        maxWhiteRow = recalculateMaxRow(2);

        //switch to next player after redo
        currentPlayer = (nextMove.getPlayer() == 1) ? 2 : 1;
        statusBar.updateStatus(currentPlayer, false, 0);
        return true;
    }

    //check entire board to find max continuous line
    public int recalculateMaxRow(int player) {
        int maxRow = 0;
        for (int row = 0; row < board.getBoardSize(); row++) {
            for (int col = 0; col < board.getBoardSize(); col++) {
                if (board.getCell(row, col) == player) {
                    int currentMax = board.getMaxLength(row, col, player);
                    if (currentMax > maxRow) {
                        maxRow = currentMax;
                    }
                }
            }
        }
        return maxRow;
    }

    //reset game for a new match
    public void resetGame() {
        board.clearBoard();  //clear board
        blackMoves = 0;  //reset move counter
        whiteMoves = 0;
        maxBlackRow = 0;
        maxWhiteRow = 0;
        gameOver = false;
        currentPlayer = 1;  //reset to black's turn
        moveHistory.clear(); //clear history
        redoHistory.clear();
        statusBar.updateStatus(currentPlayer, false, 0); // Reset status bar
        invalidMove.hideWarning(); //hide any warnings
    }
}