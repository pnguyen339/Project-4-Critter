package assignment4;
/* CRITTERS Critter.java
 * EE422C Project 4 submission by
 * Replace <...> with your actual data.
 * <Student1 Name>
 * <Student1 EID>
 * <Student1 5-digit Unique No.>
 * <Student2 Name>
 * <Student2 EID>
 * <Student2 5-digit Unique No.>
 * Slip days used: <0>
 * Fall 2016
 */


import java.util.List;
import java.util.*;
/* see the PDF for descriptions of the methods and fields in this class
 * you may add fields, methods or inner classes to Critter ONLY if you make your additions private
 * no new public, protected or default-package code or data can be added to Critter
 */


public abstract class Critter {
	private static String myPackage;
	private	static List<Critter> population = new java.util.ArrayList<Critter>();
	private static List<Critter> babies = new java.util.ArrayList<Critter>();
	//protected static ArrayList<ArrayList<Critter>> location = new ArrayList<ArrayList<Critter>>();
	protected static HashMap<int[2], ArrayList<Critter>> world = new HashMap<Vector, ArrayList<Critter>>();
	private boolean move;
	private static boolean fightTime = false;
	// Gets the package name.  This assumes that Critter and its subclasses are all in the same package.
	static {
		myPackage = Critter.class.getPackage().toString().split(" ")[1];
	}
	
	private static java.util.Random rand = new java.util.Random();
	public static int getRandomInt(int max) {
		return rand.nextInt(max);
	}
	
	public static void setSeed(long new_seed) {
		rand = new java.util.Random(new_seed);
	}
	
	
	/* a one-character long string that visually depicts your critter in the ASCII interface */
	public String toString() { return ""; }
	
	private int energy = 0;
	protected int getEnergy() { return energy; }
	
	private int x_coord;
	private int y_coord;
	
	
	private final void move(int direction, int numMove) {
		 
		int[2] posOrig = {this.x_coord, this.y_coord}; //remove the critter from its current position on the Map
		
		
		switch(direction) {
			case 0: 
				this.x_coord = (this.x_coord + numMove)%Params.world_width;
				break;
			
			case 1:
				this.x_coord = (this.x_coord + numMove)%Params.world_width;
				this.y_coord = (this.y_coord - numMove);
				while(this.y_coord < 0)
					this.y_coord += Params.world_height;
				break;

			case 2:
				this.y_coord = (this.y_coord - numMove);
				while(this.y_coord < 0)
					this.y_coord += Params.world_height;
				break;

			case 3:
				this.x_coord = (this.x_coord - numMove);
				while(this.x_coord < 0)
					this.x_coord += Params.world_width;
				this.y_coord = (this.y_coord - numMove);
				while(this.y_coord < 0)
					this.y_coord += Params.world_height;
				break;

			case 4:
				this.x_coord = (this.x_coord - numMove);
				while(this.x_coord < 0)
					this.x_coord += Params.world_width;
				break;

			case 5:
				this.x_coord = (this.x_coord - numMove);
				while(this.x_coord < 0)
					this.x_coord += Params.world_width;
				this.y_coord = (this.y_coord + numMove)%Params.world_height;
				break;

			case 6:
				this.y_coord = (this.y_coord + numMove)%Params.world_height;
				break;

			case 7:
				this.x_coord = (this.x_coord + numMove)%Params.world_width;
				this.y_coord = (this.y_coord + numMove)%Params.world_height;
				break;
		}
		
		if(fightTime) {
			int[2] posNew = {this.x_coord, this.y_coord};
			if(!world.containsKey(posNew)) {
				this.x_coord = posOrig[0];
				this.y_coord = posOrig[1];
				return;
			}
		}

		if(world.get(posOrig).size() == 1) {
			world.remove(posOrig);
		}
		else {
			
			int i =world.get(posOrig).indexOf(this);
			world.get(posOrig).remove(i);
		}
		this.posUpdate();
		
	}
	
	private final int battle(Critter opponent) {
		boolean fighter0 = this.fight(opponent.toString());
		boolean fighter1 = opponent.fight(this.toString());
		if(this.x_coord == opponent.x_coord  && this.y_coord == opponent.y_coord) {
			
			int attack0 = 0;
			if (fighter0) 
				attack0 = Critter.getRandomInt(this.getEnergy());
			
			int attack1 = 0;
			if(fighter1) 
				attack1 = Critter.getRandomInt(opponent.getEnergy());
			
			if(attack0 > attack1)
				this.energy += opponent.energy/2;
				return 1;
			else
				opponent.energy += this.energy/2;
				return 0;
		}
	}
	

	private final void fightCheck() {
			for(int[2] loc: world.keySet()) {
				ArrayList<Critter> fighter = world.get(loc);
				while(fighter.size() > 1) {
					int dead = fighter.get(0).battle(fighter.get(1));
					fighter.remove(dead);
				}
			}
	}

	private final void posUpdate() { //update the Map with the critter new position
		int[2] pos = { this.x_coord, this.y_coord};
		ArrayList<Critter> crit;

		if(world.containsKey(pos)) {
			crit = world.getKey(pos);
			crit.add(me);
		}
		else {
			crit = new ArrayList<Critter>();
			crit.add(me);
			world.put(pos, crit);
		}
	}

	protected final void walk(int direction) {
		if(!this.move) {
			
			this.move(direction, 1);
			this.energy -= Params.walk_energy_cost;
			this.move = true;
		}
	}
	
	protected final void run(int direction) {
		if(!this.move) {
			
			this.move(direction, 2);
			this.energy -= Params.run_energy_cost;
			this.move = true;
		}
	}
	
	private final void insertWorld(Critter critt) {
		int[2] loc = {critt.x_coord, critt.y_coord};
		ArrayList<Critter> crit;

		if(world.containsKey(loc)) {
			crit = world.getKey(loc);
			crit.add(critt);
		}
		else {
			crit = new ArrayList<Critter>();
			world.put(loc, crit);
		}

	}

	protected final void reproduce(Critter offspring, int direction) {
		Constructor<?> constructor = this.getConstructor();
		Object instanceOfOffspring = constructor.newInstance();
		
		offspring = (Critter) instanceOfOffspring;
		offspring.x_coord = Critter.getRandomInt(Params.world_width);
		offspring.y_coord = Critter.getRandomInt(Params.world_height);
		offspring.energy = this.energy/2;
		offspring.move = false;
		
		this.energy -= offspring.energy;
		
		babies.add(offspring);
	}

	public abstract void doTimeStep();
	public abstract boolean fight(String opponent);
	
	/**
	 * create and initialize a Critter subclass.
	 * critter_class_name must be the unqualified name of a concrete subclass of Critter, if not,
	 * an InvalidCritterException must be thrown.
	 * (Java weirdness: Exception throwing does not work properly if the parameter has lower-case instead of
	 * upper. For example, if craig is supplied instead of Craig, an error is thrown instead of
	 * an Exception.)
	 * @param critter_class_name
	 * @throws InvalidCritterException
	 */
	public static void makeCritter(String critter_class_name) throws InvalidCritterException {
		Class<?> myCritter = null;
		Constructor<?> constructor = null;
		Object instanceOfMyCritter = null;

		try {
			myCritter = Class.forName(critter_class_name); 	// Class object of specified name
		} catch (ClassNotFoundException e) {
			throw new InvalidCritterException(critter_class_name);
		}
		try {
			constructor = myCritter.getConstructor();		// No-parameter constructor object
			instanceOfMyCritter = constructor.newInstance();	// Create new object using constructor
		} catch (NoSuchMethodException n) {
			throw new InvalidCritterException(critter_class_name);

		}
		Critter me = (Critter)instanceOfMyCritter;		// Cast to Critter
		me.x_coord = Critter.getRandomInt(Params.world_width);
		me.y_coord = Critter.getRandomInt(Params.world_height);
		me.energy = Params.start_energy;	
		me.move = false;
		insertWorld(me);
		
	}
	
	/**
	 * Gets a list of critters of a specific type.
	 * @param critter_class_name What kind of Critter is to be listed.  Unqualified class name.
	 * @return List of Critters.
	 * @throws InvalidCritterException
	 */
	public static List<Critter> getInstances(String critter_class_name) throws InvalidCritterException {
		List<Critter> result = new java.util.ArrayList<Critter>();
	
		return result;
	}
	
	/**
	 * Prints out how many Critters of each type there are on the board.
	 * @param critters List of Critters.
	 */
	public static void runStats(List<Critter> critters) {
		System.out.print("" + critters.size() + " critters as follows -- ");
		java.util.Map<String, Integer> critter_count = new java.util.HashMap<String, Integer>();
		for (Critter crit : critters) {
			String crit_string = crit.toString();
			Integer old_count = critter_count.get(crit_string);
			if (old_count == null) {
				critter_count.put(crit_string,  1);
			} else {
				critter_count.put(crit_string, old_count.intValue() + 1);
			}
		}
		String prefix = "";
		for (String s : critter_count.keySet()) {
			System.out.print(prefix + s + ":" + critter_count.get(s));
			prefix = ", ";
		}
		System.out.println();		
	}
	
	/* the TestCritter class allows some critters to "cheat". If you want to 
	 * create tests of your Critter model, you can create subclasses of this class
	 * and then use the setter functions contained here. 
	 * 
	 * NOTE: you must make sure that the setter functions work with your implementation
	 * of Critter. That means, if you're recording the positions of your critters
	 * using some sort of external grid or some other data structure in addition
	 * to the x_coord and y_coord functions, then you MUST update these setter functions
	 * so that they correctly update your grid/data structure.
	 */
	static abstract class TestCritter extends Critter {
		protected void setEnergy(int new_energy_value) {
			super.energy = new_energy_value;
		}
		
		protected void setX_coord(int new_x_coord) {
			super.x_coord = new_x_coord;
		}
		
		protected void setY_coord(int new_y_coord) {
			super.y_coord = new_y_coord;
		}
		
		protected int getX_coord() {
			return super.x_coord;
		}
		
		protected int getY_coord() {
			return super.y_coord;
		}
		

		/*
		 * This method getPopulation has to be modified by you if you are not using the population
		 * ArrayList that has been provided in the starter code.  In any case, it has to be
		 * implemented for grading tests to work.
		 */
		protected static List<Critter> getPopulation() {
			return population;
		}
		
		/*
		 * This method getBabies has to be modified by you if you are not using the babies
		 * ArrayList that has been provided in the starter code.  In any case, it has to be
		 * implemented for grading tests to work.  Babies should be added to the general population 
		 * at '''either the beginning OR the end of every timestep.
		 */
		protected static List<Critter> getBabies() {
			return babies;
		}
	}

	/**
	 * Clear the world of all critters, dead and alive
	 */
	public static void clearWorld() {
		world.clear();		

	}
	
	private static void resetMove() {
		for(Critter each: population){
			each.move = false;
		}

		fightTime =false;
	}
	
	public static void worldTimeStep() {
		for(Critter each: population){
			each.doTimeStep();
		}
		fightTime = true;
		
		fightCheck();

		for(Critter each: population){
			each.energy -= Params.rest_energy_cost;
		}

		generateAlgae();

		for(Critter bae: babies) {
			population.add(bae);
			insertWorld(bae);
		}
		babies.clear();

		resetMove();

	}
	
	public static void displayWorld() {
		
	}
}
