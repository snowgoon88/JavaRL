/**
 * 
 */
package simulator;

import Jama.Matrix;
import model.CompleteArm;

/**
 * A 'DynSystem' is made of a World and an Agent at least.
 * Compagnion Class to Simulator
 * 
 * @author alain.dutech@loria.fr
 */
public class DynSystem {

//	Agent _agent;
	public CompleteArm _world;

	/**
	 * 
	 */
	public DynSystem() {
		_world = new CompleteArm();
//		_agent = null;
//		_agent.linkToWorld( _world );
//		_world.addAgent( _agent );
	}
	
	/**
	 * Set World in a starting state : position and consigne.
	 */
	public void reset() {
		// Position with no speed.
		_world.setup(Math.toRadians(10), Math.toRadians(25));
		// Set some speed
		_world.getArm().setArmSpeed(new double[] {Math.toRadians(2),0});
	}
	/**
	 * Perception and Decision
	 * @param time of the Simulator
	 */
	public void updateAgents( double time ) {
	}
	
	/**
	 * Use agent decisions to change the state of the World
	 * for the next deltaT simulation time.
	 * @param time of the Simulator
	 * @param deltaT need world Updtated at time+deltaT
	 */
	public void updateWorld( double time, double deltaT ) {
		_world.applyTorque(new Matrix(1,2,0.0), deltaT);
	}
	
	
	
	
}
