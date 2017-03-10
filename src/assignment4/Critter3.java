package assignment4;

/*
 * Critter3
 * An evolving critter! Carries random genetic information that can mutate from each offspring.
 * The genetic information decides its probability of running, walking, fighting, etc.  
 * It is, admittedly, a bit of a mess.
 */
public class Critter3 extends Critter {
	private int move_prob;
	private int walk_prob;
	private int pref_dir;
	
	private int rep_prob;
	
	private int fight_prob;
	private int same_species_fight;
	
	public Critter3() {
		move_prob = Critter.getRandomInt(100);
		walk_prob = Critter.getRandomInt(100);
		pref_dir = Critter.getRandomInt(8);
		
		rep_prob = Critter.getRandomInt(100);

		fight_prob = Critter.getRandomInt(100);
		same_species_fight = Critter.getRandomInt(100);
	}
	
	private int absolute(int number) {
		return (number < 0 ? -1 * number : number);
	}
	
	public Critter3(Critter3 other) {
		move_prob = absolute(other.getMoveProb() + Critter.getRandomInt(10) - 5);
		walk_prob = absolute(other.getWalkProb() + Critter.getRandomInt(10) - 5);
		pref_dir = (other.getPrefDir() + Critter.getRandomInt(16)) % 8;
		
		rep_prob = absolute(other.getRepProb() + Critter.getRandomInt(10) - 5);
		
		fight_prob = absolute(other.getFightProb() + Critter.getRandomInt(10) - 5);
		same_species_fight = absolute(other.getSameFight() + Critter.getRandomInt(10) - 5);
	}
	
	public int getMoveProb() { return move_prob; }
	
	public int getWalkProb() { return walk_prob; }
	
	public int getPrefDir() { return pref_dir; }
	
	public int getRepProb() { return rep_prob; }
	
	public int getFightProb() { return fight_prob; }
	
	public int getSameFight() { return same_species_fight; }
	
	private void move() {
		int dir = (pref_dir + Critter.getRandomInt(5) + 6) % 8;		// Find a direction
		
		if (Critter.getRandomInt(100) < walk_prob)					// Then move in it
			walk(dir);
		else 
			run(dir);
	}
	
	@Override
	public void doTimeStep() {
		if (Critter.getRandomInt(100) < move_prob) 
			move();
		
		if (getEnergy() > Params.min_reproduce_energy && Critter.getRandomInt(100) < rep_prob) {
			Critter3 child = new Critter3(this);
			reproduce(child, Critter.getRandomInt(8));
		}
	}
	
	@Override
	public boolean fight(String opponent) {
		int fight = fight_prob;									// find probability of fighting
		
		if (opponent.toString().equals(this.toString())) {
			fight = same_species_fight;
		}
		if (Critter.getRandomInt(100) < fight) {				// roll the dice!
			return true;										// either fight or run away
		}
		else {
			move();
			return false;
		}
	}
	
	@Override
	public String toString () {
		return "3";
	}
}
