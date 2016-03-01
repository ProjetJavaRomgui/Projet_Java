package core;

/**
 * A driver for the Ants vs. Some-Bees game
 */
public class AntsVsSomeBees {
	
	public static void main (String[] args) {
		
		AntColony colony = new AntColony(5, 8, 2, 20, 10, 5); // specify the colony ]tunnels, length, moats, food, life, difficulty (1-10)]
		new AntGame(colony); // launch the game 
		
	}
	
}
