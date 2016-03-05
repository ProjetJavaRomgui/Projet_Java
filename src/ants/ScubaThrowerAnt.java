package ants;


/**
 * An aquatic ant who throws leaves at bees
 *
 */
public class ScubaThrowerAnt extends ThrowerAnt {
	public ScubaThrowerAnt () {
		super(5);
		waterSafe = true ;
		level = 2; // Disponible au bout de 100 tours
		landSafe = false;
		this.damage = 3;
		this.name = "Scuba Thrower Ant";
		this.description = "The first amphibious thrower ant !\nInvented by Jules VernAnt, a scientist of the colony.";
	}
	public ScubaThrowerAnt (int foodcost) {
		super(foodcost);
		waterSafe = true ;
	}
}