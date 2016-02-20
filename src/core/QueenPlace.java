package core;

import java.util.Arrays;
import java.util.stream.Stream;

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
			Bee[] both = Stream.concat(Arrays.stream(OnQueen), Arrays.stream(AtTheEnd)).toArray(Bee[]::new);
			return both;
		}
		return this.bees.toArray(new Bee[0]);
	}
	
	public void setQueenPlace(Place place){
		queenPosition = place;
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