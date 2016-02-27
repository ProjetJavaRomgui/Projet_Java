package ants;

import core.AntColony;
import core.Bee;

public class SlowAnt extends ThrowerAnt{
	public SlowAnt(){
		super(1);
		this.foodCost=25;
		this.damage=0;
		this.level=2;
		this.name = "Slow Ant";
		this.description = "Found in a strange blue police cabin (apparently called TantRDIS),\nthis ant can slow bees for seconds !";
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
			if (this.buff) target.slow(6);
			else target.slow(3);
		}
		
		
	}
}
