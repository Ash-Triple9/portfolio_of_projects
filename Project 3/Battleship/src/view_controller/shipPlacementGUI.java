package view_controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import model.Board;
import model.Ship;
import model.Square;

public class shipPlacementGUI extends BorderPane {

    private GUIManager mainGUI;
    private Button b = new Button("Place all ships to continue");
    private boolean shipIsVertical = true;
    private boolean running = false;
    private int[] shipSizes = new int[]{5, 4, 3, 3, 2}; // Array to hold ship sizes
    private int currentShipIndex = 0; // Index to track which ship size to place
    private Board playerBoard;
    private Image im;
    private BackgroundImage bg;

    public shipPlacementGUI(GUIManager gm) {
        this.mainGUI = gm;
        this.setPrefSize(500, 500);
        this.setTop(b);
        this.setAlignment(b, Pos.CENTER);
        this.setPadding(new Insets(20, 0, 0, 0));
        this.playerBoard = playerBoard();
        this.setCenter(playerBoard);
        styleButtons();
        updateButton();
        setTheBackground();
    }
    public void setTheBackground() {
        // Load the image
        Image backgroundImage = new Image("images/water_spg_background.png");
        
        // Create a background image
        BackgroundImage background = new BackgroundImage(backgroundImage,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT);

        // Create a background with the image
        setBackground(new Background(background));
    }

    public Board playerBoard() {
        playerBoard = new Board(false, event -> {
            if (running || currentShipIndex >= shipSizes.length) {
                return;
            }
            Square sq = (Square) event.getSource();
            if (playerBoard.placeShip(new Ship(shipSizes[currentShipIndex], shipIsVertical), sq.x, sq.y)) {
                if (++currentShipIndex == shipSizes.length) { // Increment to next ship size
                    b.setText("All ships placed, click to continue");
                    b.setStyle("-fx-background-color: #00FF00; " +   // Background color
                			"-fx-text-fill: black; " +            	 // Text color
			                "-fx-font-size: 12px; " +             	 // Text size
			                "-fx-font-weight: bold; " +           	 // Font weight
			                "-fx-border-width: 2px; " +           	 // Border width
			                "-fx-border-color: #FFFFFF;");        	 // Border color
                }
            }
        }, this.mainGUI.getPlayer(), this.mainGUI);
        return playerBoard;
    }
    
    public void styleButtons() {
    	b.setAlignment(Pos.CENTER);
		b.setPrefSize(208, 30);
		b.setTextFill(Color.BLACK);
		b.setStyle("-fx-background-color: #FF0000; " +   // Background color
                			"-fx-text-fill: black; " +            	 // Text color
			                "-fx-font-size: 12px; " +             	 // Text size
			                "-fx-font-weight: bold; " +           	 // Font weight
			                "-fx-border-width: 2px; " +           	 // Border width
			                "-fx-border-color: #FFFFFF;");        	 // Border color
    }

    public Board getPlayerBoard() {
        return this.playerBoard;
    }

    private void updateButton() {
        b.setOnAction(event -> {
            if (currentShipIndex == shipSizes.length) { // Ensure all ships are placed before continuing
                mainGUI.incrementCurrentGUI();
            }
        });
    }

    public void rotateShip() {
        shipIsVertical = !shipIsVertical;
    }

}
