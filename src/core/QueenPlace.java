package core;

import java.util.Arrays;

/**
 * Special place for Queen
 *
 */
public class QueenPlace extends Place {
	public static Place queenPosition = null;
	public QueenPlace(String name, Place exit, int tunnel, int left, AntColony parent) {
		super(name, exit, tunnel, left, parent);
	}
	
	public QueenPlace (String name, AntColony parent){
		super(name, parent);
	}
	

	public Bee[] getBees()  {
		if(queenPosition != null){
			Bee[] OnQueen = queenPosition.bees.toArray(new Bee[0]); // Bees on the current queen
			Bee[] AtTheEnd = this.bees.toArray(new Bee[0]); // Bees at the end of the road
			if(OnQueen == null) return AtTheEnd;
			else if(AtTheEnd == null) return OnQueen;
			else {
				int OnQueenLen=OnQueen.length;
				int AtTheEndLen = AtTheEnd.length;
				Bee[] both = new Bee[OnQueenLen + AtTheEndLen];
				System.arraycopy(OnQueen, 0, both, 0, OnQueenLen);
				System.arraycopy(AtTheEnd, 0, both, 0, AtTheEndLen);
				return both;
			}
		}
		return this.bees.toArray(new Bee[0]);
	}
	
	public void setQueenPlace(Place place,AntColony colony){
		queenPosition = place;
		for(Place places:colony.getPlaces()){
			if (places.getExit() == place){
				queenPosition.setEntrance(places);
			}
		}
	}
	
	public Place getQueenPlace (){
		if(queenPosition == null){
			return this;
		}
		return queenPosition;
	}
	
	public boolean hasQueen(){
		if(queenPosition == null) return false ;
		return true ;
	}
}