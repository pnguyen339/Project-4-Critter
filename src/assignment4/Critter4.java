package assignment4;

/*
 * Critter4
 * A silly critter. Tries to build a wall out of copies of itself!
 * Always moves in the same direction, but only moves 50% of the time.
 * Tries to reproduce if it has at least twice as much energy as needed. 
 */
public class Critter4 extends Critter {
	private static final int move_prob = 50;
	private static final int dir = 0;
	
	public Critter4() {	}
	
	@Override
	public void doTimeStep() {
		if (Critter.getRandomInt(100) < move_prob) {
			walk(dir);
		}
	}
	
	@Override
	public boolean fight(String opponent) {
		if (opponent.toString().equals(this.toString())) {
			walk(dir);
			return false;
		}
		else return true;
	}
	
	@Override
	public String toString () {
		return "4";
	}
}
