package ants;

import core.Ant;
import core.AntColony;
import core.Bee;

/** 
* An ant-shield for ant. So brave
*
*/

public class BodyguardAntBetter extends Ant {

	public BodyguardAntBetter() {
		super(10);
		this.foodCost = 75;
		this.contener = true ;
		
		this.name = "Big guard";
		this.description = "Add an armor of 10.";
	}

	@Override
	public void action(AntColony colony) {
		
	}
	
}