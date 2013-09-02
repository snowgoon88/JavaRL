/**
 * 
 */
package simulator;


import utils.JamaU;
import utils.dataStructures.Pair;
import utils.dataStructures.trees.thirdGenKD.KdTreeIterator;
import Jama.Matrix;

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
			_agent.perceive(time);
			_consigne = _agent.decide(time);
		}
		else if (_consigne.isStillValid(time) == false) {
			_agent.perceive(time);
			_consigne = _agent.decide(time);
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
	}
	
	/**
	 * wrapUp world and agent.
	 */
	public void wrapUp(double time) {
		// Agent learns one last time
		_agent.learn(time);
		// Print Agent memory
		System.out.println("AGENT MEMORY");
		
		for (KdTreeIterator<Pair<Matrix>> it = _agent._memory.getKdTreeIterator(); it.hasNext();) {
			Pair<Matrix> item = it.next();
			String str = "In S="+JamaU.matToString(item.first.getMatrix(0, 0, 0, 3));
			str += " A="+JamaU.matToString(item.second);
			str += " ==> dX="+JamaU.matToString(item.first.getMatrix(0, 0, 4, 5));
			System.out.println(str);
		}
	}
	
}
