package core;

/**
 * Represents a Bee
 *
 * @author YOUR NAME HERE
 */
public class Bee extends Insect {

	private static final int DAMAGE = 1;

	/**
	 * Creates a new bee with the given armor
	 *
	 * @param armor
	 *            The bee's armor
	 */
	public Bee (int armor) {
		super(armor);
	}

	/**
	 * Deals damage to the given ant
	 *
	 * @param ant
	 *            The ant to sting
	 */
	public void sting (Ant ant) {
		ant.reduceArmor(DAMAGE);
	}

	/**
	 * Moves to the given place
	 *
	 * @param place
	 *            The place to move to
	 */
	public void moveTo (Place place) {
		this.place.removeInsect(this);
		place.addInsect(this);
	}

	@Override
	public void leavePlace () {
		place.removeInsect(this);
	}

	/**
	 * Returns true if the bee cannot advance (because an ant is in the way)
	 *
	 * @return if the bee can advance
	 */
	public boolean isBlocked () {
		return place.getAnt() != null;
	}

	/**
	 * A bee's action is to sting the Ant that blocks its exit if it is blocked,
	 * otherwise it moves to the exit of its current place.
	 */
	@Override
	public void action (AntColony colony) {
		if (isBlocked()) {
			sting(place.getAnt());
		}
		else if (armor > 0) {
			moveTo(place.getExit());
		}
	}
}
