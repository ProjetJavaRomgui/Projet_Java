package ants;

import core.AntColony;
import core.Bee;

public class ShortThrowerAnt extends ThrowerAnt {
	public ShortThrowerAnt(){
		super(1);
		this.foodCost=10;
		this.damage=2;
		this.level=3; //disponible au bout de 150 tours
		this.name = "Short Thrower Ant";
		this.description = "Like the Thrower Ant. This ant can only cover 2 places";
	}
	/**
	 * Returns a target for this ant
	 *
	 * @return A bee to target
	 */
	public Bee getTarget () {
		return place.getClosestBee(0, 2);
	}

	@Override
	public void action (AntColony colony) {
		Bee target = getTarget();
		if (target != null) {
			target.reduceArmor(getDamage());
		}
	}
}
