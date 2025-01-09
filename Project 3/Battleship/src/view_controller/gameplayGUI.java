package view_controller;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.Board;
import model.ExplosionAnimationBoard;
import model.Player;


public class gameplayGUI extends BorderPane {

	private GUIManager mainGUI;
	private Board playerBoard, enemyBoard;
	private Button powerupButton = new Button();

	
	public gameplayGUI(GUIManager gm, shipPlacementGUI spg, Player player) {
		this.mainGUI = gm;
		this.setPrefSize(500, 800);
		BattleshipMain battleshipMain = new BattleshipMain(gm, spg.getPlayerBoard());
		battleshipMain.enemyBoard();
		battleshipMain.startGame();
		setTheBackground();
		styleButtonNoPowerup();
		VBox vbox = new VBox(10, battleshipMain.getEnemyBoard(), powerupButton, battleshipMain.getPlayerBoard());
		vbox.setAlignment(Pos.CENTER);
		this.setCenter(vbox);

		powerupButton.setOnAction(event-> {
			player.givePowerup();
		});
	}
	
	public void styleButtonNoPowerup() {
		powerupButton.setText("Hit 3 In A Row For A Powerup!");
		powerupButton.setPrefSize(200, 20);
		powerupButton.setTextFill(Color.BLACK);
		powerupButton.setStyle("-fx-background-color: #FF0000; " +   // Background color
                			"-fx-text-fill: black; " +            	 // Text color
			                "-fx-font-size: 11px; " +             	 // Text size
			                "-fx-font-weight: bold; " +           	 // Font weight
			                "-fx-border-width: 2px; " +           	 // Border width
			                "-fx-border-color: #FFFFFF;");        	 // Border color

	}
	
	public void styleButtonPowerup() {
		powerupButton.setText("Click To Activate Powerup!");
		powerupButton.setPrefSize(200, 20);
		powerupButton.setTextFill(Color.BLACK);
		powerupButton.setStyle("-fx-background-color: #00FF00; " +   // Background color
                			"-fx-text-fill: black; " +            	 // Text color
			                "-fx-font-size: 11px; " +             	 // Text size
			                "-fx-font-weight: bold; " +           	 // Font weight
			                "-fx-border-width: 2px; " +           	 // Border width
			                "-fx-border-color: #FFFFFF;");        	 // Border color
	}

	public void setTheBackground() {
		// Load the image
		Image backgroundImage = new Image("images/water_gg_background.png");

		// Create a background image
		BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);

		// Create a background with the image
		setBackground(new Background(background));
	}
}