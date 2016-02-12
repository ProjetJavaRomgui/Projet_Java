package ants;

import core.Ant;
import core.AntColony;
import core.Bee;

/**
 * An aquatic ant who throws leaves at bees
 *
 * @author YOUR NAME HERE
 */
public class ScubaThrowerAnt extends ThrowerAnt {
	public ScubaThrowerAnt () {
		super(5);
		waterSafe = true ;
	}
}