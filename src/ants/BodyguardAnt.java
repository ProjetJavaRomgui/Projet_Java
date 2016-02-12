package ants;

import core.Ant;
import core.AntColony;
import core.Bee;

/** 
* An ant-shield for ant. So brave
*
*/

public class BodyguardAnt extends Ant {

	public BodyguardAnt() {
		super(2);
		this.foodCost = 5;
		this.contener = true ;
	}

	@Override
	public void action(AntColony colony) {
		
	}
	
}