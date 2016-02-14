package ants;

import core.Ant;
import core.AntColony;
import core.Bee;

/**
 * An ant who throws leaves at bees
 *
 * @author YOUR NAME HERE
 */
public class LongThrowerAnt extends Ant {

	protected int damage;

	/**
	 * Creates a new Thrower Ant.
	 * Armor: 1, Food: 4, Damage: 1
	 */
	public LongThrowerAnt () {
		super(1);
		this.foodCost = 10;
		damage = 1;
	}
	
	public LongThrowerAnt (int foodcost) {
		super(1);
		this.foodCost = foodcost;
		damage = 1;
	}
	/**
	 * Returns a target for this ant
	 *
	 * @return A bee to target
	 */
	public Bee getTarget () {
		return place.getClosestBee(0, 5);
	}

	@Override
	public void action (AntColony colony) {
		Bee target = getTarget();
		if (target != null) {
			target.reduceArmor(damage);
		}
	}
}
