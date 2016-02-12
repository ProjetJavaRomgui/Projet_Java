package ants;

import core.Ant;
import core.AntColony;
import core.Bee;

/**
 * An invisible ant, will you be able to see it ? 
 *
 * @author YOUR NAME HERE
 */

public class NinjaAnt extends Ant {
	public int damage = 1;
	public NinjaAnt() {
		super(1);
		this.foodCost = 5;
		this.blocking=false;
	}
	
	public Bee[] getBees(){
		return place.getBees();
	}
	
	@Override
	public void action(AntColony colony) {
		Bee[] beelist = getBees();
		for(int i=0; i<beelist.length ; i++){
			if(beelist[i].getPlace() == this.getPlace()){
				beelist[i].reduceArmor(damage);
			}
		}
		
	}
	
	
}