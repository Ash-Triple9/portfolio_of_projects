package view_controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Player;
import view_controller.gameplayGUI;

public class gameOverGUI extends BorderPane{
	
	private GUIManager mainGUI;
	private boolean winOrLose;
	// visual components
	private Label gameOverLabel;
	private Button resetButton;
	private Button playAgainButton;
	private leaderboardTableView table;
	private Label leaderboardLabel;
	private Label instructionLabel;
	private HBox buttonsHBox;
	
	
	public gameOverGUI(GUIManager gm, Player userPlayer, boolean wOrL) {
		this.winOrLose = wOrL;
		this.mainGUI = gm;
		this.setPrefSize(788, 528);
		setTheBackground();
		createComponents();
		createLayout();
		registerListeners();
		setStats(userPlayer);
	}
	
	public void setStats(Player user) {
		if (user != null) {
			table.addPlayer(user);
		} else {
			System.out.println("User Was Null");
		}
	}
	
	public void createComponents() {
		if (this.winOrLose) {
			styleComponentsWin();
		} else {
			styleComponentsLose();
		}
	}
	
	public void styleComponentsWin() {
		// LEADERBOARD LABEL
		leaderboardLabel = new Label("LEADERBOARD");
		Font font1 = Font.font("Arial", FontWeight.BOLD, 20);
		leaderboardLabel.setFont(font1);
		leaderboardLabel.setTextFill(Color.BLACK);
		// INSTRUCTION LABEL
		instructionLabel = new Label("Click on a stat to sort the leaderboard!");
		Font font3 = Font.font("Arial", 12);
		instructionLabel.setFont(font3);
		instructionLabel.setTextFill(Color.BLACK);
		// GAME OVER LABEL
		gameOverLabel = new Label("Game Over - You Win!");
        Font font2 = Font.font("Arial", FontWeight.BOLD, 28);
        gameOverLabel.setFont(font2);
        gameOverLabel.setTextFill(Color.DARKGREEN);
        // RESET BUTTON
        resetButton = new Button("Reset Leaderboard");
		resetButton.setStyle(
			    "-fx-background-color: red; " +      // Background color
			    "-fx-text-fill: white; " +           // Text color
			    "-fx-border-color: white; " +        // Border color
			    "-fx-border-width: 2px; "            // Border width
		);
		// PLAY AGAIN BUTTON
		playAgainButton = new Button("Play Again");
		playAgainButton.setStyle(
				"-fx-background-color: #31b03c; " +      // Background color
				"-fx-text-fill: white; " +           // Text color
				"-fx-border-color: white; " +        // Border color
				"-fx-border-width: 2px; "            // Border width
		);
		// BUTTONS HBOX
		buttonsHBox = new HBox();
		buttonsHBox.getChildren().addAll(resetButton, playAgainButton);
		buttonsHBox.setAlignment(Pos.CENTER);
		buttonsHBox.setSpacing(10);
		table = new leaderboardTableView(true);
	}
	
	public void styleComponentsLose() {
		// LEADERBOARD LABEL
		leaderboardLabel = new Label("LEADERBOARD");
		Font font1 = Font.font("Arial", FontWeight.BOLD, 20);
		leaderboardLabel.setFont(font1);
		leaderboardLabel.setTextFill(Color.RED);
		// INSTRUCTION LABEL
		instructionLabel = new Label("Click on a stat to sort the leaderboard!");
		Font font3 = Font.font("Arial", 12);
		instructionLabel.setFont(font3);
		instructionLabel.setTextFill(Color.WHITE);
		// GAME OVER LABEL
		gameOverLabel = new Label("Game Over - You Lose!");
        Font font = Font.font("Arial", FontWeight.BOLD, 28);
        gameOverLabel.setFont(font);
        gameOverLabel.setTextFill(Color.RED);
        // RESET BUTTON
        resetButton = new Button("Reset Leaderboard");
		resetButton.setStyle(
			    "-fx-background-color: red; " +      // Background color
			    "-fx-text-fill: white; " +           // Text color
			    "-fx-border-color: white; " +        // Border color
			    "-fx-border-width: 2px; "            // Border width
		);
		// PLAY AGAIN BUTTON
		playAgainButton = new Button("Play Again");
		playAgainButton.setStyle(
			    "-fx-background-color: #31b03c; " +      // Background color
			    "-fx-text-fill: white; " +           // Text color
			    "-fx-border-color: white; " +        // Border color
			    "-fx-border-width: 2px; "            // Border width
		);
		// BUTTONS HBOX
		buttonsHBox = new HBox();
		buttonsHBox.getChildren().addAll(resetButton, playAgainButton);
		buttonsHBox.setAlignment(Pos.CENTER);
		buttonsHBox.setSpacing(10);
		table = new leaderboardTableView(false);
	}
	
	public void setTheBackground() {
        if (this.winOrLose) {
        	// Load the image
            Image backgroundImage = new Image("/images/pixel_beach.jpg");
            
            // Create a background image
            BackgroundImage background = new BackgroundImage(backgroundImage,
                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER, BackgroundSize.DEFAULT);

            // Create a background with the image
            setBackground(new Background(background));
        } else {
        	this.setStyle("-fx-background-color: #000000;");
        }
    }
	
	public void registerListeners() {
		resetButton.setOnAction(event-> {
			table.reset();
		});
		playAgainButton.setOnAction(event-> {
			System.out.println("Playing Again...");
			mainGUI.playAgin();
		});
	}
	
	public void savePlayer() {
		String fileName = "objects.ser";
    	try {
	    	FileOutputStream bytesToDisk = new FileOutputStream(fileName);
	    	ObjectOutputStream outFile = new ObjectOutputStream(bytesToDisk);
	    	ArrayList<Player> playersAsArrayList = table.getPlayersAsArrayList();
	    	// outFile understands the writeObject message.
	    	outFile.writeObject(playersAsArrayList);
	    	outFile.close(); // Always close the output file!
    	} catch (IOException ioe) {
    		ioe.printStackTrace();
    	}
	}
	
	public void createLayout() {
		VBox labelAndTable = new VBox();
		labelAndTable.getChildren().addAll(leaderboardLabel, table, instructionLabel);
		labelAndTable.setAlignment(Pos.CENTER);
		labelAndTable.setSpacing(10);
		this.setTop(gameOverLabel);
		this.setBottom(buttonsHBox);
		this.setCenter(labelAndTable);
		this.setAlignment(gameOverLabel, Pos.CENTER);
		this.setAlignment(buttonsHBox, Pos.CENTER);
	}
}
