package core;

/**
 * A driver for the Ants vs. Some-Bees game
 */
public class AntsVsSomeBees {

	public static void main (String[] args) {
		AntColony colony = new AntColony(5, 8, 2, 20, 10, 1); // specify the colony ]tunnels, length, moats, food, life, difficulty (1-10)]
		Hive hive = Hive.makeFullHive(); // specify the attackers (the hive)
		new AntGame(colony, hive); // launch the game 
	}
}
