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
		super(3);
		this.foodCost = 10;
		this.contener = true ;
	}

	@Override
	public void action(AntColony colony) {
		
	}
	
}