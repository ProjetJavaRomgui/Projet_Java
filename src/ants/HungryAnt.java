package ants;

import core.Ant;
import core.AntColony;
import core.Bee;

/**
 * An Ant that eat bees (it must stings :/ ) 
 *
 */

public class HungryAnt extends Ant {
	public int Cooldown = 0; //nombre de tours entre deux festins
	public HungryAnt() {
		super(1);
		this.damage = 15;
		this.foodCost = 12;
		level = 6; // Disponible au bout de 300 tours

		this.name = "Hungry Ant";
		this.description = "It was in an psychiatric hospital,\n now it's used here because this ant like to eat bees...\n It take 3 seconds for this ant to eat a bee.";
	}
	
	public Bee getTarget(){
		return place.getClosestBee(0, 0);
	}
	@Override
	public void action(AntColony colony) {
		Bee target = getTarget();
		if(Cooldown!=0) Cooldown -= 1 ;
		else if (target != null){
			target.reduceArmor(getDamage());
			if(target.getArmor()<=0){
				target.invisible = true;
			}
			Cooldown = 3;
		}
		
		
		
	}
	
	
}