package ants;

import core.Ant;
import core.AntColony;
import core.Bee;

/**
 * An ant who throws leaves at bees
 *
 */
public class NinjaThrowerAnt extends Ant {


	/**
	 * Creates a new Thrower Ant.
	 * Armor: 1, Food: 4, Damage: 1
	 */
	public NinjaThrowerAnt () {
		super(1);
		this.foodCost = 150;
		this.damage = 5;
		level = 8; // Disponible au bout de 400 tours
		this.name = "TantK";
		this.description = "This is a result of some colony's military experiments,\n this ant can target every bees in the same line of it.\n In the same time.";
	}
	
	public NinjaThrowerAnt (int foodcost) {
		super(1);
		this.foodCost = foodcost;
	}
	/**
	 * Returns a target for this ant
	 *
	 * @return A bee to target
	 */

	@Override
	public void action (AntColony colony) {
		for(Bee target: colony.getAllBees()){
			if (target.getPlace().left>=place.left && target.getPlace().tunnel==place.tunnel &&  target != null && target.getPlace() != null && target.getPlace().left < 8) {
				target.reduceArmor(getDamage());
			}
		}
		
		
	}
}
