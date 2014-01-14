/**
 * 
 */
package simulator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import utils.ParameterFactory;

/**
 * Called with "java .... nomClasseXP params"
 * 
 * Load the given NomClassXP and runBatch with the arguments.
 * 
 * @author alain.dutech@loria.fr
 */
public class Main {

	ParameterFactory _param;
	XPDefault _exp;
	Simulator _sim;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Main(String expName) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// A parameterFactory
		_param = new ParameterFactory();
		
		// XP to run
		
		Class xpClass;
		try {
			xpClass = Main.class.getClassLoader().loadClass(expName);

			Constructor<XPDefault> ctor = xpClass.getDeclaredConstructor(ParameterFactory.class);
			_exp = ctor.newInstance(_param);
		} catch (ClassNotFoundException e) {
			System.err.println("Could not load "+expName);
			e.printStackTrace();
			System.exit(1);
		}
		
		// Simu
		_sim = new Simulator(_exp);
		_param.addObjectWithParameters(_sim);
	}
	
	boolean run(String[] args) {
		boolean res = _param.parse(args);
		if (res) {
			_sim.runBatch();	
		}
		else {
			System.err.println("testBatch : parameters not parsed.");
			return res;
		}
		return res;
	}
	
	/**
	 * @param args
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 */
	public static void main(String[] args) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Main app = new Main(args[0]);
		String[] prunedArgs = Arrays.copyOfRange(args, 1, args.length);
		System.out.println("Running "+"simulator.XP0_TestConsigne");
		app.run(prunedArgs);
	}

}
