//InvalidMove class to manage an error/warning message display (when player makes an invalid move)
package view;

import javafx.scene.control.Label;

public class InvalidMove {
    private Label warningLabel; //the label to display the warning message

    public InvalidMove(Label warningLabel) {
        this.warningLabel = warningLabel;
        this.warningLabel.setVisible(false);  //initially, the warning label is hidden
    }

    //show a warning with custom message
    public void showWarning(String message) {
        warningLabel.setText(message); //set the warning message text
        warningLabel.setVisible(true);  //show the warning
    }

    //hide warning message
    public void hideWarning() {
        warningLabel.setVisible(false);  // Hide the warning
    }
}