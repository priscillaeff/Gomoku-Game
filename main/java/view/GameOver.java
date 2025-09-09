//GameOver class to display a game-over pop up window
package view;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.InputStream;

public class GameOver {
    private final Stage stage; //new stage to display the game over screen
    private final int winner;

    public GameOver(int winner) {
        this.winner = winner;
        this.stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL); //makes the stage modal (blocks interaction with other windows)
        stage.initStyle(StageStyle.UNDECORATED); //removes window decorations (e.g., title bar)
        createUI(); //calls method to set up the UI components
    }

    //create and set up the user interface of the game over window
    private void createUI() {
        // Load custom font
        Font titleFont = loadFont("/fonts/PressStart2P.ttf", 50);
        Font winnerFont = loadFont("/fonts/PressStart2P.ttf", 20);
        Font loserFont = loadFont("/fonts/PressStart2P.ttf", 15);
        Font drawFont = loadFont("/fonts/PressStart2P.ttf", 20);
        Font OKFont = loadFont("/fonts/PressStart2P.ttf", 10);

        //create "GAME OVER" text
        Text gameOverText = new Text("GAME\nOVER");
        gameOverText.setFont(titleFont);
        gameOverText.setFill(Color.GOLD);

        //initialize text for winner, loser and draw
        Text winnerText = new Text();
        Text loserText = new Text();
        Text drawText = new Text();

        if (winner == 1) { //black wins
            winnerText = new Text("BLACK WINS!");
            winnerText.setFont(winnerFont);
            winnerText.setFill(Color.WHITE);
            loserText = new Text("WHITE LOSES!\n");
            loserText.setFont(loserFont);
            loserText.setFill(Color.WHITE);
        } else if (winner == 2) { //white wins
            winnerText = new Text("WHITE WINS!");
            winnerText.setFont(winnerFont);
            winnerText.setFill(Color.WHITE);
            loserText = new Text("BLACK LOSES!\n");
            loserText.setFont(loserFont);
            loserText.setFill(Color.WHITE);
        } else { //draw
            drawText = new Text("DRAW!\n");
            drawText.setFont(drawFont);
            drawText.setFill(Color.WHITE);
        }

        //OK Button to close game over window
        Button OKButton = new Button("OK");
        OKButton.setFont(OKFont);
        OKButton.setStyle("-fx-background-color: gold; -fx-text-fill: black;"); //set button styles
        OKButton.setOnAction(e -> stage.close()); //close the stage when the button is clicked
        OKButton.setOnMouseEntered(e -> OKButton.setStyle("-fx-background-color: orange; -fx-text-fill: black;"));
        OKButton.setOnMouseExited(e -> OKButton.setStyle("-fx-background-color: gold; -fx-text-fill: black;"));

        Region space = new Region();
        space.setPrefHeight(5); // Add some space between elements

        //layout
        VBox layout = new VBox(10);
        layout.getChildren().addAll(gameOverText, space);

        //add winner, loser, or draw message depending on the outcome
        if (winner == 1 || winner == 2) {
            layout.getChildren().addAll(winnerText, loserText);
        } else {
            layout.getChildren().add(drawText);
        }

        //add the OK button
        layout.getChildren().add(OKButton);
        layout.setStyle("" +
                "-fx-alignment: center; " +
                "-fx-padding: 20px; " +
                "-fx-background-color: black;" +
                "-fx-border-color: gold;" +
                "-fx-border-width: 5px;" +
                "-fx-border-style: solid;" //set border style to solid (rectangle)
        );

        //create and set the scene for the stage
        Scene scene = new Scene(layout, 400, 300);
        scene.setFill(Color.BLACK);
        stage.setScene(scene);
    }

    //show the game over stage on screen
    public void show() {
        stage.centerOnScreen();
        stage.show();
    }

    //helper method to load a custom font from a file
    private Font loadFont(String path, double size) {
        try (InputStream fontStream = getClass().getResourceAsStream(path)) {
            if (fontStream != null) {
                return Font.loadFont(fontStream, size); //load the font
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Font.font("System", size);
    }
}