package ants;

import core.Ant;
import core.AntColony;
import core.Bee;

/** 
* An ant-shield-buff for ant. So brave
*
*/

public class BuffGuard extends Ant {

	public BuffGuard() {
		super(10);
		this.foodCost = 200;
		this.contener = true;
		level = 10;

		this.name = "Buffer-guard";
		this.description = "Never use the Queen again, buy this awesome all-in-one buffer-guard !\nOnly for 200f !";
	}

	@Override
	public void action(AntColony colony) {
		if(this.place.getAnt()!=null){
			this.place.getAnt().buff = true;
		}
	}
	
}