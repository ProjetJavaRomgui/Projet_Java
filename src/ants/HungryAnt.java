package ants;

import core.Ant;
import core.AntColony;
import core.Bee;

/**
 * An Ant that eat bees (it must stings :/ ) 
 *
 * @author YOUR NAME HERE
 */

public class HungryAnt extends Ant {
	public int Cooldown = 0; //nombre de tours entre deux festins
	public HungryAnt() {
		super(1);
		this.foodCost = 4;
	}
	
	public Bee getTarget(){
		return place.getClosestBee(0, 0);
	}
	@Override
	public void action(AntColony colony) {
		Bee target = getTarget();
		if(Cooldown!=0) Cooldown -= 1 ;
		else if (target != null){
			target.reduceArmor(target.getArmor());
			Cooldown = 3;
		}
		
	}
	
	
}