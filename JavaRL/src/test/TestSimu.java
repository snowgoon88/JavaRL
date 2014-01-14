/**
 * 
 */
package test;


import simulator.DynSystem;
import simulator.Simulator;
import utils.ParameterFactory;

/**
 * run with --paramFile src/test/paramBatch.txt
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
		DynSystem syst = new DynSystem(null);
		Simulator sim = new Simulator(syst);
		sim.runBatch();
		
		return true;
	}
	/**
	 * Read basic parameters and run in batch mode.
	 * @param args
	 * 
	 * run with --paramFile src/test/paramBatch.txt
	 * 
	 */
	boolean testBatch(String[] args) {
		ParameterFactory param = new ParameterFactory();
		DynSystem syst = new DynSystem(param);
		Simulator sim = new Simulator(syst);
		param.addObjectWithParameters(sim);
		boolean res = param.parse(args);
		if (res) {
			sim.runBatch();	
		}
		else {
			System.err.println("testBatch : parameters not parsed.");
			return res;
		}
		
		return res;
	}
	
	/**
	 * - TODO Experience with scenario
	 */
	
	/** 
	 * - TODO GUI : play/pause reset log {see ExpGUI}
	 */
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// Set up a simple configuration that logs on the console.
		// v1.2 -Dlog4j.configuration=log/log4j1.2.xml
		// v2   -Dlog4j.configurationFile=log/log4j2.xml
		
		TestSimu app = new TestSimu();
		app.run(args);

	}

}
