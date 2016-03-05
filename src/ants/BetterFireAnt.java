package ants;

import core.Ant;
import core.AntColony;
import core.Bee;
import core.Insect;

/**
 * This ant is on fiiiiire
 *
 */
public class BetterFireAnt extends Ant {
	public BetterFireAnt(){
		super(1);
		this.damage = 1000;
		this.foodCost = 60;
		level = 8; // Disponible au bout de 400 tours

		this.name = "Booom Ant";
		this.description = "Be careful ! This is a bombAnt !\nOnce dead, it explode.\n Incects in the same place die,\n and incects just near loose 50% of their life !\n/!\\ CAUTION !";
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
			
			core.AntGame.addBigExplosion(this.place, 100, 20);
			
			Bee[] beelist = getBees();
			for(int i=0; i<beelist.length ; i++){
				if(beelist[i].getPlace() == this.getPlace()){
					beelist[i].reduceArmor(beelist[i].getArmor());
				}
			}
			
			//Incects near loose 50% of their life
			for(Ant ant: place.parent.getAllAnts()){
				if(Math.abs(ant.getPlace().tunnel-place.tunnel)<=1 && Math.abs(ant.getPlace().left-place.left)<=1){
					if(!(ant instanceof QueenAnt)){
						ant.armor += -ant.initArmor/2;
					}
				}
			}
			for(Bee bee: place.parent.getAllBees()){
				if(Math.abs(bee.getPlace().tunnel-place.tunnel)<=1 && Math.abs(bee.getPlace().left-place.left)<=1){
					bee.armor += -bee.initArmor/2;
				}
				
			}
			

			
			System.out.println(this + " ran out of armor and expired");
			leavePlace();

		}
	}
	
}