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
	public FireAnt(){
		super(1);
		this.foodCost = 6;
		this.damage = 25;
		level = 5; // Disponible au bout de 250 tours

		this.name = "Fire Ant";
		this.description = "This poor ant explode one dead.\n -25 to insects on the same place.\n(Let it live ! It's too young to die !)";
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
			
			core.AntGame.addExplosion(this.place);
			
			Bee[] beelist = getBees();
			for(int i=0; i<beelist.length ; i++){
				if(beelist[i].getPlace() == this.getPlace()){
					beelist[i].reduceArmor(getDamage());
				}
			}
			System.out.println(this + " ran out of armor and expired");
			leavePlace();

		}
	}
	
}