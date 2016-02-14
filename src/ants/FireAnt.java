package ants;

import core.Ant;
import core.AntColony;
import core.Bee;

/**
 * This ant is on fiiiiire
 *
 * @author YOUR NAME HERE
 */
public class FireAnt extends Ant {
	public int damage = 3;
	public FireAnt(){
		super(1);
		this.foodCost = 6;
		this.damage = 6;
		
		this.name = "Fire Ant";
		this.description = "Once dead, it explode.\n -6 to insects on the same place.";
	}
	@Override
	public void action(AntColony colony) {
		// this ant is asleep, please does not wake her up	
	}
	
	public Bee[] getBees(){
		return place.getBees();
	}
	
	
	public void reduceArmor (int amount){
		armor -= amount;
		if (armor <= 0) {
			Bee[] beelist = getBees();
			for(int i=0; i<beelist.length ; i++){
				if(beelist[i].getPlace() == this.getPlace()){
					beelist[i].reduceArmor(damage);
				}
			}
			System.out.println(this + " ran out of armor and expired");
			leavePlace();
		}
	}
	
}