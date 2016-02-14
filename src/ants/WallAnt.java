package ants;

import core.Ant;
import core.AntColony;

/**
 * An ant who block the way of the bees with his mighty body 
 *
 * @author YOUR NAME HERE
 */
public class WallAnt extends Ant {
	
	/** 
	 * Create a new WallAnt
	 */
	public WallAnt () {
		super(4);
		this.foodCost = 4;

		this.name = "Wall Ant";
		this.description = "Bees cannot pass through this ant until it die !";
		
	}

	@Override
	public void action(AntColony colony) {
		// TODO Auto-generated method stub
		
	}
}