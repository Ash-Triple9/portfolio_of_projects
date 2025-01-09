package model;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.util.Duration;
import view_controller.GUIManager;

public class SplashAnimationBoard extends Canvas {
    private Image spritesheet;
    private GraphicsContext g2;
    private Timeline timeline;
    double sx, sy, sw, sh, dx, dy, dw, dh; // Variables for sprite sheet coordinates and destination dimensions
    private GUIManager gm;
    private int initialX;
    private int initialY;

    // Constructor initializes canvas and animation properties
    public SplashAnimationBoard(int width, int height, GUIManager gm) {
        this.gm = gm;
        this.setWidth(width);
        this.setHeight(height);
        // Load the image for the animation
        spritesheet = new Image("file:images2/splash_animation_frames_final.png", false);
        g2 = this.getGraphicsContext2D();
        // Setup animation timeline that updates every 100ms
        timeline = new Timeline(new KeyFrame(Duration.millis(100), new WalkTheWalker()));
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    // Starts the splash animation at specified coordinates
    public void startAnimation(int x, int y) {
        dx = x;  // Set the starting x-coordinate for the animation
        dy = y;  // Set the starting y-coordinate for the animation
        timeline.play();
    }

    // Initializes starting coordinates for the animation
    public void initCoordinates(int newX, int newY) {
        this.initialX = newX;
        this.initialY = newY;
    }

    // Inner class to handle animation updates
    private class WalkTheWalker implements EventHandler<ActionEvent> {
        int tic = 0;  // Counter for the number of animation frames shown

        // Initializes variables used for drawing the sprite
        public WalkTheWalker() {
            tic = 0;  // Reset counter
            sx = 0;  // Initial sprite x-coordinate
            sy = 0;  // Initial sprite y-coordinate
            sw = 80;  // Width of each sprite frame
            sh = 50;  // Height of each sprite frame
            dx = 0;  // Initial drawing x-coordinate on canvas
            dy = 0;  // Initial drawing y-coordinate on canvas
            dw = 80;  // Width to draw on canvas
            dh = 50;  // Height to draw on canvas
        }

        @Override
        // Called every 100 ms to animate the sprite
        public void handle(ActionEvent event) {
            tic++;  // Increment frame counter
            sx += 80;  // Move to the next sprite frame on the spritesheet
            g2.drawImage(spritesheet, sx, sy, sw, sh, dx, dy, dw, dh);  // Draw the current frame
            if (tic >= 8) {  // If 8 frames have been shown
                timeline.stop();  // Stop the animation
                gm.endSplashAnimation(initialX, initialY);  // Notify that animation has ended
                sx = 0;  // Reset sprite sheet x-coordinate
            }
        }
    }
}
