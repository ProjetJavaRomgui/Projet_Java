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
		this.foodCost = 30;
		this.contener = true ;
	}

	@Override
	public void action(AntColony colony) {
		
	}
	
}