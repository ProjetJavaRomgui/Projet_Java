package ants;

public class QueenAnt extends ScubaThrowerAnt {
	private static volatile QueenAnt instance = null;
	
	private QueenAnt() {
		super(6);
	}
	
	public final static QueenAnt getInstance() {
		if(QueenAnt.instance == null){
			synchronized(QueenAnt.class) {
				if(QueenAnt.instance == null) {
					QueenAnt.instance = new QueenAnt() ;
				}
			}
		}
		return QueenAnt.instance;
	}
}