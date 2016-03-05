package core;

import java.util.ArrayList;

/**
 * An entire colony of ants and their tunnels.
 *
 */
public class AntColony {

	public static final String QUEEN_NAME = "AntQueen"; // name of the Queen's place
	public static final int MAX_TUNNEL_LENGTH = 8;
	public int life = 10;
	public int lastAttacked = 1000;
 
	private int food; // amount of food available
	QueenPlace queenPlace; // where the queen is
	private ArrayList<Place> places; // the places in the colony
	private ArrayList<Place> beeEntrances; // places which bees can enter (the starts of the tunnels)

	/**
	 * Creates a new ant colony with the given layout.
	 *
	 * @param numTunnels
	 *            The number of tunnels (paths)
	 * @param tunnelLength
	 *            The length of each tunnel
	 * @param moatFrequency
	 *            The frequency of which moats (water areas) appear. 0 means that there are no moats
	 * @param startingFood
	 *            The starting food for this colony.
	 */
	public AntColony (int startingFood, int life, int difficulty) {
		// simulation values
		
		int lineWater = 0;
		food = startingFood;

		this.life = life;
		// init variables
		places = new ArrayList<Place>();
		beeEntrances = new ArrayList<Place>();
		queenPlace = new QueenPlace(QUEEN_NAME, this); // magic variable namexw
		int tunnelLength = Math.min(8, MAX_TUNNEL_LENGTH)+1; // don't go off the screen!
		// set up tunnels, as a kind of linked-list
		Place curr, prev; // reference to current exit of the tunnel
		for (int tunnel = 0; tunnel < 5; tunnel++) {
			curr = queenPlace; // start the tunnel's at the queen
			
				
				lineWater = 0;

				if(tunnel==2 && difficulty<5){
					lineWater = 1000;
				}
				
				for (int step = 0; step < tunnelLength+1; step++) {
					
					prev = curr; // keep track of the previous guy (who we will exit to)
					if (Math.random()<1-(float)difficulty/10 || lineWater>=tunnelLength*(float)difficulty/10+1 || step>=5-1){
						curr = new Place("tunnel[" + tunnel + "-" + step + "]", prev, tunnel, step, this); // create new place with an exit that is the previous spot
					} else {
						curr = new Water("water[" + tunnel + "-" + step + "]", prev, tunnel, step, this);
						lineWater++;
					}
					prev.setEntrance(curr); // the previous person's entrance is the new spot
					places.add(curr); // add new place to the list
				}
			
			beeEntrances.add(curr); // current place is last item in the tunnel, so mark that it is a bee entrance
		} // loop to next tunnel

	}

	/**
	 * Returns an array of Places in this colony. Places are ordered by tunnel, with each tunnel's places listed start to end.
	 *
	 * @return The tunnels in this colony
	 */
	public Place[] getPlaces () {
		return places.toArray(new Place[0]);
	}

	/**
	 * Returns an array of places that the bees can enter into the colony
	 *
	 * @return Places the bees can enter
	 */
	public Place[] getBeeEntrances () {
		ArrayList<Place> beeE = new ArrayList<Place>(); // the places in the colony
		
		int i =0;
		for(Place pl: beeEntrances){
			if(i>=core.AntGame.minTunnel && i<=core.AntGame.maxTunnel){
				beeE.add(pl);
			}
			i++;
		}
		
		return beeE.toArray(new Place[0]);
	}

	/**
	 * Returns the queen's location
	 *
	 * @return The queen's location
	 */
	public Place getQueenPlace () {
		return queenPlace.getQueenPlace();
	}

	/**
	 * Returns the amount of available food
	 *
	 * @return the amount of available food
	 */
	public int getFood () {
		return food;
	}

	/**
	 * Increases the amount of available food
	 *
	 * @param amount
	 *            The amount to increase by
	 */
	public void increaseFood (int amount) {
		food += amount;
	}

	/**
	 * Returns if there are any bees in the queen's location (and so the game should be lost)
	 *
	 * @return if there are any bees in the queen's location
	 */
	public boolean queenHasBees () {
		return queenPlace.getBees().length > 0;
	}

	// place an ant if there is enough food available
	/**
	 * Places the given ant in the given tunnel IF there is enough available food. Otherwise has no effect
	 *
	 * @param place
	 *            Where to place the ant
	 * @param ant
	 *            The ant to place
	 */
	public void deployAnt (Place place, Ant ant) {
		if (food >= ant.getFoodCost()) {
			if(ant.name == "Queen Ant"){
				if(!queenPlace.hasQueen()){
					if(place.addInsect(ant)) {
						food -= ant.getFoodCost();
						queenPlace.setQueenPlace(place, this); 
					}
				}
				else System.out.println("There can t be more than one queen !");
			}
			else if(place.addInsect(ant)) food -= ant.getFoodCost();
		}
		else {
			System.out.println("Not enough food remains to place " + ant);
		}
	}

	/**
	 * Removes the ant inhabiting the given Place
	 *
	 * @param place
	 *            Where to remove the ant from
	 */
	public void removeAnt (Place place) {
		if (place.getAnt() != null && place != queenPlace.getQueenPlace()) {
			place.removeInsect(place.getAnt());
		}
	}

	/**
	 * Returns a list of all the ants currently in the colony
	 *
	 * @return a list of all the ants currently in the colony
	 */
	public ArrayList<Ant> getAllAnts () {
		ArrayList<Ant> ants = new ArrayList<Ant>();
		for (Place p : places) {
			if (p.getAnt() != null) {
				ants.add(p.getAnt());
			}
			if (p.getContainingAnt() != null) {
				ants.add(p.getContainingAnt());
			}
		}
		return ants;
	}

	/**
	 * Returns a list of all the bees currently in the colony
	 *
	 * @return a list of all the bees currently in the colony
	 */
	public ArrayList<Bee> getAllBees () {
		ArrayList<Bee> bees = new ArrayList<Bee>();
		for (Place p : places) {
			for (Bee b : p.getBees()) {
				bees.add(b);
			}
		}
		return bees;
	}
	

	@Override
	public String toString () {
		return "Food: " + food + "; " + getAllBees() + "; " + getAllAnts();
	}
}
