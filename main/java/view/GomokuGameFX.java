//GomokuGameFX class implements the core Gomoku game interface
//with interactive board, player turn management, and animated effects (move visual, time-limited turns, etc.)
package view;

import controller.GomokuController;
import controller.SidebarControl;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.Board;
import javafx.geometry.Insets;
import javafx.animation.Timeline;

public class GomokuGameFX extends Application {
    private static final int CELL_SIZE = 30;
    private static final int BOARD_SIZE = 20;
    private static final int BOARD_LENGTH = CELL_SIZE * (BOARD_SIZE - 1);
    private static final int BORDER_WIDTH = 20;
    private static final int PIXEL_SIZE = 2;
    private static final int BACKGROUND_PADDING = 70;

    private GomokuController controller;
    private Canvas canvas;
    private double backgroundWidth = 700;
    private double backgroundHeight = 700;
    private InvalidMove invalidMove;
    private Label warningLabel;
    private Label currentPlayerLabel;
    private Label timeLabel;
    private int timeLimit;
    private Timeline countdown = new Timeline();
    private boolean isFirstMove = true;

    private Font loadFont(String path, double fontSize) {
        //Load font from resources
        return Font.loadFont(getClass().getResourceAsStream(path), fontSize);
    }

    @Override
    public void start(Stage primaryStage) {
        //create a new game board, sidebar, and controller
        Board board = new Board(BOARD_SIZE);
        StatusBar statusBar = new StatusBar();
        GameSidebar gameSidebar = new GameSidebar();
        SidebarControl sidebarControl = new SidebarControl(gameSidebar);

        Font labelFont = loadFont("/fonts/PressStart2P.ttf", 12);

        //warning label for invalid moves
        warningLabel = new Label();
        warningLabel.setTextFill(Color.RED);
        warningLabel.setStyle("-fx-font-weight: bold;");
        warningLabel.setFont(labelFont);
        invalidMove = new InvalidMove(warningLabel);

        controller = new GomokuController(board, statusBar,invalidMove, this);

        //label to show current player (BLACK or WHITE)
        currentPlayerLabel = new Label("Current Player: BLACK");
        currentPlayerLabel.setTextFill(Color.WHITE);
        currentPlayerLabel.setFont(labelFont);

        //label to show remaining time
        timeLabel = new Label("Time Limit: 30s");
        timeLabel.setTextFill(Color.WHITE);
        timeLabel.setFont(labelFont);

        //canvas to draw the board and stones
        canvas = new Canvas(backgroundWidth, backgroundHeight);

        //set up board visuals
        drawBoard();
        drawStones(board);
        setupHoverEffect(); //add hover highlight for current move

        //main root layout
        BorderPane root = new BorderPane();
        root.setBottom(statusBar); //at bottom
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        //layout for top labels (current label and time)
        HBox topRow = new HBox(150);
        topRow.setAlignment(javafx.geometry.Pos.CENTER);
        topRow.getChildren().addAll(currentPlayerLabel, timeLabel);

        //combine top labels, board canvas, and warning label
        VBox boardContainer = new VBox();
        boardContainer.setAlignment(javafx.geometry.Pos.CENTER);
        boardContainer.getChildren().addAll(topRow, canvas, warningLabel);
        root.setCenter(boardContainer);

        //sidebar for controls such as Undo, Redo, Reset
        VBox sidebarContainer = new VBox(gameSidebar);
        sidebarContainer.setAlignment(javafx.geometry.Pos.CENTER);
        VBox.setVgrow(gameSidebar, javafx.scene.layout.Priority.ALWAYS);
        root.setRight(sidebarContainer);

        warningLabel.setMaxWidth(Double.MAX_VALUE); //make invalid move text in center
        warningLabel.setAlignment(javafx.geometry.Pos.CENTER);

        //handle mouse click to place a stone
        canvas.setOnMouseClicked(e -> {
            double x = e.getX() - BORDER_WIDTH - (BACKGROUND_PADDING / 2);
            double y = e.getY() - BORDER_WIDTH - (BACKGROUND_PADDING / 2);

            int col = (int) Math.round(x / CELL_SIZE);
            int row = (int) Math.round(y / CELL_SIZE);

            if (controller.makeMove(row, col)) {
                drawBoard();
                drawStones(board);

                int blackMoves = controller.getBlackMoves();
                int whiteMoves = controller.getWhiteMoves();
                int maxBlackRow = controller.getMaxBlackRow();
                int maxWhiteRow = controller.getMaxWhiteRow();
                sidebarControl.updateMoves(blackMoves, whiteMoves);
                gameSidebar.updateMaxRow(maxBlackRow, maxWhiteRow);

                if (controller.isGameOver()) {
                    int winner = controller.getWinner();
                    countdown.stop();
                } else{
                    updateCurrentPlayerLabel();
                    if (isFirstMove) {
                        isFirstMove = false;
                        startCountdown();
                    } else {
                        countdown.stop();
                        startCountdown();
                    }
                }
            } else{
                invalidMoveAnimation(row, col);
                invalidMove.showWarning("Invalid move!");
            }
        });

        //undo button event handler
        gameSidebar.getUndoButton().setOnAction(e -> {
            //revert previous move
            if (controller.undoMove()) {
                drawBoard();
                drawStones(board);
                updateCurrentPlayerLabel();

                int blackMoves = controller.getBlackMoves();
                int whiteMoves = controller.getWhiteMoves();
                int maxBlackRow = controller.getMaxBlackRow();
                int maxWhiteRow = controller.getMaxWhiteRow();
                sidebarControl.updateMoves(blackMoves, whiteMoves);
                gameSidebar.updateMaxRow(maxBlackRow, maxWhiteRow);
            }
        });

        //redo button event handler
        gameSidebar.getRedoButton().setOnAction(e -> {
            //redo previously undone move
            if (controller.redoMove()) {
                drawBoard();
                drawStones(board);
                updateCurrentPlayerLabel();

                int blackMoves = controller.getBlackMoves();
                int whiteMoves = controller.getWhiteMoves();
                int maxBlackRow = controller.getMaxBlackRow();
                int maxWhiteRow = controller.getMaxWhiteRow();
                sidebarControl.updateMoves(blackMoves, whiteMoves);
                gameSidebar.updateMaxRow(maxBlackRow, maxWhiteRow);
            }
        });

        //exit button closes the game window
        gameSidebar.getExitButton().setOnAction(e ->
            primaryStage.close());

        //reset button clears the board and resets everything
        gameSidebar.getResetButton().setOnAction(e -> {
            if (countdown != null) {
                countdown.stop();
            }
            controller.resetGame();     //clear the board and reset game
            drawBoardWithFade();        //redraw the empty board
            drawStones(board);
            updateCurrentPlayerLabel();  //reset to "BLACK"
            isFirstMove = true;          //the timer reset if the first move is done
            timeLimit = 30;
            timeLabel.setText("Time Limit: " + timeLimit + "s");
            countdown.stop();           //stop any running timer
            sidebarControl.updateMoves(0, 0);  //force move counts to 0
            gameSidebar.updateMaxRow(0, 0); //force max counts to 0
        });

        //create scene with black background
        Scene scene = new Scene(root, Color.BLACK);
        primaryStage.setTitle("Gomoku Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    //fills the canvas with black
    private void drawBackground() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    //draw the board with orange background and grid
    private void drawBoard() {
        drawBackground();
        GraphicsContext gc = canvas.getGraphicsContext2D();

        //board area
        gc.setFill(Color.ORANGE);
        gc.fillRect(BACKGROUND_PADDING / 2, BACKGROUND_PADDING / 2, BOARD_LENGTH + 2 * BORDER_WIDTH, BOARD_LENGTH + 2 * BORDER_WIDTH);

        //grid lines
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        for (int i = 0; i < BOARD_SIZE; i++) {
            double pos = i * CELL_SIZE + BORDER_WIDTH + (BACKGROUND_PADDING / 2);
            gc.strokeLine(pos, BORDER_WIDTH + (BACKGROUND_PADDING / 2), pos, BOARD_LENGTH + BORDER_WIDTH + (BACKGROUND_PADDING / 2));
            gc.strokeLine(BORDER_WIDTH + (BACKGROUND_PADDING / 2), pos, BOARD_LENGTH + BORDER_WIDTH + (BACKGROUND_PADDING / 2), pos);
        }
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(BORDER_WIDTH + (BACKGROUND_PADDING / 2), BORDER_WIDTH + (BACKGROUND_PADDING / 2), BOARD_LENGTH, BOARD_LENGTH);
    }

    //draw stones on the board
    private void drawStones(Board board) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        int[][] gameBoard = board.getBoard();
        for (int row = 0; row < gameBoard.length; row++) {
            for (int col = 0; col < gameBoard[row].length; col++) {
                int stone = gameBoard[row][col];
                if (stone != 0) {
                    drawStone(gc, row, col, stone);
                }
            }
        }
    }

    //draw a single stone pixel by pixel
    private void drawStone(GraphicsContext gc, int row, int col, int player) {
        double centerX = col * CELL_SIZE + BORDER_WIDTH + (BACKGROUND_PADDING / 2);
        double centerY = row * CELL_SIZE + BORDER_WIDTH + (BACKGROUND_PADDING / 2);
        double radius = CELL_SIZE / 2.2;

        gc.setFill(player == 1 ? Color.BLACK : Color.WHITE);

        for (int x = -((int) radius); x <= radius; x += PIXEL_SIZE) {
            for (int y = -((int) radius); y <= radius; y += PIXEL_SIZE) {
                double distance = Math.sqrt(x * x + y * y);
                if (distance <= radius) {
                    gc.fillRect(centerX + x, centerY + y, PIXEL_SIZE, PIXEL_SIZE);
                }
            }
        }
    }

    //start the 30-second countdown timer
    private void startCountdown() {
        //update every second, switch player when time's up
        if (countdown != null && countdown.getStatus() == Animation.Status.RUNNING) {
            countdown.stop();
        }
        timeLimit = 30;
        timeLabel.setText("Time Limit: " + timeLimit + "s");
        countdown = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeLimit--;
            timeLabel.setText("Time Limit: " + timeLimit + "s");
            if (timeLimit <= 0) {
                countdown.stop();
                invalidMove.showWarning("Time's up! Switching player...");
                controller.switchPlayer();
                updateCurrentPlayerLabel();
                startCountdown();
            }
        }));
        countdown.setCycleCount(Animation.INDEFINITE);
        countdown.play();
    }

    //change label depending on player's turn
    private void updateCurrentPlayerLabel() {
        int currentPlayer = controller.getCurrentPlayer();
        currentPlayerLabel.setText("Current Player: " + (currentPlayer == 1 ? "BLACK" : "WHITE"));
    }

    //display semi-transparent circle (stone) preview when hovering over valid cell
    private void setupHoverEffect() {
        canvas.setOnMouseMoved(e -> {
            if (controller.isGameOver()) return;

            //clear previous hover
            drawBoard();
            drawStones(controller.getBoard());

            //calculate intersection position
            double x = e.getX() - BORDER_WIDTH - (BACKGROUND_PADDING / 2);
            double y = e.getY() - BORDER_WIDTH - (BACKGROUND_PADDING / 2);

            int col = (int) Math.round(x / CELL_SIZE);
            int row = (int) Math.round(y / CELL_SIZE);

            //only highlight if the position is valid and empty
            if (controller.getBoard().isValidPos(row, col) &&
                    controller.getBoard().getCell(row, col) == 0) {

                GraphicsContext gc = canvas.getGraphicsContext2D();
                gc.setStroke(controller.getCurrentPlayer() == 1 ?
                        Color.rgb(0, 0, 0, 0.5) : Color.rgb(255, 255, 255, 0.5));
                gc.setLineWidth(2);

                double centerX = col * CELL_SIZE + BORDER_WIDTH + (BACKGROUND_PADDING / 2);
                double centerY = row * CELL_SIZE + BORDER_WIDTH + (BACKGROUND_PADDING / 2);
                double radius = CELL_SIZE / 3;

                gc.strokeOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
            }
        });
        //clear hover when mouse exits canvas
        canvas.setOnMouseExited(e -> {
            drawBoard();
            drawStones(controller.getBoard());
        });
    }

    //flashes a red circle at invalid move location
    private void invalidMoveAnimation(int row, int col) {
        final int flashes = 2; //number of flashes
        final int duration = 100; //milliseconds per flash

        //calculate center of cell
        double centerX = col * CELL_SIZE + BORDER_WIDTH + (BACKGROUND_PADDING / 2);
        double centerY = row * CELL_SIZE + BORDER_WIDTH + (BACKGROUND_PADDING / 2);
        double radius = CELL_SIZE / 2.2 + 5;

        Timeline timeline = new Timeline();

        for (int i = 0; i < flashes; i++) {
            //flash "on" (draw red circle)
            KeyFrame showFrame = new KeyFrame(Duration.millis(i * 2 * duration), e -> {
                GraphicsContext gc = canvas.getGraphicsContext2D();
                gc.setStroke(Color.RED);
                gc.setLineWidth(3);
                gc.strokeOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
            });

            //flash "off" (draw normal board and stones)
            KeyFrame hideFrame = new KeyFrame(Duration.millis((i * 2 + 1) * duration), e -> {
                drawBoard();
                drawStones(controller.getBoard());
            });

            //add both frames to the timeline
            timeline.getKeyFrames().addAll(showFrame, hideFrame);
        }

        timeline.play(); //start animation
    }

    //fades out amd back in when the board is redrawn
    private void drawBoardWithFade() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), canvas);
        fadeOut.setFromValue(1.0); //full opacity
        fadeOut.setToValue(0.5); //half transparent

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), canvas);
        fadeIn.setFromValue(0.5); //half transparent
        fadeIn.setToValue(1.0); //full opacity

        //when fade out finishes, update board, then fade in
        fadeOut.setOnFinished(e -> {
            drawBoard();
            drawStones(controller.getBoard());
            fadeIn.play();
        });
        fadeOut.play();
    }

    //draw single stone with opacity
    private void drawStonesOpacity(GraphicsContext gc, int row, int col, int player, double opacity) {
        double centerX = col * CELL_SIZE + BORDER_WIDTH + (BACKGROUND_PADDING / 2);
        double centerY = row * CELL_SIZE + BORDER_WIDTH + (BACKGROUND_PADDING / 2);
        double radius = CELL_SIZE / 2.2;

        //determine color with opacity (black or white)
        Color stoneColor = player == 1 ?
                Color.rgb(0, 0, 0, opacity) : //black with opacity
                Color.rgb(255, 255, 255, opacity); //white with opacity

        gc.setFill(stoneColor);

        for (int x = -((int) radius); x <= radius; x += PIXEL_SIZE) {
            for (int y = -((int) radius); y <= radius; y += PIXEL_SIZE) {
                double distance = Math.sqrt(x * x + y * y);
                if (distance <= radius) {
                    gc.fillRect(centerX + x, centerY + y, PIXEL_SIZE, PIXEL_SIZE);
                }
            }
        }
    }

    //flickers stones before showing game over
    public void animateWinningStones() {
        Timeline flickerTimeline = new Timeline();

        int flickerCount = 3; //number of flickers
        double duration = 200; //milliseconds per half-cycle

        for (int i = 0; i < flickerCount; i++) {
            //fade out
            KeyFrame fadeOut = new KeyFrame(Duration.millis(i * 2 * duration), e -> {
                drawBoard();
                stoneOpacityFlick(controller.getBoard(), 0.3); //stone dimmed
            });

            //fade in
            KeyFrame fadeIn = new KeyFrame(Duration.millis((i * 2 + 1) * duration), e -> {
                drawBoard();
                drawStones(controller.getBoard());
            });

            flickerTimeline.getKeyFrames().addAll(fadeOut, fadeIn);
        }

        //after flickering, show game over
        flickerTimeline.setOnFinished(e -> {
            Platform.runLater(() -> new GameOver(controller.getWinner()).show());
        });

        flickerTimeline.play();
    }

    //draws all stone with opacity (for flicker)
    private void stoneOpacityFlick(Board board, double opacity) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        int[][] gameBoard = board.getBoard();

        //loop through board and draw stones with opacity
        for (int row = 0; row < gameBoard.length; row++) {
            for (int col = 0; col < gameBoard[row].length; col++) {
                int stone = gameBoard[row][col];
                if (stone != 0) {
                    drawStonesOpacity(gc, row, col, stone, opacity);
                }
            }
        }
    }

    //launch JavaFX application
    public static void main(String[] args) {
        launch(args);
    }
}