package ants;


/**
 * An aquatic ant who throws leaves at bees
 *
 * @author YOUR NAME HERE
 */
public class ScubaThrowerAnt extends ThrowerAnt {
	public ScubaThrowerAnt () {
		super(5);
		waterSafe = true ;
		
		this.name = "Scuba Thrower Ant";
		this.description = "The first amphibious thrower ant !";
	}
}