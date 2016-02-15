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
		this.foodCost = 12;
		level = 6; // Disponible au bout de 300 tours

		this.name = "Hungry Ant";
		this.description = "This ant eat Bees !";
	}
	
	public Bee getTarget(){
		return place.getClosestBee(0, 0);
	}
	@Override
	public void action(AntColony colony) {
		Bee target = getTarget();
		if(Cooldown!=0) Cooldown -= 1 ;
		else if (target != null){
			target.reduceArmor(Math.max(15,target.getArmor()));
			if(target.getArmor()<=0){
				target.invisible = true;
			}
			Cooldown = 3;
		}
		
	}
	
	
}