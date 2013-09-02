/**
 * 
 */
package simulator;

import model.CompleteArm;


/**
 * A 'DynSystem' is made of a World and an Agent at least.
 * Compagnion Class to Simulator
 * 
 * @author alain.dutech@loria.fr
 */
public class DynSystem {

	/** Agent */
	// Agent is linked to CompleteArm and use AgentConsigne for this example.
	public Agent _agent;
	AgentConsigne _consigne = null;
	boolean _decisionFlag = false;
	
	/** World */
	// World is the complete arm for this example
	public CompleteArm _world;

	
	/**
	 * 
	 */
	public DynSystem() {
		_world = new CompleteArm();
		_agent = new Agent( _world );
		_consigne = null;
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
	}
	/**
	 * Perception and Decision
	 * @param time of the Simulator
	 */
	public void updateAgents( double time ) {
		// Agent Perception if not ready or end of current consigne
		if (_consigne == null ) {
			_agent.perceive();
			_consigne = _agent.decide(time);
			_decisionFlag = true;
		}
		else if (_consigne.isStillValid(time) == false) {
			_agent.perceive();
			_consigne = _agent.decide(time);
			_decisionFlag = true;
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
		_world.applyCommand(_consigne.getValConsigne(time), deltaT);
		
		// And the agent learns
		if (_decisionFlag) {
			_agent.learn();
			_decisionFlag = false;
		}
	}
	
	/**
	 * wrapUp world and agent.
	 */
	public void wrapUp() {
		// Print Agent memory
		System.out.println("AGENT MEMORY");
		_agent._memory.dumpDisplay("");
	}
	
}
