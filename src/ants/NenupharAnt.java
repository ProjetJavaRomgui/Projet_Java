package ants;

import core.Ant;
import core.AntColony;

public class NenupharAnt extends Ant{

	public NenupharAnt() {
		super(1);
		waterSafe = true;
		blocking = false;
		level = 6;
		damage = 0;
		name = "Nenuphar";
		description = "A Lily flower, ants are light enough to stand on it";
		landSafe = false;
		
	}

	@Override
	public void action(AntColony colony) {
		// TODO Auto-generated method stub
		
	}

}
