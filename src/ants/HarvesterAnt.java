package ants;

import java.awt.Point;

import core.Ant;
import core.AntColony;

/**
 * An Ant that harvests food
 *
 * @author YOUR NAME HERE
 */
public class HarvesterAnt extends Ant {

	/**
	 * Creates a new Harvester Ant
	 */
	public HarvesterAnt () {
		super(1);
		this.foodCost = 2;
		this.foodMakePerTurn = 1;
		
		this.name = "Harvester Ant";
		this.description = "This ant make 3 food each 5 seconds.";
		
	}

	@Override
	public void action (AntColony colony) {
		
	}
}
