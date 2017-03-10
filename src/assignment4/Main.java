package assignment4;


import java.util.Scanner;
import java.io.*;


/*
 * Usage: java <pkgname>.Main <input file> test
 * input file is optional.  If input file is specified, the word 'test' is optional.
 * May not use 'test' argument without specifying input file.
 */
public class Main {

    static Scanner kb;	// scanner connected to keyboard input, or input file
    private static String inputFile;	// input file, used instead of keyboard input if specified
    static ByteArrayOutputStream testOutputString;	// if test specified, holds all console output
    private static String myPackage;	// package of Critter file.  Critter cannot be in default pkg.
    private static boolean DEBUG = false; // Use it or not, as you wish!
    static PrintStream old = System.out;	// if you want to restore output to console


    // Gets the package name.  The usage assumes that Critter and its subclasses are all in the same package.
    static {
        myPackage = Critter.class.getPackage().toString().split(" ")[1];
    }

    /**
     * Main method.
     * @param args args can be empty.  If not empty, provide two parameters -- the first is a file name, 
     * and the second is test (for test output, where all output to be directed to a String), or nothing.
     */
    public static void main(String[] args) { 
        if (args.length != 0) {
            try {
                inputFile = args[0];
                kb = new Scanner(new File(inputFile));			
            } catch (FileNotFoundException e) {
                System.out.println("USAGE: java Main OR java Main <input file> <test output>");
                e.printStackTrace();
            } catch (NullPointerException e) {
                System.out.println("USAGE: java Main OR java Main <input file>  <test output>");
            }
            if (args.length >= 2) {
                if (args[1].equals("test")) { // if the word "test" is the second argument to java
                    // Create a stream to hold the output
                    testOutputString = new ByteArrayOutputStream();
                    PrintStream ps = new PrintStream(testOutputString);
                    // Save the old System.out.
                    old = System.out;
                    // Tell Java to use the special stream; all console output will be redirected here from now
                    System.setOut(ps);
                }
            }
        } else { // if no arguments to main
            kb = new Scanner(System.in); // use keyboard and console
        }

        /* Do not alter the code above for your submission. */
        /* Write your code below. */
        
        runController(kb);		// why is inputfile even a variable in instance scope? it's used literally twice
        // for that matter, what about old? It's pretty obvious -- it can't even take useful assignment!
        // and testOutputString? Again, used literally twice and nowhere else!
        // it's like the provided code is purposely ridiculous...

        System.out.flush();

    }

	private static void runController(Scanner keyboard) {
		String line = null;
		Scanner input = null;
		
		while (true) {		// loop until exit
			try {
				line = keyboard.nextLine();
				input = new Scanner(line);
				
				String crittertype;
				int optionalnumber;
				
				
				switch (input.next()) {
					case "quit":
						return;
						
					case "show":
						if (input.hasNext()) throw new Exception(); 
						Critter.displayWorld();
						break;
						
					case "step":
						optionalnumber = 1;
						if (input.hasNextInt()) optionalnumber = input.nextInt();
						if (input.hasNext()) throw new Exception();
						
						for (int i = 0; i < optionalnumber; i++) {
							// doTimeStep();	// TODO
						}
						break;
						
					case "seed":
						long seed = input.nextLong();
						if (input.hasNext()) throw new Exception();
						Critter.setSeed(seed);
						break;
						
					case "make":
						crittertype = input.next();
						optionalnumber = 1;
						if (input.hasNextInt()) optionalnumber = input.nextInt();
						if (input.hasNext()) throw new Exception();
						
						for (int i = 0; i < optionalnumber; i++) {
							Critter.makeCritter(crittertype);
						}
						
						break;
						
					case "stats":
						crittertype = input.next();
						if (input.hasNext()) throw new Exception();
						
						// TODO everything
						break;
						
					default:
						System.out.println("invalid command: " + line);
						break;
				}
				
			}
			catch (Exception e) {
				System.out.println("error processing: " + line);
				
			}
			finally {
				input.close();
			}
		}
	}
}
