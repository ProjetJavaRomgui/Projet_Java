package ants;

import core.Ant;
import core.AntColony;
import core.Bee;

/**
 * An ant who throws leaves at bees
 *
 * @author YOUR NAME HERE
 */
public class LongThrowerAnt extends ThrowerAnt {

	protected int damage;

	/**
	 * Creates a new Thrower Ant.
	 * Armor: 1, Food: 4, Damage: 1
	 */
	public LongThrowerAnt () {
		super(1);
		this.foodCost = 10;
		level = 4; // Disponible au bout de 200 tours

		damage = 1;
		this.name = "Long Thrower Ant";
		this.description = "Like the Thrower Ant this ant can cover 5 places !";
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
