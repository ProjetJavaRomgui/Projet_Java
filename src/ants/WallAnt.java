package ants;

import core.Ant;
import core.AntColony;

/**
 * An ant who block the way of the bees with his mighty body 
 *
 * @author YOUR NAME HERE
 */
public class WallAnt extends Ant {
	
	private boolean oldBuff = false;
	/** 
	 * Create a new WallAnt
	 */
	public WallAnt () {
		super(4);
		this.foodCost = 4;
		level = 1; // Disponible au bout de 50 tours

		this.name = "Wall Ant";
		this.description = "Bees cannot pass through this ant until it die !\nThe original <<you'll have to pass over my body>>"
;
		
	}

	@Override
	public void action(AntColony colony) {
		// TODO Auto-generated method stub
		if(buff && !oldBuff){
			this.armor = this.armor*3;
			this.initArmor = this.initArmor*3;
		}
		if(!buff && oldBuff){
			this.initArmor = this.initArmor/3;
			this.armor = this.armor/3+1;
		}
		oldBuff = buff;
	}
}