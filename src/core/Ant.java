package core;

/**
 * A class representing a basic Ant
 *
 */
public abstract class Ant extends Insect {

	protected int foodCost; // the amount of food needed to make this ant
	protected int foodMakePerTurn;
	protected boolean contener = false ;
	protected boolean blocking = true ;
	public boolean buff = false;
	public int level = 0;
	public boolean DEAD = false;

	/**
	 * Creates a new Ant, with a food cost of 0.
	 *
	 * @param armor
	 *            The armor of the ant.
	 */
	public Ant (int armor) {
		super(armor, null);
		lastAttack = 10000000;
		foodCost = 0;
		foodMakePerTurn = 0;
	}

	/**
	 * Returns the ant's food cost
	 *
	 * @return the ant's food cost
	 */
	public int getFoodCost () {
		return foodCost;
	}

	public int getDamage() {
		if (buff) return damage*2;
		return damage;
	}
	/**
	 * Removes the ant from its current place
	 */
	@Override
	public void leavePlace () {
		DEAD = true;
	}
	
	public void remove () {
		AntGame.DEADANT++;
		place.removeInsect(this);
	}
	
	public boolean isContener() {
		return contener;
	}
	
	public boolean isBlocking() {
		return blocking;
	}

	public boolean getLandSafe() {
		return landSafe;
	}

}
