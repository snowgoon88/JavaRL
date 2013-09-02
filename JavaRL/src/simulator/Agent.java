/**
 * 
 */
package simulator;

import java.util.Random;

import utils.dataStructures.Pair;
import utils.dataStructures.trees.thirdGenKD.KdTree;
import Jama.Matrix;
import model.CompleteArm;

/**
 * @author alain.dutech@loria.fr
 */
public class Agent {
	/** World we live in */
	CompleteArm _world;
	/** Dimension of Arm */
	int _dimArm;
	/** Dimension of Command */
	int _dimCom;
	
	
	/** Current State */
	Matrix _state = null;
	/** Current endPoint position */
	Matrix _x = null;
	
	/** Current Action */
	Matrix _action = null;
	/** Current Consigne */
	AgentConsigne _consigne = null;
	
	/** Current Goal */
	Matrix _goal = null;
	
	/** Memory */
	KdTree<Pair<Matrix>> _memory = null;
	
	static Random _rnd = new Random();
	
	public Agent(CompleteArm world) {
		this._world = world;
		_dimArm = _world.getArm().getDimension();
		_dimCom = _world.getMuscles().getNbMuscles();
		// State : q,dq,dx
		_state = new Matrix(1,2*_dimArm+2,0.0);
		// Action : u
		_action = new Matrix(1,_dimCom);
		
		// Memory
		_memory = new KdTree<>(_state.getColumnDimension(), 20); /*dim, sizeBucket */
		
		// Set a goal
		_goal = new Matrix(1, 2);
		_goal.set(0, 0, 0.0); // X
		_goal.set(0, 1, 0.8); // Y
	}
	
	/**
	 * Called when a decision should be made
	 */
	void perceive() {
		// Update State with q,dq
		_state.setMatrix(0, 0, 0, _dimArm-1, _world.getArm().getArmPos());
		_state.setMatrix(0, 0, _dimArm, 2*_dimArm-1, _world.getArm().getArmSpeed());
		// Update current position
		_x = _world.getArm().getArmEndPointMatrix();
		// dx untouched
	}
	/**
	 * Based upon the current perception of the World
	 * @param time is the current simulator time.
	 * @return
	 */
	AgentConsigne decide(double time) {
		// Set up desired dx
		_state.setMatrix(0, 0, 2*_dimArm, 2*_dimArm+1, _goal.minus(_x.getMatrix(0, 0, 0, 1)));
		// TODO Regression to get _action
		// Now use RandomConsigne for 1s.
		_consigne = randomConsigne(time, 1.0);
		_action = _consigne.getValConsigne(time); // to Learn
		
		return _consigne;
	}
	/**
	 * Called after a decision has been applied on the World.
	 */
	void learn() {
		// Compute realized dx : current.dx - stored.dx
		Matrix dx = _world.getArm().getArmEndPointMatrix().minus(_x);
		_state.setMatrix(0, 0, 2*_dimArm, 2*_dimArm+1, dx);
		
		// Store the result
		_memory.addPoint(_state.getColumnPackedCopy(), new Pair<Matrix>(_state,_action));
		
	}
	
	/**
	 * Create a new AgentConsigne at Random :
	 * every Consigne as a 10% change of being set to 0.2.
	 * 
	 * @param time startingTime
	 * @param timeLength duration
	 * 
	 * @return randomly generated AgentConsigne
	 */
	AgentConsigne randomConsigne(double time, double timeLength) {
		
		
		//System.out.println(">>> New AgentConsigne at t="+time+" for "+timeLength+" s.");
		//String str = "";
		AgentConsigne act = new AgentConsigne(time, timeLength);
		for (int i = 0; i < act.size(); i++) {
			double prob = _rnd.nextDouble();
			if (prob < 0.1) {
				act.setConsigne(i, 0.2);
				//str += "cons "+i+" : 0.2 ("+prob+") ";
			}
			else {
				//str += "cons "+i+" : 0.0 ("+prob+") ";
			}
		}
		//System.out.println(">>>> "+str);
		
		return act;
	}
}
