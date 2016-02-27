package ants;

import core.AntColony;
import core.Bee;

public class StunThrowerAnt extends ThrowerAnt {
	public StunThrowerAnt() {
		super(1);
		this.foodCost=25;
		this.damage=0;
		this.level=3;
		this.name = "Stun Thrower Ant";
		this.description = "Particulary violent,\nthis ant can stun bees.";
	}
	
	/**
	 * Returns a target for this ant
	 *
	 * @return A bee to target
	 */
	public Bee getTarget () {
		return place.getClosestBee(0, 3);
	}

	@Override
	public void action (AntColony colony) {
		Bee target = getTarget();
		if (target != null) {
			if (this.buff) target.stun(2);
			else target.stun(1);
		}
		
		
	}
}
