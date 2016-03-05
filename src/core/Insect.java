package core;

import ants.NenupharAnt;

/**
 * Represents an insect (e.g., an Ant or a Bee) in the game
 *
 */
public abstract class Insect {
	public boolean landSafe = true;
	public int armor; // insect's current armor
	public int initArmor;
	protected Place place; // insect's current location
	public int lastAttacked = 10000;
	public int lastAttack;
	public boolean waterSafe = false;
	public String name = "Thing";
	public String description = "This is an insect, obvious.";
	public boolean invisible = false;
	public int damage = 0;
	int randomDecalage = (int)(10-Math.random()*20);

	/**
	 * Creates a new Insect with the given armor in the given location
	 *
	 * @param armor
	 *            The insect's armor
	 * @param place
	 *            The insect's location
	 */
	public Insect (int armor, Place place) {
		if (armor <= 0) {
			throw new IllegalArgumentException("Cannot create an insect with armor of 0");
		}
		this.initArmor = armor*2;
		this.armor = armor*2;
		this.place = place;
	}

	/**
	 * Creates an Insect with the given armor. The insect's location is null
	 *
	 * @param armor
	 *            The insect's armor
	 */
	public Insect (int armor) {
		this(armor, null);
	}

	/**
	 * Set's the insect's current location
	 *
	 * @param place
	 *            The insect's current location
	 */
	public void setPlace (Place place) {
		this.place = place;
	}

	/**
	 * Return's the insect's current location
	 *
	 * @return the insect's current location
	 */
	public Place getPlace () {
		return place;
	}

	/**
	 * Returns the insect's current armor
	 *
	 * @return the insect's current armor
	 */
	public int getArmor () {
		return armor;
	}

	/**
	 * Reduces the insect's current armor (e.g., through damage)
	 *
	 * @param amount
	 *            The amount to decrease the armor by
	 */
	public void reduceArmor (int amount) {
		armor -= amount;
		if (armor <= 0) {
			System.out.println(this + " ran out of armor and expired");
			leavePlace();
			
		}
		if (!(this instanceof NenupharAnt)){
			lastAttacked = 0;
		}
	}

	/**
	 * Has the insect move out of its current location. Abstract in case the insect takes action when it leaves
	 */
	public abstract void leavePlace ();

	/**
	 * The insect takes an action on its turn
	 *
	 * @param colony
	 *            The colony in which this action takes place (to support wide-spread effects)
	 */
	public abstract void action (AntColony colony);

	@Override
	public String toString () {
		return this.getClass().getName() + "[" + armor + ", " + place + "]"; // supports inheritance!
	}
	
	public boolean getWaterSafe() {
		return waterSafe;
	}
	
	public String getName() {
		return name;
	}
}
