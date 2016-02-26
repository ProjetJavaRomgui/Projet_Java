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
		level = 2; // Disponible au bout de 100 tours

		this.name = "Little guard";
		this.description = "This beautiful armor can be placed over an other ant.\n It add an armor of 3.";
	}

	@Override
	public void action(AntColony colony) {
		
	}
	
}