package core;

/**
 * A driver for the Ants vs. Some-Bees game
 */
public class AntsVsSomeBees {

	public static void main (String[] args) {
		AntColony colony = new AntColony(5, 8, 0, 200, 10); // specify the colony ]tunnels, length, moats, food, life]
		Hive hive = Hive.makeInsaneHive(); // specify the attackers (the hive)
		new AntGame(colony, hive); // launch the game 
	}
}
