package view_controller;

import javafx.geometry.Insets;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class HowToPlayGUI extends BorderPane{
	
	private GUIManager mainGUI;
	private Label title = new Label("How To Play");
	private VBox instructionsVBox = new VBox();
	// Instructions Labels
	private Label instruction1 = new Label("• Place your ships! (Press 'R' to rotate them)");
	private Label instruction2 = new Label("• Click on a square to shoot a missle!");
	private Label instruction3 = new Label("• A splash means you missed. But an");
	private Label instruction3B = new Label("   explosion indicates a hit!");
	private Label instruction4 = new Label("• Get 3 hits in a row and you'll ");
	private Label instruction4B = new Label(" earn youself a powerup!");
	private Label instruction5 = new Label("• Once you've destroyed your enemies ships,");
	private Label instruction5B = new Label("   you win!");
	private Label instruction5C = new Label("• Enemy will try to destroy your ships too!");
	private Label instruction6 = new Label("• After each game your stats are recorded");
	private Label instruction6B = new Label("   and uploaded to the leaderbaord.");
	private Label instruction7 = new Label("• Don't Lose!");
	private Button nextButton = new Button("Got it!");

	public HowToPlayGUI(GUIManager gm) {
		this.mainGUI = gm;
        this.setPrefSize(500, 500);
        this.setPadding(new Insets(0, 0, 5, 0));
        styleComponents();
        layoutGUI();
        registerListeners();
        setTheBackground();
	}
	
	public void styleComponents() {
		nextButton.setStyle(
				"-fx-background-color: #31b03c; " +   // Background color
    			"-fx-text-fill: white; " +            // Text color
                "-fx-font-size: 11px; " +             // Text size
                "-fx-font-weight: bold; " +           // Font weight
                "-fx-border-width: 2px; " +           // Border width
                "-fx-border-color: #FFFFFF;");        // Border color
		title.setStyle("-fx-text-fill: black; -fx-font-size: 34px; -fx-font-weight: bold;");
		title.setPadding(new Insets(35, 0, 0, 0));
		instruction1.setStyle("-fx-text-fill: black; -fx-font-size: 19px; -fx-font-weight: bold;");
		instruction2.setStyle("-fx-text-fill: black; -fx-font-size: 19px; -fx-font-weight: bold;");
		instruction3.setStyle("-fx-text-fill: black; -fx-font-size: 19px; -fx-font-weight: bold;");
		instruction3B.setStyle("-fx-text-fill: black; -fx-font-size: 19px; -fx-font-weight: bold;");
		instruction4.setStyle("-fx-text-fill: black; -fx-font-size: 19px; -fx-font-weight: bold;");
		instruction4B.setStyle("-fx-text-fill: black; -fx-font-size: 19px; -fx-font-weight: bold;");
		instruction5.setStyle("-fx-text-fill: black; -fx-font-size: 19px; -fx-font-weight: bold;");
		instruction5B.setStyle("-fx-text-fill: black; -fx-font-size: 19px; -fx-font-weight: bold;");
		instruction5C.setStyle("-fx-text-fill: black; -fx-font-size: 19px; -fx-font-weight: bold;");
		instruction6.setStyle("-fx-text-fill: black; -fx-font-size: 19px; -fx-font-weight: bold;");
		instruction6B.setStyle("-fx-text-fill: black; -fx-font-size: 19px; -fx-font-weight: bold;");
		instruction7.setStyle("-fx-text-fill: black; -fx-font-size: 19px; -fx-font-weight: bold;");
	}
	
	public void registerListeners() {
		nextButton.setOnAction(event-> {
			this.mainGUI.incrementCurrentGUI();
		});
	}
	
	public void layoutGUI() {
		// Print instructions
		instructionsVBox.getChildren().addAll(instruction1, 
				instruction2, instruction3, instruction3B, instruction4, instruction4B, 
				instruction5, instruction5B, instruction5C, instruction6, instruction6B, instruction7);
		instructionsVBox.setAlignment(Pos.BASELINE_LEFT);
		instructionsVBox.setPadding(new Insets(0, 0, 0, 55));
		this.setTop(title);
		this.setCenter(instructionsVBox);
		this.setBottom(nextButton);
		this.setAlignment(title, Pos.CENTER);
		this.setAlignment(nextButton, Pos.CENTER);
	}
	
	public void setTheBackground() {
        // Load the image
        Image backgroundImage = new Image("images/piratemap_resized.jpg");
        
        // Create a background image
        BackgroundImage background = new BackgroundImage(backgroundImage,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT);

        // Create a background with the image
        setBackground(new Background(background));
    }
	
}
