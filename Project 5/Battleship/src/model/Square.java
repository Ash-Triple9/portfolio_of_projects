package model;

import java.io.File;
import java.util.ArrayList;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import view_controller.GUIManager;

public class Square extends Rectangle {

    public int x, y; 
    public Ship ship = null; // Ship positioned on this square, if any
    public boolean wasHit = false; 
    public boolean isPlayer; // Flag to determine if the square belongs to a player
    public Player player; 
    public GUIManager gm; 
    private ImageView shipImageView; 

    private Board board; 

    // Constructor to initialize a square with size, position, board, and player
    public Square(int x, int y, Board board, Player p) {
        super(35, 35); // Call superclass constructor with square size
        this.x = x;
        this.y = y;
        this.isPlayer = false;
        this.board = board;
        this.player = p;
        setFill(Color.web("#09aaed")); // Set initial color of the square
        setStroke(Color.WHITE); // Set border color of the square
    }

    // Set the image of the ship for this square
    public void setShipImage(Image image) {
        shipImageView.setImage(image);
    }

    // Method to handle actions when a square is hit
    public boolean hit() {
        wasHit = true;
        if (ship != null) { // If there is a ship, handle ship hit logic
            if (this.board.isEnemy()) {
                setFill(Color.RED); // Change color to red to indicate hit
                this.board.gm.executeExplosionAnimation(x, y, false);
                // Check and apply powerups if available
                if (player.hasPowerup()) {
                    player.usePowerup();
                    activatePowerup(player.getPowerupChoice());
                    player.missesNoLongerVoid();
                } 
                // Update player hit status based on game rules
                if (!player.getVoidMissedShots()) {
                    player.hit();
                } else {
                    player.incrementHitsInARow();
                }
            } else {
                this.board.gm.executeExplosionAnimation(x, y, true);
            }
            ship.hit(); // Call the hit method on the ship
            if (!ship.hasntSunk()) {
                board.ships--; // Decrement ship count if sunk
            }
            // Play explosion sound effect
            String soundFile = "songs/vine-boom.mp3";
            Media soundMedia = new Media(new File(soundFile).toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(soundMedia);
            mediaPlayer.play();
            // Update powerup button styles based on hit conditions
            if (player.hitsInARow % 3 == 0 && player.hitsInARow != 0) {
                this.board.gm.updatePowerupButtonStyleYes();
            } else {
                this.board.gm.updatePowerupButtonStyleNo();
            }
            if (player.getCanHavePowerup()) {
                this.board.gm.updatePowerupButtonStyleYes();
            } else {
                this.board.gm.updatePowerupButtonStyleNo();
            }
            return true;
        } else {
            // Handle logic if square is hit but no ship is present
            if (this.board.isEnemy()) {
                if (player.hasPowerup()) {
                    player.usePowerup();
                    activatePowerup(player.getPowerupChoice());
                    player.missesNoLongerVoid();
                    this.board.gm.updatePowerupButtonStyleNo();
                } else {
                    if (!player.getVoidMissedShots()) {
                        player.missed();
                    }
                }
                this.board.gm.executeSplashAnimation(x, y, false);
            } else {
                this.board.gm.executeSplashAnimation(x, y, true);
            }
            // Play splash sound effect
            String soundFile = "songs/bloop.mp3";
            Media soundMedia = new Media(new File(soundFile).toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(soundMedia);
            mediaPlayer.play();
        }
        if (player.getCanHavePowerup()) {
            this.board.gm.updatePowerupButtonStyleYes();
        } else {
            this.board.gm.updatePowerupButtonStyleNo();
        }

        return false;
    }

    // Method to hit surrounding squares based on the square powerup
    private void hitSurroundingSquares() {
        for (int i = Math.max(0, x - 1); i <= Math.min(9, x + 1); i++) {
            for (int j = Math.max(0, y - 1); j <= Math.min(9, y + 1); j++) {
                Square neighbor = board.getSquare(i, j);
                if (!neighbor.wasHit) {
                    neighbor.hit();
                }
            }
        }
    }

    // Method to hit all squares in the same vertical line
    private void hitVerticalLine() {
        for (int i = 0; i < 10; i++) {
            Square neighbor = board.getSquare(x, i);
            if (!neighbor.wasHit) {
                neighbor.hit();
            }
        }
    }

    // Method to hit all squares in the same horizontal line
    private void hitHorizontalLine() {
        for (int i = 0; i < 10; i++) {
            Square neighbor = board.getSquare(i, y);
            if (!neighbor.wasHit) {
                neighbor.hit();
            }
        }
    }

    // Method to activate the selected powerup
    private void activatePowerup(String powerupChoice) {
        if (powerupChoice.equals("vertical")) {
            hitVerticalLine();
        } else if (powerupChoice.equals("horizontal")) {
            hitHorizontalLine();
        } else {
            hitSurroundingSquares();
        }
    }

    // Mark the square as belonging to the player
    public void becomePlayerSquare() {
        this.isPlayer = true;
    }
}
