/**
 * 
 */
package test;

import simulator.Parameters;
import simulator.Simulator;

/**
 * @author dutech
 *
 */
public class TestSimu {

	/**
	 * Creation and all the tests
	 */
	public TestSimu() {
		System.out.println("***** TestSimu *****");
	}
		
	public void run(String[] args) {	
		boolean res;
		int nbTest = 0;
		int nbPassed = 0;


		nbTest ++;
		res = testBasic(args);
		if (res) {
			System.out.println("testBasic >> "+res);
			nbPassed ++;
		}
		else {
			System.err.println("testBasic >> "+res);
		}
		nbTest ++;
		res = testArg4j(args);
		if (res) {
			System.out.println("testArgs4j >> "+res);
			nbPassed ++;
		}
		else {
			System.err.println("testArgs4j >> "+res);
		}
		nbTest ++;
		res = testBatch(args);
		if (res) {
			System.out.println("testBatch >> "+res);
			nbPassed ++;
		}
		else {
			System.err.println("testBatch >> "+res);
		}
		
		if (nbTest > nbPassed) {
			System.err.println("FAILURE : only "+nbPassed+" success out of "+nbTest);
			System.exit(1);
		}
		else {
			System.out.println("SUCCESS : "+nbPassed+" success out of "+nbTest);
			System.exit(0);
		}
	}
	
	/**
	 * In Batch Mode for 5 seconds max.
	 * @return
	 */
	boolean testBasic(String[] args) {
		Simulator _sim = new Simulator();
		_sim.reset();
		
		while (_sim._timeSimu < 5.0 ) {
			_sim.step(0.1);
		}
		return true;
	}
	/**
	 * Read basic parameters from the Command Line
	 * @param args
	 * @return true if all parameters set.
	 */
	boolean testArg4j(String[] args) {
		Parameters param = new Parameters();
		param.parseFromCLI(args);
		System.out.println("from CLI  "+param.maxTime+"; "+param.deltaTime);
       
        
        Parameters param2 = new Parameters();
		param2.parseFromFile("src/test/paramFile.txt");
		System.out.println("from FILE "+param2.maxTime+"; "+param2.deltaTime);
        
        
        return true;
	}
	/**
	 * Read basic parameters and run in batch mode.
	 * @param args
	 * @return
	 */
	boolean testBatch(String[] args) {
		Parameters param = new Parameters();
		boolean res = param.parseFromCLI(args);
		
		if (res==true) {
			Simulator sim = new Simulator();
			sim.reset();
			while (sim._timeSimu < param.maxTime ) {
				sim.step(param.deltaTime);
			}
			return true;
		}
		return false;
	}
	
	/**
	 * - batch mode (tMax, DeltaT)
	 * - [Random or Fixed] Agent decides
	 * - log on screen and/or file (name)
	 * - (Parameter in file)
	 */
	
	/**
	 * - Experience with scenario
	 */
	
	/** 
	 * - GUI : play/pause reset log {see ExpGUI}
	 */
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestSimu app = new TestSimu();
		app.run(args);

	}

}
