package core;

/**
 * Represents a Bee
 *
 * @author YOUR NAME HERE
 */
public class Bee extends Insect {

	private static final int DAMAGE = 1;
	int randomDecalage = 0;
	int colonyDegat = 1;
	int turns = 0;
	int level = 0;
	int stun = 0; //nombre de tours stun restants
	int slow = 0;
	int slowCount = 0;
	public boolean damageDone = false;
	/**
	 * Creates a new bee with the given armor
	 *
	 * @param armor
	 *            The bee's armor
	 */
	public Bee (int armor) {
		super(armor);
		randomDecalage = (int)(10-Math.random()*20);
		waterSafe = true ;
		
		this.level = armor/5;
	}

	/**
	 * Deals damage to the given ant
	 *
	 * @param ant
	 *            The ant to sting
	 */
	public void sting (Ant ant) {
		ant.reduceArmor(DAMAGE);
		lastAttack=0;
	}

	/**
	 * Moves to the given place
	 *
	 * @param place
	 *            The place to move to
	 */
	public void moveTo (Place place) {
		this.place.removeInsect(this);
		place.addInsect(this);
	}

	@Override
	public void leavePlace () {
		AntGame.DEADBEES++;
		place.removeInsect(this);
	}

	/**
	 * Returns true if the bee cannot advance (because an ant is in the way)
	 *
	 * @return if the bee can advance
	 */
	public boolean isBlocked () {
		if (place.getAnt() != null){
			if(place.getAnt().isBlocking()){
				return true;
			}
		}
		if (place.getContainingAnt() != null){
			return true;
		}
		return false;
	}

	/**
	 * A bee's action is to sting the Ant that blocks its exit if it is blocked,
	 * otherwise it moves to the exit of its current place.
	 */
	@Override
	public void action (AntColony colony) {
		if(stun == 0){
			if(slow > 0){
				slowCount = (slowCount+1)%3;
			}
			if(slow == 0 || slowCount == 0){
				turns++;
				if (isBlocked()) {
					if (place.getContainingAnt() != null){
						sting(place.getContainingAnt());
					} else if (place.getAnt() != null){
						sting(place.getAnt());
					}
				}
				else if (armor > 0 && turns>1) {
					moveTo(place.getExit());
				}
			}
		}
		else stun = stun-1;
		if (slow > 0) slow += -1;
		else slowCount = 0 ;
	}

	public void stun(int i) {
		if (this.stun < i){
			this.stun=i;
		}
	}

	public void slow(int i) {
		if (this.slow < i){
		this.slow = i;
		}
		
	}
	
}
