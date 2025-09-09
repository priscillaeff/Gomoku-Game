//GameSidebar class implements a styled sidebar showing game stats and controls
//to create and manage a game sidebar UI that displays the title,
//move statistics (counts and max consecutive rows), and control buttons
package view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox; // Import HBox
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;

public class GameSidebar extends VBox {

    private Label title;
    private Button resetButton;
    private Button exitButton;
    private Button saveButton;
    private Button loadButton;
    private Label blackMovesLabel;
    private Label whiteMovesLabel;
    private Label blackMaxLabel;
    private Label whiteMaxLabel;

    private int blackMoves = 0;
    private int whiteMoves = 0;
    private int maxBlackRow = 0;
    private int maxWhiteRow = 0;
    private boolean isBlackTurn = true; //flag to track whose turn it is

    private Button undoButton; //button for undo
    private Button redoButton; //button for redo

    public GameSidebar() {
        setPadding(new Insets(20)); //set padding around sidebar
        setSpacing(20); //spacing between elements in sidebar
        setStyle("-fx-background-color: black; -fx-padding: 10; -fx-border-color: gold; -fx-border-width: 3;");
        setPrefWidth(400); //set preferred width for the sidebar
        setAlignment(Pos.CENTER); //center the elements vertically

        //load custom font
        Font titleFont = loadFont("/fonts/PressStart2P.ttf", 50);
        Font labelFont = loadFont("/fonts/PressStart2P.ttf", 12);

        //title setup
        title = new Label("GOMOKU");
        title.setFont(titleFont);
        title.setTextFill(Color.WHITE);

        //buttons setup
        resetButton = createStartExitButton("START NEW GAME");
        exitButton = createStartExitButton("EXIT GAME");
        saveButton = createSaveLoadButton("SAVE");
        loadButton = createSaveLoadButton("LOAD");

        //create an HBox to place save and load buttons side by side
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(saveButton, loadButton);

        //create a VBox to add the start and exit buttons, as well as the buttonBox
        VBox buttons = new VBox(10);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(resetButton, exitButton, buttonBox);

        //move tracker section
        GridPane moveTracker = new GridPane();
        moveTracker.setHgap(20);
        moveTracker.setVgap(10);
        moveTracker.setPadding(new Insets(10)); //padding around the GridPane
        moveTracker.setAlignment(Pos.CENTER);

        Label moveTitle = new Label("   MOVE TRACKER");
        moveTitle.setFont(labelFont);
        moveTitle.setTextFill(Color.WHITE);

        //labels for black and white player's moves and max consecutive rows
        Label blackLabel = new Label(" BLACK");
        Label whiteLabel = new Label(" WHITE");
        blackMovesLabel = new Label("Moves: 0");
        whiteMovesLabel = new Label("Moves: 0");
        blackMaxLabel = new Label("Max: 0");
        whiteMaxLabel = new Label("Max: 0");

        //set font and text color for each Label
        blackLabel.setFont(labelFont);
        whiteLabel.setFont(labelFont);
        blackMovesLabel.setFont(labelFont);
        whiteMovesLabel.setFont(labelFont);
        blackMaxLabel.setFont(labelFont);
        whiteMaxLabel.setFont(labelFont);

        blackLabel.setTextFill(Color.WHITE);
        whiteLabel.setTextFill(Color.WHITE);
        blackMovesLabel.setTextFill(Color.WHITE);
        whiteMovesLabel.setTextFill(Color.WHITE);
        blackMaxLabel.setTextFill(Color.WHITE);
        whiteMaxLabel.setTextFill(Color.WHITE);

        //add labels to GridPane
        moveTracker.add(moveTitle, 0, 0, 2, 1);
        moveTracker.add(blackLabel, 0, 1);
        moveTracker.add(whiteLabel, 1, 1);
        moveTracker.add(blackMovesLabel, 0, 2);
        moveTracker.add(whiteMovesLabel, 1, 2);
        moveTracker.add(blackMaxLabel, 0, 3);
        moveTracker.add(whiteMaxLabel, 1, 3);

        //undo and redo buttons setup
        HBox undoRedoBox = new HBox(10);
        undoRedoBox.setAlignment(Pos.CENTER);

        undoButton = createUndoRedoButton("<");
        redoButton = createUndoRedoButton(">");
        undoRedoBox.getChildren().addAll(undoButton, redoButton);

        //add elements to sidebar
        getChildren().addAll(title, buttons, moveTracker, undoRedoBox);

        //reset button to start new game
        resetButton.setOnAction(e -> startNewGame());
    }

    //load custom font
    private Font loadFont(String path, double fontSize) {
        return Font.loadFont(getClass().getResourceAsStream(path), fontSize);
    }

    private Font buttonFont = loadFont("/fonts/PressStart2P.ttf", 14);

    //create start/exit button
    private Button createStartExitButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: gold; -fx-text-fill: black; -fx-padding: 10;");
        button.setFont(buttonFont);
        button.setPrefWidth(250);

        //hover effect for button
        button.setOnMouseEntered(e ->
                button.setStyle("-fx-background-color: darkorange; -fx-text-fill: black; -fx-padding: 10;") // Keep padding consistent
        );
        button.setOnMouseExited(e ->
                button.setStyle("-fx-background-color: gold; -fx-text-fill: black; -fx-padding: 10;") // Keep padding consistent
        );

        return button;
    }

    //create save/load buttons
    private Button createSaveLoadButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: gold; -fx-text-fill: black; -fx-padding: 10;");
        button.setFont(buttonFont);
        button.setPrefWidth(120);

        //hover effects for button
        button.setOnMouseEntered(e ->
                button.setStyle("-fx-background-color: darkorange; -fx-text-fill: black; -fx-padding: 10;") // Keep padding consistent
        );
        button.setOnMouseExited(e ->
                button.setStyle("-fx-background-color: gold; -fx-text-fill: black; -fx-padding: 10;") // Keep padding consistent
        );

        return button;
    }

    //create undo/redo buttons
    private Button createUndoRedoButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: gold; -fx-text-fill: black;");
        button.setFont(buttonFont);
        button.setPrefSize(50, 50); //set the size of the button to make it circular
        button.setShape(new javafx.scene.shape.Circle(10)); //set button shape to circle

        //hover effects for buttons
        button.setOnMouseEntered(e ->
                button.setStyle("-fx-background-color: darkorange; -fx-text-fill: black;")
        );
        button.setOnMouseExited(e ->
                button.setStyle("-fx-background-color: gold; -fx-text-fill: black;")
        );

        return button;
    }

    //resets game stats
    public void startNewGame() {
        isBlackTurn = true;
        blackMoves = 0;
        whiteMoves = 0;
        maxBlackRow = 0;
        maxWhiteRow = 0;

        updateMoves(0, 0);
        updateMaxRow(0, 0);
    }

    //update the move counts for both players
    public void updateMoves(int blackMoves, int whiteMoves) {
        this.blackMoves = blackMoves; //set black's move count
        this.whiteMoves = whiteMoves; //set white's move count
        blackMovesLabel.setText("Moves: " + blackMoves); //update label
        whiteMovesLabel.setText("Moves: " + whiteMoves);
    }

    //update the max consecutive rows for both players
    public void updateMaxRow(int blackMaxRow, int whiteMaxRow) {
        this.maxBlackRow = blackMaxRow; //set black's max consecutive row count
        this.maxWhiteRow = whiteMaxRow; //set white's max consecutive row count
        blackMaxLabel.setText("Max: " + blackMaxRow);
        whiteMaxLabel.setText("Max: " + whiteMaxRow);
    }


    public Button getResetButton() {
        return resetButton;
    }

    public Button getExitButton() {
        return exitButton;
    }

    public Button getUndoButton() {
        return undoButton;
    }

    public Button getRedoButton() {
        return redoButton;
    }
}