package ants;

public class QueenAnt extends ScubaThrowerAnt {
	
	public QueenAnt() {
		super(6);
		this.landSafe = true;
		this.armor = 10;
		this.initArmor = 10;
		this.damage = 5;
		this.name = "Queen Ant";
		this.description = "This is the queen,\n for now she's deeper in the colony but you can place her.\n If she die, the colony die so be careful !\nHowever she can buff nearest ants !";
	}
}