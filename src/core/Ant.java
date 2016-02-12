package core;

/**
 * A class representing a basic Ant
 *
 * @author YOUR NAME HERE
 */
public abstract class Ant extends Insect {

	protected int foodCost; // the amount of food needed to make this ant
	protected int foodMakePerTurn;
	protected boolean contener = false ;
	protected boolean blocking = true ;
	/**
	 * Creates a new Ant, with a food cost of 0.
	 *
	 * @param armor
	 *            The armor of the ant.
	 */
	public Ant (int armor) {
		super(armor, null);
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

	/**
	 * Removes the ant from its current place
	 */
	@Override
	public void leavePlace () {
		place.removeInsect(this);
	}
	
	public boolean isContener() {
		return contener;
	}
	
	public boolean isBlocking() {
		return blocking;
	}
}
