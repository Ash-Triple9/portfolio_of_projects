package model;

import java.util.Random;

public class RandomAI {
	private Random random = new Random();
	
	public int getXCoords() {
		return random.nextInt(10);
	}
	
	public int getYCoords() {
		return random.nextInt(10);
	}
	
}
