package assignment4;

import java.util.List;

/* 
 * Critter2
 * A cowardly critter. Stays still to avoid being found, and tries to run away from every fight!
 * Only tries to reproduce when it's safe -- has at least twice the minimum energy needed
 */
public class Critter2 extends Critter {
	public Critter2() {	}
	
	@Override
	public void doTimeStep() {
		if (getEnergy() > 2 * Params.min_reproduce_energy) {
			Critter1 child = new Critter1();
			reproduce(child, Critter.getRandomInt(8));
		}
	}
	
	@Override
	public boolean fight(String unused) {
		if (getEnergy() > Params.run_energy_cost) {
			run(Critter.getRandomInt(8));
			return false;
		}
		
		return true;
	}
	
	@Override
	public String toString () {
		return "2";
	}
}
