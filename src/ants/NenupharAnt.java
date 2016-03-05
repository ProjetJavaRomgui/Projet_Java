package ants;

import core.Ant;
import core.AntColony;

public class NenupharAnt extends Ant{

	public NenupharAnt() {
		super(1);
		waterSafe = true;
		blocking = false;
		level = 10;
		damage = 0;
		foodCost = 200;
		name = "Water Lily";
		description = "A Water Lily, one ant should be light enough to stand on it";
		landSafe = false;
		
	}

	@Override
	public void action(AntColony colony) {
		place.hasNenuphar = true;
		this.reduceArmor(2);
		
	}

}
