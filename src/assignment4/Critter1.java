package assignment4;


/* 
 * Critter1
 * A simple critter. Has a randomly generated direction, always walks in that direction.
 * Guaranteed to try to fight -- it can't run away, since it always walks during the time step!
 * Guaranteed to try to reproduce -- why wouldn't it, after all? 
 */
public class Critter1 extends Critter {
	private int direction;
	
	public Critter1() {
		direction = Critter.getRandomInt(8);
	}
	
	@Override
	public void doTimeStep() {
		walk(direction);
		
		if (getEnergy() > Params.min_reproduce_energy) {
			Critter1 child = new Critter1();
			reproduce(child, Critter.getRandomInt(8));
		}
	}
	
	@Override
	public boolean fight(String unused) {
		return true;
	}
	
	@Override
	public String toString () {
		return "1";
	}
}
