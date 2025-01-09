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

public class ExplosionAnimationBoard extends Canvas {
    private Image spritesheet; // Holds the sprite sheet used for explosion animation.
    private GraphicsContext g2; // Graphics context for drawing on the canvas.
    private Timeline timeline; 
    double sx, sy, sw, sh, dx, dy, dw, dh; // Source and destination coordinates for drawing images.
    private GUIManager gm; // Reference to the GUI manager for callback.
    private int initialX; 
    private int initialY; 

    public ExplosionAnimationBoard(int width, int height, GUIManager gm) {
        this.gm = gm;
        this.setWidth(width);
        this.setHeight(height);
        spritesheet = new Image("file:images2/explosion_animation_frames.png", false); // Load the explosion sprite sheet.
        g2 = this.getGraphicsContext2D(); // Get the canvas graphics context to draw.
        // Set up the animation timeline with repeated calls to the handler every 100 milliseconds.
        timeline = new Timeline(new KeyFrame(Duration.millis(100), new WalkTheWalker()));
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    public void initCoordinates(int newX, int newY) {
        this.initialX = newX;
        this.initialY = newY; // Store initial coordinates for use after the animation.
    }

    public void startAnimation(int x, int y) {
        dx = x; // Set the drawing X coordinate.
        dy = y; // Set the drawing Y coordinate.
        timeline.play(); // Start the animation.
    }

    // Inner class to handle the progression of animation frames.
    private class WalkTheWalker implements EventHandler<ActionEvent> {
        int tic = 0; // Tracks the number of frames shown.

        public WalkTheWalker() {
            reset(); // Reset animation frame coordinates and counters.
        }

        // Resets animation frame variables and counter.
        private void reset() {
            tic = 0;
            sx = 0;
            sy = 0;
            sw = 64;
            sh = 64;
            dx = 0;
            dy = 0;
            dw = 64;
            dh = 64;
        }

        @Override
        // Updates the sprite sheet frame to display and handles the animation loop.
        public void handle(ActionEvent event) {
            tic++; // Increment frame counter.
            sx += 64; // Move to the next sprite frame horizontally.
            if (tic % 2 == 0) {
                sy += 64; // Move to the next sprite frame row every two ticks.
            }
            // Draw the current frame of the explosion at the specified canvas coordinates.
            g2.drawImage(spritesheet, sx, sy, sw, sh, dx, dy, dw, dh);
            if (tic >= 8) { // After 8 frames, stop the animation and notify the GUI manager.
                timeline.stop();
                gm.endExplosionAnimation(initialX, initialY); // Callback to handle post-animation.
                reset(); // Reset frame coordinates for future animations.
            }
        }
    }
}
