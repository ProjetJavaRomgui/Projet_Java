package ants;

import core.Ant;
import core.AntColony;
import core.Bee;

/**
 * An ant who throws leaves at bees
 *
 */
public class LongThrowerAnt extends ThrowerAnt {

	/**
	 * Creates a new Thrower Ant.
	 * Armor: 1, Food: 4, Damage: 1
	 */
	public LongThrowerAnt () {
		super(1);
		this.foodCost = 10;
		level = 4; // Disponible au bout de 200 tours
		this.damage = 2;
		this.name = "Long Thrower Ant";
		this.description = "Like the Thrower Ant but with a big thing overhead.\nThis ant can cover 5 places but cannot shoot at short range !";
	}
	
	public LongThrowerAnt (int foodcost) {
		super(1);
		this.foodCost = foodcost;
		this.damage = 2;
	}
	/**
	 * Returns a target for this ant
	 *
	 * @return A bee to target
	 */
	public Bee getTarget () {
		return place.getClosestBee(3, 5);
	}

	@Override
	public void action (AntColony colony) {
		Bee target = getTarget();
		if (target != null) {
			target.reduceArmor(getDamage());
		}
		
		
	}
}
