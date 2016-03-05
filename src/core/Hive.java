package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a hive--which contains the bees that will attack!
 *
 */
public class Hive extends Place {

	public static final String NAME = "Hive";

	private int beeArmor; // armor for all the bees
	private Map<Integer, Bee[]> waves; // a mapping from attack times to the list of bees that will charge in

	/**
	 * Creates a new hive, in which Bees have the given armor
	 *
	 * @param beeArmor
	 *            The armor of the bees
	 */
	public Hive () {
		super(NAME, null,-1,-1,null);
		this.beeArmor = 1;
		waves = new HashMap<Integer, Bee[]>();
	}

	/**
	 * Moves in the invaders who are attacking the colony at the given time.
	 *
	 * @param colony
	 *            The colony to attack
	 * @param currentTime
	 *            The current time
	 * @return An array of the bees who invaded (for animation/processing)
	 */
	public Bee[] invade (AntColony colony, int currentTime) {
		Place[] exits = colony.getBeeEntrances();

		Bee[] wave = waves.get(currentTime);
		if (wave == null) {
			return new Bee[0]; // return empty set if no bees attacking now
		}

		for (Bee b : wave) // move all the bees in
		{
			int randExit = (int) (Math.random() * exits.length);
			b.moveTo(exits[randExit]); // move b to a random exit from the hive (entrance to the colony)
		}
		return wave; // return who invaded
	}

	/**
	 * Adds a wave of attacking bees to this hive
	 *
	 * @param attackTime
	 *            When the bees will attack
	 * @param numBees
	 *            The number of bees to attack
	 */
	public void addWave (int attackTime, int numBees, int beeArmor) {
		this.beeArmor = beeArmor;
		Bee[] bees = new Bee[numBees];
		for (int i = 0; i < bees.length; i++) {
			bees[i] = new Bee(beeArmor);
			this.addInsect(bees[i]); // put the bee in Place
		}
		waves.put(attackTime, bees);
	}

	/**
	 * Returns an array of all the bees who are part of the attack (whether they are currently in the hive or not!)
	 *
	 * @return An array of Bees
	 */
	public Bee[] getAllBees () {
		ArrayList<Bee> beesRet = new ArrayList<Bee>(); // easy temp work
		for (Bee bee : bees) {
			beesRet.add(bee);
		}
		return beesRet.toArray(new Bee[0]);
	}

	
}
