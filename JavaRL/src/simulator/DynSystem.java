/**
 * 
 */
package simulator;


import utils.JamaU;
import utils.ParameterFactory;
import utils.dataStructures.Pair;
import utils.dataStructures.trees.thirdGenKD.KdTreeIterator;

import model.CompleteArm;
import viewer.SCompleteArm;

import Jama.Matrix;


/**
 * A 'DynSystem' is made of a World and an Agent at least.
 * Compagnion Class to Simulator
 * 
 * @author alain.dutech@loria.fr
 */
public class DynSystem extends XPDefault {

	/** Agent */
	// Agent is linked to CompleteArm and use AgentConsigne for this example.
	public Agent _agent;
	AgentConsigne _consigne = null;
	
	/** World */
	// World is the complete arm for this example
	public CompleteArm _world;
	/** Observer as String of the World */
	SCompleteArm _armV;

	
	/**
	 * 
	 */
	public DynSystem(ParameterFactory param) {
		super(param);
		
		_world = new CompleteArm();
		_armV = new SCompleteArm(_world);
		_world.addObserver(_armV);
		
		_agent = new Agent( _world );
		_consigne = null;
	}
	
	/**
	 * Set World in a starting state : position and consigne.
	 */
	public void reset(int indexXP) {
		super.reset(indexXP);
		// Position with no speed.
		_world.setup(Math.toRadians(10), Math.toRadians(25));
		// Set some speed
		_world.getArm().setArmSpeed(new double[] {Math.toRadians(2),0});
	
		// Agent
		_consigne = null;
		
		// Logging
		if (_logScreen ) {
			System.out.println("#"+String.format("%8s", "time")+"\t"+_armV.explainStr);
			System.out.println(df3_5.format(0.0)+"\t"+_armV.viewStr);
		}
		if (_logFilename != "") {
			_logFile.writeLine("#"+String.format("%8s", "time")+"\t"+_armV.explainStr);
			_logFile.write(df3_5.format(0.0)+"\t"+_armV.viewStr);
		}
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
		
		if (_logScreen) {
			System.out.println(df3_5.format(time)+"\t"+_armV.viewStr);
		}
		if (_logFile != null) {
			_logFile.write(df3_5.format(time)+"\t"+_armV.viewStr);
		}
	}
	
	/**
	 * wrapUp world and agent.
	 */
	public void wrapUp(double time) {
		// Agent learns one last time
		_agent.learn(time);
		
		super.wrapUp(time);
	}
	
	/**
	 * Display Agent
	 */
	public void displayAgent() {
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

	@Override
	public void end() {
		displayAgent();
	}
	
}
