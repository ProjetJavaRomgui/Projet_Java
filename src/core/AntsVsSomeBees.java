package core;

/**
 *
 */
public class AntsVsSomeBees {

	
	static final boolean DEBUG = true; // True to activate DEBUG mode, with all ants and directly on turn 600
	
	
	public static void main (String[] args) {
		
		AntColony colony = new AntColony(20, 10, 4); // specify the colony ]food, life, difficulty (1-10)]
		new AntGame(colony); // launch the game 
		
	}
	
}
