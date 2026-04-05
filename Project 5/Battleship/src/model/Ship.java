package model;



import javafx.scene.Parent;
import javafx.scene.image.ImageView;



public class Ship extends Parent {

    public int type;	// keeps track of type of ship 

    public boolean vertical = true;		// sets vertical or horizontal

    private int health;			// keeps track of ships health
    
    private ImageView shipImage;


    // Constructor for ship

    public Ship(int type, boolean verticalOrHorizontal) {

        this.type = type;

        this.vertical = verticalOrHorizontal;

        health = type;

    }



    public void hit() {
        health--;	// decrement ships health if been hit
        if (health <= 0) {
        	show();
        }
    }



    public boolean hasntSunk() {
        return health > 0;	 // check if boat has not sunk yet
    }
    
    public void setImageAs(ImageView sImage) {
    	this.shipImage = sImage;
    }
    
    public void show() {
    	this.shipImage.setVisible(true);
    }
    
    public void hide() {
    	this.shipImage.setVisible(false);
    }

}
