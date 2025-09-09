//Board class represents the game board for Gomoku game
//handles stone placements, win checking, and board status updates
package logic;

public class Board {
    public static final int DEFAULT_SIZE = 19; //default board size
    private final int[][] board; //2D array
    private final int boardSize;
    private int totalMoves; //total number of stones placed
    private final int maxMoves; //max number of moves

    public Board(int boardSize) {
        if (boardSize < 5 || boardSize > 20) {
            throw new IllegalArgumentException();
        }
        this.boardSize = boardSize;
        this.totalMoves = 0;
        this.maxMoves = boardSize * boardSize;
        this.board = new int[boardSize][boardSize];
    }

    //place a stone on board at specified position
    public boolean placeStone(int row, int col, int player) {
        //check if position is valid and empty
        if (!isValidPos(row, col) || board[row][col] != 0) {
            return false;
        }
        board[row][col] = player; //place stone
        totalMoves++; //increment move count
        return true;
    }

    //remove stone from specified position
    public boolean removeStone(int row, int col) {
        if (!isValidPos(row, col) || board[row][col] == 0) {
            return false;
        }
        board[row][col] = 0; //remove the stone
        totalMoves--; //decrement the move count
        return true;
    }

    //Check if the current move results im a win
    public boolean checkWin(int row, int col, int player) {
        //directions: horizontal, vertical, diagonal, anti-diagonal
        int[][] directions = {
                {0, 1}, {1, 0},
                {1, 1}, {1, -1}};

        for (int[] direction : directions) {
            int count = 1;

            //count consecutive stones in one direction and the opposite direction
            count += countConsecutive(row, col, player, direction[0], direction[1]);
            count += countConsecutive(row, col, player, -direction[0], -direction[1]);

            //win if there are 5 consecutive stones
            if (count >= 5) return true;
        }
        return false;
    }

    //Helper method to count consecutive stones in a given direction
    private int countConsecutive(int row, int col, int player, int dx, int dy) {
        int count = 0;

        //look at 4 positions
        for (int i = 1; i < 5; i++) {
            int newRow = row + i * dx;
            int newCol = col + i * dy;

            if (isValidPos(newRow, newCol) && board[newRow][newCol] == player) {
                count++;
            } else {
                break; //stop counting if no match
            }
        }
        return count;
    }

    //check if (row,col) is valid on board
    public boolean isValidPos(int row, int col) {
        return row >= 0 && row < boardSize && col >= 0 && col < boardSize;
    }

    public int[][] getBoard() {
        return board;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public int getCell(int row, int col) {
        return board[row][col];
    }

    //check if the board is full
    public boolean isFull() {
        return totalMoves >= maxMoves;
    }  //return true if the board has reached the maximum number of moves

    //clear the board and reset all values to 0
    public void clearBoard() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                board[i][j] = 0;
            }
        }
        totalMoves = 0;
    }

    //get max Length of consecutive stones
    public int getMaxLength(int row, int col, int player) {
        int maxLength = 0;

        //check all 4 directions (horizontal, vertical, diagonal, anti-diagonal)
        int[][] directions = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};  // Right, Down, Diagonal, Anti-diagonal

        for (int[] direction : directions) {
            int count = 1;  //current stone is counted
            count += countConsecutive(row, col, player, direction[0], direction[1]);  // Count in one direction
            count += countConsecutive(row, col, player, -direction[0], -direction[1]); // Count in the opposite direction

            //track the maximum consecutive length found
            if (count > maxLength) {
                maxLength = count;
            }
        }
        return maxLength; //return the longest chain found
    }
}