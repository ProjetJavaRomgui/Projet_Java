package ants;

import core.Ant;
import core.AntColony;
import core.Bee;

/**
 * An invisible ant, will you be able to see it ? 
 *
 */

public class NinjaAnt extends Ant {
	public NinjaAnt() {
		super(1);
		this.foodCost = 40;
		this.blocking=false;
		level = 4; // Disponible au bout de 200 tours
		this.damage = 2;
		this.name = "Ninja Ant";
		this.description = "Bees are stupid,\nthey don't see Ninja ants because their have a ninja costume !";
	}
	
	public Bee[] getBees(){
		return place.getBees();
	}
	
	@Override
	public void action(AntColony colony) {
		Bee[] beelist = getBees();
		for(int i=0; i<beelist.length ; i++){
			if(beelist[i].getPlace() == this.getPlace()){
				beelist[i].reduceArmor(getDamage());
				lastAttack = 0;

			}
		}
		
		
		
	}
	
	
}