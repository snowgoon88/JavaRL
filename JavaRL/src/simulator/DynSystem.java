/**
 * 
 */
package simulator;

import java.io.IOException;

import Jama.Matrix;
import model.CommandSequence;
import model.CompleteArm;
import model.Consignes;

/**
 * A 'DynSystem' is made of a World and an Agent at least.
 * Compagnion Class to Simulator
 * 
 * @author alain.dutech@loria.fr
 */
public class DynSystem {

	/** Agent */
	// Agent is a set of Consigne for this example
	public Consignes _agent;
	Matrix _u = null;
	
	/** World */
	// World is the complete arm for this example
	public CompleteArm _world;

	/**
	 * 
	 */
	public DynSystem() {
		_world = new CompleteArm();
//		_agent = null;
//		_agent.linkToWorld( _world );
//		_world.addAgent( _agent );
		_agent = new Consignes(_world.getArrayNeuroControlers().length);
		_u = new Matrix(1,_world.getArrayNeuroControlers().length, 0.0);
		try {
			_agent.read("src/test/consigne_agent.data");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Set World in a starting state : position and consigne.
	 */
	public void reset() {
		// Position with no speed.
		_world.setup(Math.toRadians(10), Math.toRadians(25));
		// Set some speed
		_world.getArm().setArmSpeed(new double[] {Math.toRadians(2),0});
	
		// Agent
		// Un vecteur (Matrix 1x6) de consignes musculaires, initialisée à 0.0.
		for (int i = 0; i < 6; i++) {
			_u.set(0,i, 0.0);
		}
	}
	/**
	 * Perception and Decision
	 * @param time of the Simulator
	 */
	public void updateAgents( double time ) {
		// Agent
		for (int i = 0; i < _agent.size(); i++) {
			CommandSequence cs = _agent.get(i);
			// la valeur de la consigne est copiée dans le vecteur u
			_u.set(0,i, cs.getValAtTimeFocussed(time));
		}
	}
	
	/**
	 * Use agent decisions to change the state of the World
	 * for the next deltaT simulation time.
	 * @param time of the Simulator
	 * @param deltaT need world Updated at time+deltaT
	 */
	public void updateWorld( double time, double deltaT ) {
		// Applique les consignes sur le bras
		_world.applyCommand(_u, deltaT);
		// exmple with 0 torque _world.applyTorque(new Matrix(1,2,0.0), deltaT);
	}
	
	
	
	
}
