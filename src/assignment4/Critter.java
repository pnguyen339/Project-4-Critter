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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
	protected static HashMap<int[], ArrayList<Critter>> world = new HashMap<int[], ArrayList<Critter>>();
	
	private boolean moved;
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
	
	private static int[] offsetOf(int direction) {
		switch(direction) {
			case 0: 
				return new int[] {1, 0};
		
			case 1:
				return new int[] {1, -1};

			case 2:
				return new int[] {0, -1};

			case 3:
				return new int[] {-1, -1};

			case 4:
				return new int[] {-1, 0};

			case 5:
				return new int[] {-1, 1};

			case 6:
				return new int[] {0, 1};

			case 7:
				return new int[] {1, 1};
			
			default:
				return new int[] {0, 0};
		}
	}
	
	private final void move(int direction, int numMove) {
		 
		int[] posOrig = {this.x_coord, this.y_coord};	// remember current position
		
		int[] offset = offsetOf(direction);				// find where the critter goes, in both x and y
		this.x_coord = (this.x_coord + (numMove * offset[0])) % Params.world_width;
		while (this.x_coord < 0)
			x_coord += Params.world_width;
		
		this.y_coord = (this.y_coord + (numMove * offset[1])) % Params.world_height;
		while (this.y_coord < 0)
			y_coord += Params.world_height;
		
		if(fightTime) {									// avoid collisions if in fight stage
			int[] posNew = {this.x_coord, this.y_coord};
			if(world.get(posNew).size() > 0) {
				this.x_coord = posOrig[0];
				this.y_coord = posOrig[1];
				return;
			}
		}

		if(world.get(posOrig).size() == 1) {			// if move is successful, remove from old place
			world.remove(posOrig);
		}
		else {
			world.get(posOrig).remove(this);
		}
		
		insertWorld(this);								// add to new place
		
	}
	
	private final static void battle(Critter fighter1, Critter fighter2) {
		boolean fights1 = fighter1.fight(fighter2.toString());
		boolean fights2 = fighter2.fight(fighter1.toString());
		
		if(fighter1.x_coord == fighter2.x_coord  && fighter1.y_coord == fighter2.y_coord) {
			int attack1 = 0;
			if (fights1) 
				attack1 = Critter.getRandomInt(fighter1.energy);
			
			int attack2 = 0;
			if(fights2) 
				attack2 = Critter.getRandomInt(fighter2.energy);

			if(attack1 < attack2) {
				fighter2.energy += fighter1.energy / 2;
				killCritter(fighter1);
			}
			else {
				fighter1.energy += fighter2.energy / 2;
				killCritter(fighter2);
			}
		}
	}
	

	private final static void fightCheck() {
			for(int[] loc : world.keySet()) {
				ArrayList<Critter> fighters = world.get(loc);
				while(fighters.size() > 1) {
					battle(fighters.get(0), fighters.get(1));
				}
			}
	}
	
	private static void killCritter(Critter ded) {
		population.remove(ded);
		world.get(new int[] {ded.x_coord, ded.y_coord}).remove(ded);
	}
	

	protected final void walk(int direction) {
		if(!this.moved) {
			
			this.move(direction, 1);
			this.energy -= Params.walk_energy_cost;
			this.moved = true;
		}
		
		if (this.energy <= 0) {
			killCritter(this);
		}
	}
	
	protected final void run(int direction) {
		if(!this.moved) {
			
			this.move(direction, 2);
			this.energy -= Params.run_energy_cost;
			this.moved = true;
		}		
		
		if (this.energy <= 0) {
			killCritter(this);
		}
	}
	
	private final static void insertWorld(Critter critt) {
		int[] loc = {critt.x_coord, critt.y_coord};
		ArrayList<Critter> crit;

		if(world.containsKey(loc)) {
			crit = world.get(loc);
			crit.add(critt);
		}
		else {
			crit = new ArrayList<Critter>();
			world.put(loc, crit);
		}
	}

	protected final void reproduce(Critter offspring, int direction) {
		offspring.x_coord = this.x_coord;
		offspring.y_coord = this.y_coord;
		
		int[] offset = offsetOf(direction);
		offspring.x_coord = (offspring.x_coord + offset[0]) % Params.world_width;
		while (offspring.x_coord < 0)
			x_coord += Params.world_width;
		
		offspring.y_coord = (offspring.y_coord + offset[1]) % Params.world_height;
		while (offspring.y_coord < 0)
			y_coord += Params.world_height;
		
		offspring.energy = this.energy/2;
		offspring.moved = false;
		
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
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static void makeCritter(String critter_class_name) throws InvalidCritterException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
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
		me.moved = false;
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
		
		/* TODO debug this in particular */
		for (Critter critter : population) {
			if (critter.getClass().getSimpleName().equals(critter_class_name)) {
				result.add(critter);
			}
		}
		
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
			each.moved = false;
		}
	}
	
	public static void worldTimeStep() {
		for(Critter each: population){
			each.doTimeStep();
		}
		
		fightTime = true;
		fightCheck();
		fightTime = false;

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
	
	private static void generateAlgae() {
		// TODO Auto-generated method stub
		Critter newAlg;
		for(int i = 1; i <= Params.refresh_algae_count; i++) {
			newAlg = (Critter) (new Algae());
			newAlg.x_coord = Critter.getRandomInt(Params.world_width);
			newAlg.y_coord = Critter.getRandomInt(Params.world_height);
			newAlg.energy = Params.start_energy;
			babies.add(newAlg);
		}
		
	}

	public static void displayWorld() {
		String head = "+";									// build the head, +---+
		for (int i = 0; i < Params.world_width; i++) {
			head = head + "-";
		}
		head = head + "+";
		
		System.out.println(head);							// print the head
		for (int y = 0; y < Params.world_height; y++) {
			System.out.print("|");							// print the vertical lines, then the row contents
			for (int x = 0; x < Params.world_width; x++) {
				ArrayList<Critter> critters = world.get(new int[] {x, y});
				
				if (critters != null && critters.size() > 0) 
					System.out.print(critters.get(0).toString());
			}
			System.out.println("|");						// print the ending vertical line, and newline
		}
		System.out.println(head);							// end by printing the head again
	}
}
