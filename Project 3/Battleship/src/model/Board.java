package model;

import java.io.File;
import java.util.ArrayList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import view_controller.GUIManager;

public class Board extends Parent {
    private VBox rows = new VBox();
    private boolean isEnemy = false; // Flag to check if the board belongs to the enemy
    public int ships = 5; // Number of ships on the board
    public GUIManager gm;
    private ImageView shipImageView;
    private Image shipImage;
    
    public Board(boolean enemy, EventHandler<? super MouseEvent> handler, Player p, GUIManager gm) {
        this.gm = gm;
        this.isEnemy = enemy;
        for (int y = 0; y < 10; y++) {
            HBox row = new HBox(); // Create a row of squares
            for (int x = 0; x < 10; x++) {
                Square s = new Square(x, y, this, p);
                s.setOnMouseClicked(handler); // Set click event handler for each square
                row.getChildren().add(s);
            }
            rows.getChildren().add(row); // Add the row to the VBox of rows
        }
        getChildren().add(rows); // Add all rows to the board's children
    }

    public boolean placeShip(Ship ship, int x, int y) {
        if (canPlaceShip(ship, x, y)) {
            // Play splash sound when placing a ship
            String soundFile = "songs/splash.mp3";
            Media soundMedia = new Media(new File(soundFile).toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(soundMedia);
            mediaPlayer.play();
            
            int length = ship.type;
            if (ship.vertical) {
                setImage(length, x, y, 90, ship); // Set ship image for vertical orientation
                for (int i = y; i < y + length; i++) {
                    Square sq = getSquare(x, i);
                    sq.ship = ship;
                    if (!isEnemy) {
                        sq.setStroke(Color.GREEN); // Highlight player's own ships
                        shipImageView.setVisible(true);
                        shipImageView.setMouseTransparent(true); // Make the ship image non-interactive
                    } else {
                        shipImageView.setVisible(false); // Hide enemy ships
                        shipImageView.setMouseTransparent(true);
                    }
                }
            } else {
                setImage(length, x, y, 0, ship); // Set ship image for horizontal orientation
                for (int i = x; i < x + length; i++) {
                    Square sq = getSquare(i, y);
                    sq.ship = ship;
                    if (!isEnemy) {
                        sq.setStroke(Color.GREEN); // Highlight player's own ships
                        shipImageView.setVisible(true);
                        shipImageView.setMouseTransparent(true); // Make the ship image non-interactive
                    } else {
                        shipImageView.setVisible(false); // Hide enemy ships
                        shipImageView.setMouseTransparent(true);
                    }
                }
            }

            return true;
        }
        return false;
    }

    // Method to instantiate image view of ships
    public ImageView ImageViewInstance() {
        return shipImageView;
    }
   
    private void setImage(int length, int x, int y, int rotate, Ship s) {
    	// Set ship image based on type and orientation
		switch (rotate) {
		// For vertical ships
		case 90:
			
			// Adjustments and settings based on ship length
	    	switch (length) {
			case 5:
				// Initialize and set properties for the ship ImageView
	            shipImageView = new ImageView();
	            shipImageView.setFitWidth(35*length);
	            shipImageView.setFitHeight(35);
	            shipImageView.setLayoutX(x*35 -64.25);
	            shipImageView.setLayoutY(y*35 +78.75);
	            shipImageView.setRotate(rotate);
	            shipImageView.setPreserveRatio(true);
	            getChildren().add(shipImageView);
	            // Load image based on ship type
	            shipImage = new Image("/images/Battleship.png");
	            shipImageView.setImage(shipImage);
	            s.setImageAs(shipImageView);
	            return;
			case 4:
				// Initialize and set properties for the ship ImageView
	            shipImageView = new ImageView();
	            shipImageView.setFitWidth(35*length);
	            shipImageView.setFitHeight(35);
	            shipImageView.setLayoutX(x*35-46.75);
	            shipImageView.setLayoutY(y*35+52.5);
	            shipImageView.setRotate(90);
	            shipImageView.setPreserveRatio(true);
	            getChildren().add(shipImageView);
	            // Load image based on ship type
	            shipImage = new Image("/images/Carrier.png");
	            shipImageView.setImage(shipImage);
	            s.setImageAs(shipImageView);
	            return;
			case 3:
				// Initialize and set properties for the ship ImageView
	            shipImageView = new ImageView();
	            shipImageView.setFitWidth(35*length);
	            shipImageView.setFitHeight(35);
	            shipImageView.setLayoutX(x*35-26.25);
	            shipImageView.setLayoutY(y*35+43.75);
	            shipImageView.setRotate(90);
	            shipImageView.setPreserveRatio(true);
	            getChildren().add(shipImageView);
	            // Load image based on ship type
	            shipImage = new Image("/images/Destroyer.png");
	            shipImageView.setImage(shipImage);
	            s.setImageAs(shipImageView);
	            return;
			case 2:
				// Initialize and set properties for the ship ImageView
	            shipImageView = new ImageView();
	            shipImageView.setFitWidth(35*length);
	            shipImageView.setFitHeight(35);
	            shipImageView.setLayoutX(x*35- 15.5);
	            shipImageView.setLayoutY(y*35+35);
	            shipImageView.setRotate(90);
	            shipImageView.setPreserveRatio(true);
	            getChildren().add(shipImageView);
	            // Load image based on ship type
	            shipImage = new Image("/images/Submarine.png");
	            shipImageView.setImage(shipImage);
	            s.setImageAs(shipImageView);
	            return;
	            }
	    
	    // For horizontal ships
		case 0:
			// Adjustments and settings based on ship length
			switch (length) {
			case 5:
				// Initialize and set properties for the ship ImageView
	            shipImageView = new ImageView();
	            shipImageView.setFitWidth(35*length);
	            shipImageView.setFitHeight(35);
	            shipImageView.setLayoutX(x*35 +8.75);
	            shipImageView.setLayoutY(y*35 +8.75);
	            shipImageView.setRotate(rotate);
	            shipImageView.setPreserveRatio(true);
	            getChildren().add(shipImageView);
	            // Load image based on ship type
	            shipImage = new Image("/images/Battleship.png");
	            shipImageView.setImage(shipImage);
	            s.setImageAs(shipImageView);
	            return;
			case 4:
				// Initialize and set properties for the ship ImageView
	            shipImageView = new ImageView();
	            shipImageView.setFitWidth(35*length);
	            shipImageView.setFitHeight(35);
	            shipImageView.setLayoutX(x*35+8.75);
	            shipImageView.setLayoutY(y*35+8.75);
	            shipImageView.setRotate(rotate);
	            shipImageView.setPreserveRatio(true);
	            getChildren().add(shipImageView);
	            // Load image based on ship type
	            shipImage = new Image("/images/Carrier.png");
	            shipImageView.setImage(shipImage);
	            s.setImageAs(shipImageView);
	            return;
			case 3:
				// Initialize and set properties for the ship ImageView
	            shipImageView = new ImageView();
	            shipImageView.setFitWidth(35*length);
	            shipImageView.setFitHeight(35);
	            shipImageView.setLayoutX(x*35);
	            shipImageView.setLayoutY(y*35+8.75);
	            shipImageView.setRotate(rotate);
	            shipImageView.setPreserveRatio(true);
	            getChildren().add(shipImageView);
	            // Load image based on ship type
	            shipImage = new Image("/images/Destroyer.png");
	            shipImageView.setImage(shipImage);
	            s.setImageAs(shipImageView);
	            return;
			case 2:
				// Initialize and set properties for the ship ImageView
	            shipImageView = new ImageView();
	            shipImageView.setFitWidth(35*length);
	            shipImageView.setFitHeight(35);
	            shipImageView.setLayoutX(x*35+5);
	            shipImageView.setLayoutY(y*35+17.5);
	            shipImageView.setRotate(rotate);
	            shipImageView.setPreserveRatio(true);
	            getChildren().add(shipImageView);
	            // Load image based on ship type
	            shipImage = new Image("/images/Submarine.png");
	            shipImageView.setImage(shipImage);
	            s.setImageAs(shipImageView);
	            return;
	            }
			}
			
		}

    // Check if a ship can be placed at a specified location
    private boolean canPlaceShip(Ship ship, int x, int y) {
        int length = ship.type;
        for (int i = 0; i < length; i++) {
            int newX = ship.vertical ? x : x + i;
            int newY = ship.vertical ? y + i : y;
            // Check if the new position is on the board and unoccupied
            if (!validatePoint(newX, newY) || getSquare(newX, newY).ship != null) {
                return false;
            }
            // Check if any neighboring square is occupied
            for (Square neighbor : getNeighbors(newX, newY)) {
                if (neighbor.ship != null) {
                    return false;
                }
            }
        }
        return true;
    }

    // Retrieve the square at a specified position
    public Square getSquare(int x, int y) {
        return (Square)((HBox)rows.getChildren().get(y)).getChildren().get(x);
    }
    
    // Method to check if the board belongs to the enemy
    public boolean isEnemy() {
        return this.isEnemy;
    }

    // Get all neighboring squares for a given position
    private Square[] getNeighbors(int x, int y) {
        Point2D[] points = new Point2D[] {
            new Point2D(x - 1, y),
            new Point2D(x + 1, y),
            new Point2D(x, y - 1),
            new Point2D(x, y + 1)
        };

        ArrayList<Square> neighbors = new ArrayList<>();
        for (Point2D p : points) {
            if (validatePoint(p.getX(), p.getY())) {
                neighbors.add(getSquare((int)p.getX(), (int)p.getY()));
            }
        }

        return neighbors.toArray(new Square[0]);
    }

    // Validate if a point is within the board boundaries
    private boolean validatePoint(double x, double y) {
        return (x >= 0) && (x < 10) && (y >= 0) && (y < 10);
    }
}
