package view_controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.Player;

public class startMenuGUI extends BorderPane{
	
	private GUIManager mainGUI;
	private RadioButton easyButton = new RadioButton("Easy");
	private RadioButton mediumButton = new RadioButton("Medium");
	private RadioButton hardButton = new RadioButton("Hard");
	private Button playButton = new Button("PLAY");
	private HBox buttons = new HBox();
	private String AIChoice = "easy";
	private Label enterNameLabel = new Label("Enter Username:");
	private TextField nameTextField = new TextField();
	private Label invalidUsernameLabel = new Label("Please Enter A Username");
	private VBox centerVBox;
	private HBox hbox = new HBox();
		
	public startMenuGUI(GUIManager gm) {
		this.mainGUI = gm;
		setTheBackground(); 
		registerListeners(); // Setup event listeners for buttons and text fields
		style(); // Apply CSS styles to components
		this.setPrefSize(512, 288); 
		this.setBottom(centerVBox); 
		this.setPadding(new Insets(0, 0, 5, 0)); 
	}
	
	public void style() {
		nameTextField.setText("Enter username"); 
		nameTextField.setPrefSize(140, 12);
		nameTextField.setStyle("-fx-text-fill: black; "); 
		nameTextField.setText(""); // Clear placeholder text when user interacts
		buttons.setSpacing(20); 
		buttons.setAlignment(Pos.CENTER); 
		easyButton.setTextFill(Color.WHITE);
		mediumButton.setTextFill(Color.WHITE);
		hardButton.setTextFill(Color.WHITE);
		playButton.setPrefSize(60, 11);
		playButton.setTextFill(Color.WHITE);
		playButton.setStyle("-fx-background-color: #31b03c; " +   // Background color
                			"-fx-text-fill: white; " +            // Text color
			                "-fx-font-size: 11px; " +             // Text size
			                "-fx-font-weight: bold; " +           // Font weight
			                "-fx-border-width: 2px; " +           // Border width
			                "-fx-border-color: #FFFFFF;");        // Border color

		hbox.getChildren().addAll(nameTextField, playButton);
		hbox.setSpacing(10); 
		hbox.setAlignment(Pos.CENTER); 
		enterNameLabel.setTextFill(Color.WHITE);
		centerVBox = new VBox();
		centerVBox.setSpacing(15); 
		centerVBox.getChildren().addAll(hbox, buttons);
		centerVBox.setAlignment(Pos.BOTTOM_CENTER); 
	}
	
	public void setTheBackground() {
        // Load and set the background image for the start menu
        Image backgroundImage = new Image("/images/battleship_image2.jpg");
        BackgroundImage background = new BackgroundImage(backgroundImage,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        setBackground(new Background(background));
    }
	
	public void registerListeners() {
		ToggleGroup toggleGroup = new ToggleGroup(); // Group radio buttons to ensure only one is selected at a time
		easyButton.setToggleGroup(toggleGroup);
		mediumButton.setToggleGroup(toggleGroup);
		hardButton.setToggleGroup(toggleGroup);
		easyButton.selectedProperty().addListener((observable) -> {
            this.AIChoice = "easy"; // Update AI choice when 'Easy' is selected
        });
		mediumButton.selectedProperty().addListener((observable) -> {
            this.AIChoice = "medium"; // Update AI choice when 'Medium' is selected
        });
		hardButton.selectedProperty().addListener((observable) -> {
            this.AIChoice = "hard"; // Update AI choice when 'Hard' is selected
        });
		playButton.setOnAction(event-> {
			if (!nameTextField.getText().equals("") && !nameTextField.getText().equals("Enter username")) {
				System.out.println("Selected AI: " + AIChoice);
				mainGUI.createPlayer(nameTextField.getText()); // Create a player with the entered username
				mainGUI.setAIDifficulty(AIChoice); // Set the difficulty level in the main GUI
				mainGUI.incrementCurrentGUI(); // Move to the next GUI screen
			} else {
				invalidUsernameLabel.setTextFill(Color.RED); // Show an error in red if username is not entered
				this.setTop(invalidUsernameLabel); // Display the error message at the top of the BorderPane
			}
		});
		nameTextField.setOnMouseClicked(event -> {
			if (nameTextField.getText().equals("Enter username")) {
				nameTextField.setStyle("-fx-text-fill: black; ");
				nameTextField.setText(""); // Clear placeholder text on mouse click
			}
		});
		buttons.getChildren().addAll(easyButton, mediumButton, hardButton); // Add buttons to HBox
	}
}
