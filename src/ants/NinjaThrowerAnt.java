package ants;

import core.Ant;
import core.AntColony;
import core.Bee;

/**
 * An ant who throws leaves at bees
 *
 * @author YOUR NAME HERE
 */
public class NinjaThrowerAnt extends Ant {

	protected int damage;

	/**
	 * Creates a new Thrower Ant.
	 * Armor: 1, Food: 4, Damage: 1
	 */
	public NinjaThrowerAnt () {
		super(1);
		this.foodCost = 50;
		damage = 1;
		this.name = "Ninja Thrower Ant";
	}
	
	public NinjaThrowerAnt (int foodcost) {
		super(1);
		this.foodCost = foodcost;
		damage = 1;
	}
	/**
	 * Returns a target for this ant
	 *
	 * @return A bee to target
	 */

	@Override
	public void action (AntColony colony) {
		for(Bee target: colony.getAllBees()){
			if (target.getPlace().left>=place.left && target.getPlace().tunnel==place.tunnel &&  target != null) {
				target.reduceArmor(damage);
			}
		}
	}
}
