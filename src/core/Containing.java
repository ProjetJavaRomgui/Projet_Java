package core;

import ants.QueenAnt;

/**
* Interface pour les fourmis contenantes (like Body Guard or Buff Guard)
*
*/

public class Containing {
	public static boolean canAddContener(Place place) {
		if(place.getContainingAnt() != null){
			System.out.println("erreur : on ne peut mettre deux fourmis contenantes sur la m�me case");
			return false;
		}
		if(place.getAnt() instanceof QueenAnt){
			System.out.println("erreur: la reine a besoin d'un grand espace vital");
			return false;
		}
		return true;
	}
	
	public static boolean canAddContenant(Place place) {
		if(place.getAnt() != null){
			System.out.println("erreur : on ne peut mettre deux fourmis non contenantes sur la m�me case");
			return false;
		}
		return true;
	}
	
	public static Ant getContened(Place place) {
		return place.getAnt();
	}
	
	public static Ant getContainer(Place place) {
		return place.getContainingAnt();
	}
}