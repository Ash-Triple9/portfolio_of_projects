package view_controller;

import java.util.ArrayList;
import java.util.Random;

import javafx.scene.input.MouseButton;
import model.Board;
import model.HardAI;
import model.IntermediateAI;
import model.Player;
import model.Square;
import model.Ship;
import model.RandomAI;

public class BattleshipMain {

	private boolean running = false; // Flag to check if the game is currently running
	private Board enemyBoard, playerBoard; 
	private boolean enemyTurn = false; // Flag to determine whose turn it is
	private Random random = new Random(); 
	private RandomAI easyAI = new RandomAI(); 
	private IntermediateAI intermediateAI = new IntermediateAI(); 
	private HardAI hardAI = new HardAI(); 
	private GUIManager mainGUI; 
	private Player userPlayer; 
	private boolean intermediateAIRandomness = true; // Flag to add randomness to intermediate AI
	private Square sq; // Temporary square object for various operations
	private ArrayList<Integer> coords; 

	public BattleshipMain(GUIManager mg, Board pb) {
		this.mainGUI = mg;
		this.userPlayer = mainGUI.getPlayer(); // Get the player object from GUI manager
		this.playerBoard = pb; 
	}

	public void startGameplayPhase(shipPlacementGUI spg) {
		this.playerBoard = spg.getPlayerBoard(); // Get the player board from ship placement GUI
		startGame();
	}

	public Board enemyBoard() {
		// Instantiate enemy board with event handling for mouse clicks on squares
		enemyBoard = new Board(true, event -> {
			if (!running) {
				return; // Do nothing if the game isn't running
			}
			Square sq = (Square) event.getSource(); // Get the square that was clicked
			if (sq.wasHit) {
				return; // Do nothing if square was already hit
			}
			if (sq.hit()) { // Perform hit action and check if a ship was hit
				enemyTurn = false; // If ship was hit, it remains player's turn
			} else {
				enemyTurn = true; // Else, switch turn to enemy
			}
			if (enemyBoard.ships == 0) { // Check if all enemy ships are destroyed
				System.out.println("YOU WIN");
				userPlayer.setWon(true);
				userPlayer.setDifficulty(mainGUI.getAIDifficulty()); // Set the difficulty at which the player won
				mainGUI.setWOrL(true); // Update GUI to reflect the win
				mainGUI.incrementCurrentGUI(); // Move to next GUI screen
			}

			if (enemyTurn) {
				enemyMove(); // If it's enemy's turn, perform enemy move
			}
		}, userPlayer, this.mainGUI);
		return enemyBoard;
	}

	private void enemyMove() {
		// Determine AI behavior based on difficulty set in GUI
		if (mainGUI.getAIDifficulty() == "easy") {
			while (enemyTurn) {
				// Get random coordinates from the easy AI and attempt to hit the player's board
				Square sq = playerBoard.getSquare(easyAI.getXCoords(), easyAI.getYCoords());
				if (sq.wasHit) {
					continue; // Skip if square was already hit
				}
				enemyTurn = sq.hit(); // Attempt to hit the square and continue if successful

				if (playerBoard.ships == 0) { // Check if all player ships are destroyed
					System.out.println("YOU LOSE");
					userPlayer.setWon(false);
					userPlayer.setDifficulty("easy");
					mainGUI.setWOrL(false); // Update GUI to reflect the loss
					mainGUI.incrementCurrentGUI(); // Move to next GUI screen
				}
			}
		} else if (mainGUI.getAIDifficulty() == "medium") {
			while (enemyTurn) {
				// Get strategic coordinates from intermediate AI and attempt to hit
				int[] x = intermediateAI.getXYCoords();
				int i = x[0];
				int j = x[1];

				Square sq = playerBoard.getSquare(i, j);
				enemyTurn = sq.hit(); // Attempt to hit the square and continue if successful
				if (enemyTurn) {
					intermediateAI.reportHit(i, j); // Report hit to AI for learning
					continue;
				} else {
					intermediateAI.reportMiss(i, j); // Report miss to AI for adjustment
				}

				if (playerBoard.ships == 0) {
					System.out.println("YOU LOSE");
					userPlayer.setWon(false);
					userPlayer.setDifficulty("medium");
					mainGUI.setWOrL(false);
					mainGUI.incrementCurrentGUI();
				}
			}
		} else {
			while (enemyTurn) {
				// Hard AI uses strategic positioning for hits
				int[] x = hardAI.getXYCoords();
				int i = x[0];
				int j = x[1];

				Square sq = playerBoard.getSquare(i, j);
				enemyTurn = sq.hit();
				if (enemyTurn) {
					hardAI.reportHit(i, j);
					continue;
				} else {
					hardAI.reportMiss(i, j);
				}

				if (playerBoard.ships == 0) {
					System.out.println("YOU LOSE");
					userPlayer.setWon(false);
					userPlayer.setDifficulty("hard");
					mainGUI.setWOrL(false);
					mainGUI.incrementCurrentGUI();
				}
			}
		}
	}

	public void startGame() {
		// Initialize ship placement for enemy board with random positions and orientations
		int[] shipSizes = new int[] { 5, 4, 3, 3, 2 };
		for (int type : shipSizes) {
			boolean placed = false;
			while (!placed) {
				int x = random.nextInt(10);
				int y = random.nextInt(10);
				placed = enemyBoard.placeShip(new Ship(type, Math.random() < 0.5), x, y);
			}
		}

		running = true; // Set game as running
	}

	public Board getPlayerBoard() {
		return this.playerBoard;
	}

	public Board getEnemyBoard() {
		return this.enemyBoard;
	}
}
