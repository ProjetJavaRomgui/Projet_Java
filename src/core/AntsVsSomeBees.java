package core;

/**
 *
 */
public class AntsVsSomeBees {
	
	public static void main (String[] args) {
		
		AntColony colony = new AntColony(20, 10, 4); // specify the colony ]food, life, difficulty (1-10)]
		new AntGame(colony); // launch the game 
		
	}
	
}
