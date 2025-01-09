package view_controller;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import model.Player;

public class leaderboardTableView extends BorderPane {
	
	private boolean winOrLose;
    private static TableView<Player> table;
    private static ObservableList<Player> players = FXCollections.observableArrayList();

    public leaderboardTableView(boolean wOrL) {
    	
    	// for styling
    	this.winOrLose = wOrL;
    	
        // Create a new TableView of Players
        table = new TableView<>();

        // Create columns
        TableColumn<Player, String> usernameCol = new TableColumn<>("Username");
        TableColumn<Player, Double> accuracyCol = new TableColumn<>("Accuracy");
        TableColumn<Player, Integer> mostHitsCol = new TableColumn<>("Hits in a Row");
        TableColumn<Player, Integer> missCol = new TableColumn<>("Misses");
        TableColumn<Player, Integer> scoreCol = new TableColumn<>("Score");
        TableColumn<Player, String> difficultyCol = new TableColumn<>("Difficulty");
        TableColumn<Player, String> wonCol = new TableColumn<>("Won");
        
        // Connect columns to Player fields
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        accuracyCol.setCellValueFactory(new PropertyValueFactory<>("accuracy"));
        mostHitsCol.setCellValueFactory(new PropertyValueFactory<>("mostHitsInARow"));
        missCol.setCellValueFactory(new PropertyValueFactory<>("miss"));
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));  
        difficultyCol.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
        wonCol.setCellValueFactory(new PropertyValueFactory<>("won"));
        
        // Set the observable list of players as the model for the TableView
        table.setItems(players);
        
        // Add columns to TableView
        table.getColumns().addAll(scoreCol, usernameCol, accuracyCol, mostHitsCol, missCol, difficultyCol, wonCol);
        
        // Set preferred column widths
        usernameCol.setMinWidth(72);
        accuracyCol.setMinWidth(50);
        mostHitsCol.setMinWidth(120);
        scoreCol.setMinWidth(50);
        missCol.setMinWidth(50);
        difficultyCol.setMinWidth(50);
        wonCol.setMinWidth(50);
        
        // style the tableView
        style();
        
        table.setMinSize(500, 400);
        table.setMaxSize(500, 400);

        // Set TableView to center of BorderPane
        this.setCenter(table);
        try {
			read();
		} catch (Exception e) {
			// Auto-generated catch block
			System.out.println("Failed to read");
		}
    }
    
    public void read() throws Exception {
		FileInputStream rawBytes = new FileInputStream("objects.ser");
		ObjectInputStream inFile = new ObjectInputStream(rawBytes);
		// Read the entire object from the file on disk. Casts required
		ArrayList<Player> savedPlayers = (ArrayList<Player>) inFile.readObject();
		// Should close input files also
		inFile.close();
		if (savedPlayers.size() > 0) {
			for (Player p : savedPlayers) {
				players.add(p);
			}
		}
	}
    
    private void style() {
        if (this.winOrLose) {
        	table.setStyle(
        			"-fx-table-cell-border-color: limegreen; " +             // Table cell border color
                    "-fx-border-width: 1px; " +                        // Border width
                    "-fx-font-size: 14px; " +                          // Font size
                    "-fx-font-family: Arial; " +                       // Font family
                    "-fx-alignment: center-right; " +                  // Text alignment (horizontal)
                    "-fx-vertical-alignment: center; " +               // Text alignment (vertical)
                    "-fx-cell-size: 40px; " +                          // Cell size (row height)
                    "-fx-selection-bar: limegreen; " +                    // Selection color
                    "-fx-selection-bar-non-focused: limegreen; " +        // Selection color when the TableView is not focused
                    "-fx-control-inner-background: gray; " +           // Control inner background color
                    "-fx-control-inner-background-alt: darkgray; " +  // Alternate control inner background color
                    "-fx-cell-horizontal-fill: true;"                  // Whether cells should fill horizontally
                );
        } else {
        	table.setStyle(
                    "-fx-table-cell-border-color: red; " +             // Table cell border color
                    "-fx-border-width: 1px; " +                        // Border width
                    "-fx-font-size: 14px; " +                          // Font size
                    "-fx-font-family: Arial; " +                       // Font family
                    "-fx-alignment: center-right; " +                  // Text alignment (horizontal)
                    "-fx-vertical-alignment: center; " +               // Text alignment (vertical)
                    "-fx-cell-size: 40px; " +                          // Cell size (row height)
                    "-fx-selection-bar: red; " +                    // Selection color
                    "-fx-selection-bar-non-focused: red; " +        // Selection color when the TableView is not focused
                    "-fx-control-inner-background: gray; " +           // Control inner background color
                    "-fx-control-inner-background-alt: darkgray; " +  // Alternate control inner background color
                    "-fx-cell-horizontal-fill: true;"                  // Whether cells should fill horizontally
                );
        }
    }

    // Method to access the player list from outside
    public ObservableList<Player> getPlayerList() {
        return players;
    }
    
    public void reset() {
    	System.out.println("Resetting leaderboard...");
    	players = FXCollections.observableArrayList();
    	table.setItems(players);
    }
    
    public void addPlayer(Player newPlayer) {
    	players.add(newPlayer);
    }
    
    public ArrayList<Player> getPlayersAsArrayList() {
    	ArrayList<Player> arrList = new ArrayList<>();
    	for (Player p : players) {
    		arrList.add(p);
    	}
    	return arrList;
    }

    // Method to access the table from outside
    public TableView<Player> getTable() {
        return table;
    }
}
