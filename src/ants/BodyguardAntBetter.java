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
		level = 6; // Disponible au bout de 300 tours

		this.name = "Big guard";
		this.description = "This isn't a fork,\nit can add an armor of 10 if placed over an ant.";
	}

	@Override
	public void action(AntColony colony) {
		
	}
	
}