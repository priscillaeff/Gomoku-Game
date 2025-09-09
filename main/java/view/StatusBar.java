//StatusBar class to create a horizontal status bar displaying name and ID
package view;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.scene.text.Font;

public class StatusBar extends HBox {
    private final Label statusLabel; //label to display status information

    public StatusBar() {
        statusLabel = new Label("Priscilla Effendi - 124040017");
        statusLabel.setFont(new Font (16));
        this.getChildren().add(statusLabel);
        this.setAlignment(Pos.CENTER);
        this.setSpacing(10);
        this.setStyle("-fx-padding: 10; -fx-background-color: #f0f0f0;");
    }

    public void updateStatus(int currentPlayer, boolean isGameOver, int winner) {
        statusLabel.setText("Priscilla Effendi - 124040017");
    }
}