package view_controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import model.ExplosionAnimationBoard;
import model.Player;
import model.SplashAnimationBoard;

public class GUIManager extends Application {
	
	// STATS
	private Player userPlayer;	
	private startMenuGUI smg; 
	private HowToPlayGUI htpg;
	private shipPlacementGUI spg; 
	public gameplayGUI gg; 
	private gameOverGUI gog; 
	private String AIChoice;
	private int currentGUI = 0;
	private Stage ps;
	private boolean winOrLose;
	private ExplosionAnimationBoard animationBoardExplosion;
	private SplashAnimationBoard animationBoardSplash;
	private int timesPlayedAgain = 0;
	private int lastCalculatedX;
	private int lastCalculatedY;
	private ImageView smokeImageView;
	private Image smokeImage;
	private ImageView whiteXImageView;
	private Image whiteXImage;

	@Override
    public void start(Stage primaryStage) throws Exception {
		String soundFile = "songs/opening.mp3";
        Media soundMedia = new Media(new File(soundFile).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(soundMedia);
        mediaPlayer.play();
        mediaPlayer.setVolume(0.4);
		this.ps = primaryStage;
		updateScene(primaryStage);
		this.lastCalculatedX = -1;
		this.lastCalculatedY = -1;
    }
	
	public void updateScene(Stage primaryStage) {
		// Start menu
		if (currentGUI == 0) {
			smg = new startMenuGUI(this);
			Scene scene = new Scene(smg);
	        primaryStage.setTitle("Battleship - Start Menu");
	        primaryStage.setScene(scene);
	        primaryStage.setResizable(false);
	        primaryStage.show();
	    // How to play
		} else if(currentGUI == 1) {
			htpg = new HowToPlayGUI(this);
			Scene scene = new Scene(htpg);
	        primaryStage.setTitle("Battleship - How To Play");
	        primaryStage.setScene(scene);
	        primaryStage.setResizable(false);
	        primaryStage.show();
	    // Ship placement
		} else if (currentGUI == 2) {
			spg = new shipPlacementGUI(this);
			Scene scene = new Scene(spg);
			registerKeyListener(scene);
	        primaryStage.setTitle("Battleship - Ship Placement");
	        primaryStage.setScene(scene);
	        primaryStage.setResizable(false);
	        primaryStage.show();
	    // Gameplay
		} else if (currentGUI == 3) {
			gg = new gameplayGUI(this, spg, userPlayer);
			Scene scene = new Scene(gg);
	        primaryStage.setTitle("Battleship - Gameplay");
	        primaryStage.setScene(scene);
	        primaryStage.setResizable(false);
	        primaryStage.show();
	    // Game over
		} else {
			gog = new gameOverGUI(this, this.userPlayer, this.winOrLose);
			primaryStage.setOnCloseRequest(event-> {
				gog.savePlayer();
			});
			Scene scene = new Scene(gog);
	        primaryStage.setTitle("Battleship - Game Over");
	        primaryStage.setScene(scene);
	        primaryStage.setResizable(false);
	        primaryStage.show();
		}
	}
	
	public void executeExplosionAnimation(int x, int y, boolean isEnemy) {
		// Calculated explosion coords
		int calculatedX = 60 + (36 * x);
		int calculatedY = 10 + (36 * y);
		this.lastCalculatedX = calculatedX + 2;
		this.lastCalculatedY = calculatedY - 8;
		if (isEnemy) {
			calculatedY += 407;
		}
		animationBoardExplosion = new ExplosionAnimationBoard(60, 60, this);
		animationBoardExplosion.setLayoutX(calculatedX);
		animationBoardExplosion.setLayoutY(calculatedY);
		animationBoardExplosion.startAnimation(0, 0);
		animationBoardExplosion.initCoordinates(calculatedX, calculatedY);
		gg.getChildren().add(animationBoardExplosion);
	}
	
	public void endExplosionAnimation(int passedX, int passedY) {
		// Create a copy of the list to avoid ConcurrentModificationException
	    List<Node> toRemove = new ArrayList<>(gg.getChildren());
	    // Iterate through the list and remove instances of SplashAnimationBoard
	    for (Node node : toRemove) {
	        if (node instanceof ExplosionAnimationBoard) {
	            gg.getChildren().remove(node);
	        }
	    }
	    this.lastCalculatedX = passedX;
		this.lastCalculatedY = passedY;
		drawSmoke(passedX, passedY);
	}
	
	public void executeSplashAnimation(int x, int y, boolean isEnemy) {
		int calculatedX = 50 + (36 * x); // offset
		int calculatedY = 10 + (36 * y); // offset
		this.lastCalculatedX = calculatedX + 2;
		this.lastCalculatedY = calculatedY - 8;
		if (isEnemy) {
			calculatedY += 407;
		}
		animationBoardSplash = new SplashAnimationBoard(80, 50, this);
		animationBoardSplash.setLayoutX(calculatedX);
		animationBoardSplash.setLayoutY(calculatedY);
		animationBoardSplash.startAnimation(0, 0);
		animationBoardSplash.initCoordinates(calculatedX, calculatedY);
		gg.getChildren().add(animationBoardSplash);
	}
	
	public void endSplashAnimation(int passedX, int passedY) {
	    // Create a copy of the list to avoid ConcurrentModificationException
	    List<Node> toRemove = new ArrayList<>(gg.getChildren());
	    // Iterate through the list and remove instances of SplashAnimationBoard
	    for (Node node : toRemove) {
	        if (node instanceof SplashAnimationBoard) {
	            gg.getChildren().remove(node);
	        }
	    }
	    this.lastCalculatedX = passedX;
		this.lastCalculatedY = passedY;
		drawWhiteX(passedX, passedY);
	}
	
	public void setAIDifficulty(String choice) {
		this.AIChoice = choice;
	}
	
	public void setWOrL(boolean wOrL) {
		this.winOrLose = wOrL;
	}
	
	public void incrementCurrentGUI() {
		this.currentGUI += 1;
		updateScene(this.ps);
	}
	
	public void updatePowerupButtonStyleNo() {
		gg.styleButtonNoPowerup();
	}
	
	public void updatePowerupButtonStyleYes() {
		gg.styleButtonPowerup();
	}
	
	public void drawSmoke(int imageX, int imageY) {
		// Smoke for when ship is hit
		imageY -= 8;
		imageX += 2;
		smokeImageView = new ImageView();
	    smokeImageView.setFitWidth(50);
        smokeImageView.setFitHeight(50);
        smokeImageView.setLayoutX(imageX);
        smokeImageView.setLayoutY(imageY);
        smokeImageView.setPreserveRatio(true);
        gg.getChildren().add(smokeImageView);
        smokeImage = new Image("/images/smoke_animation.gif");
        smokeImageView.setImage(smokeImage);
    }
    
    public void drawWhiteX(int imageX, int imageY) {
    	// White X to represent a miss
    	imageX += 24;
    	imageY += 10;
    	whiteXImageView = new ImageView();
	    whiteXImageView.setFitWidth(30);
	    whiteXImageView.setFitHeight(30);
	    whiteXImageView.setLayoutX(imageX);
	    whiteXImageView.setLayoutY(imageY);
	    whiteXImageView.setPreserveRatio(true);
        gg.getChildren().add(whiteXImageView);
        whiteXImage = new Image("/images/whitex_picture.png");
        whiteXImageView.setImage(whiteXImage);
    }
	
	public void playAgin() {
		String newUsername;
		if (timesPlayedAgain == 0) {
			timesPlayedAgain++;
			newUsername = userPlayer.getUsername() + "(" + timesPlayedAgain + ")";
		} else {
			timesPlayedAgain++;
			newUsername = userPlayer.getUsername().substring(0, userPlayer.getUsername().length() - 3) + "(" + timesPlayedAgain + ")";
		}
		
		this.userPlayer = new Player(newUsername);
		this.currentGUI = 2;
		updateScene(this.ps);
	}
	
	public void registerKeyListener(Scene s) {
		// Press R to rotate ship
		s.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.R) {
                spg.rotateShip();
            }
        });
		
	}
	
	public Player getPlayer() {
		return this.userPlayer;
	}
	
	public void createPlayer(String username) {
		this.userPlayer = new Player(username);
		System.out.println("Player Created: " + this.userPlayer.getUsername());
	}
	
	public String getAIDifficulty() {
		return this.AIChoice;
	}

    public static void main(String[] args) {
        launch(args);
    }
}
