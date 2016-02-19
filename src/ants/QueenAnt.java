package ants;

public class QueenAnt extends ScubaThrowerAnt {
	private static boolean alreadyExists = false ;
	
	public QueenAnt() {
		super(6);
		this.name = "Queen Bee";
		if (!alreadyExists){
			alreadyExists = true;
		}
	}
}