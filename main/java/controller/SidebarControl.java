//SidebarControl class Manages and displays player move counters in the game sidebar
package controller;

import view.GameSidebar;

public class SidebarControl {
    private int blackMoves = 0;
    private int whiteMoves = 0;
    private GameSidebar gameSidebar; //reference to GameSidebar object for updating the UI

    //constructor that initializes the SidebarControl with a GameSidebar instance
    public SidebarControl(GameSidebar sidebar) {
        this.gameSidebar = sidebar;
    }

    //updates the move counters for both black and white and refreshes the display in the GameSidebar
    public void updateMoves(int blackMoves, int whiteMoves) {
        this.blackMoves = blackMoves;
        this.whiteMoves = whiteMoves;
        gameSidebar.updateMoves(blackMoves, whiteMoves); //call update method in GameSidebar to refresh the UI
    }
}