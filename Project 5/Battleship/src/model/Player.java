package model;

import java.io.Serializable;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class Player implements Serializable {
    public int hitsInARow;
    private int mostHitsInARow;
    private String username;
    private int totalShots;
    private int totalHits;
    private int rank;
    private int score;
    private boolean hasPowerup;
    private boolean voidMisses;
    private String powerupChoice;
    private int miss;
    private boolean canHavePowerup;
    private String difficulty;
    private String won;

    // serialization ID for Java object serialization
    private static final long serialVersionUID = -6470090944414208496L;

    // Constructor initializes player with a username and default values
    public Player(String name) {
        this.username = name;
        resetStats();  // Resets all gameplay statistics
        this.powerupChoice = "square";  // Default powerup choice
    }

    // Method to handle when a player hits a target
    public void hit() {
        this.score += (this.hitsInARow * 100) + 100;  // Score increment based on consecutive hits
        this.totalHits++;
        this.totalShots++;
        this.hitsInARow++;
        if (this.hitsInARow % 3 == 0) {  // Eligibility for powerup every 3 consecutive hits
            this.canHavePowerup = true;
        }
        if (this.hitsInARow > this.mostHitsInARow) {  // Update record for most hits in a row
            this.mostHitsInARow = this.hitsInARow;
        }
    }

    // Method to increment the hit streak without affecting other statistics
    public void incrementHitsInARow() {
        this.hitsInARow++;
        if (this.hitsInARow % 3 == 0) {
            this.canHavePowerup = true;
        }
    }

    // Method to handle when a player misses a target
    public void missed() {
        this.score -= 10;  // Penalty for missing
        this.miss++;
        this.hitsInARow = 0;  // Reset hit streak on miss
        this.totalShots++;
    }

    // Calculate and return the shooting accuracy as a percentage
    public double getAccuracy() {
        if (totalShots == 0) return 0;
        double accuracy = ((double) totalHits / (double) totalShots) * 100;
        return Double.parseDouble(String.format("%.2f", accuracy));
    }

    // Getters and setters for player properties
    public String getUsername() {
        return this.username;
    }

    public int getMostHitsInARow() {
        return this.mostHitsInARow;
    }

    public int getScore() {
        return this.score;
    }

    public int getRank() {
        return this.rank;
    }

    public void setRank(int newRank) {
        this.rank = newRank;
    }

    public int getMiss() {
        return this.miss;
    }

    // Method to give a player a powerup, if eligible
    public void givePowerup() {
        if (this.canHavePowerup) {
            System.out.println("Powerup received");
            this.hasPowerup = true;
            this.score += 50;  // Bonus points for receiving a powerup
            powerupSelectionAlert();  // Trigger UI to select powerup type
        } else {
            System.out.println("COULD NOT GIVE POWERUP");
        }
    }

    public boolean hasPowerup() {
        return this.hasPowerup;
    }

    // Use the powerup and reset relevant flags
    public void usePowerup() {
        this.hasPowerup = false;
        this.voidMisses = true;
        this.canHavePowerup = false;
    }

    public void missesNoLongerVoid() {
        this.voidMisses = false;
    }

    public boolean getVoidMissedShots() {
        return this.voidMisses;
    }

    public String getPowerupChoice() {
        return this.powerupChoice;
    }

    public boolean getCanHavePowerup() {
        return this.canHavePowerup;
    }

    // Reset all gameplay stats to default
    public void resetStats() {
        this.hitsInARow = 0;
        this.mostHitsInARow = 0;
        this.totalHits = 0;
        this.totalShots = 0;
        this.rank = 0;
        this.score = 0;
        this.miss = 0;
    }

    // Set game win status
    public void setWon(boolean didWin) {
        this.won = didWin ? "yes" : "no";
    }

    public void setDifficulty(String playerDifficulty) {
        this.difficulty = playerDifficulty;
    }

    public String getDifficulty() {
        return this.difficulty;
    }

    public String getWon() {
        return this.won;
    }

    // UI alert for selecting a powerup, triggered after meeting conditions
    public void powerupSelectionAlert() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setHeaderText("You hit 3 shots in a row and earned yourself a powerup shot!");
        alert.setContentText("Choose a powerup option below:");
        ButtonType verticalLineShot = new ButtonType("Vertical Line Shot");
        ButtonType horizontalLineShot = new ButtonType("Horizontal Line Shot");
        ButtonType squareShot = new ButtonType("Square Shot");
        alert.getButtonTypes().setAll(verticalLineShot, horizontalLineShot, squareShot);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            switch (result.get().getText()) {
                case "Vertical Line Shot":
                    System.out.println("Vertical Line Shot selected");
                    powerupChoice = "vertical";
                    break;
                case "Horizontal Line Shot":
                    System.out.println("Horizontal Line Shot selected");
                    powerupChoice = "horizontal";
                    break;
                case "Square Shot":
                    System.out.println("Square Shot selected");
                    powerupChoice = "square";
                    break;
                default:
                    System.out.println("No powerup selected");
                    break;
            }
        } else {
            System.out.println("No option selected");
        }
    }

}
