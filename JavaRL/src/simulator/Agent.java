/**
 * 
 */
package simulator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import algorithm.LWRegression;

import utils.JamaU;
import utils.dataStructures.MaxHeap;
import utils.dataStructures.Pair;
import utils.dataStructures.trees.thirdGenKD.KdTree;
import utils.dataStructures.trees.thirdGenKD.SquareEuclideanDistanceFunction;
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
	
	
	/* In order to Log */
	static DecimalFormat df4_2 = new DecimalFormat( "0.000" );
	private static Logger logger = LogManager.getLogger(Agent.class.getName());
	
	public Agent(CompleteArm world) {
		System.out.println(Agent.class.getName());
		this._world = world;
		_dimArm = _world.getArm().getDimension();
		_dimCom = _world.getMuscles().getNbMuscles();
		// State : q,dq,dx
		_state = new Matrix(1,2*_dimArm+2,0.0);
		// Action : u
		_action = null;
		
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
	void perceive(double time) {
		
		// Need to learn
		if (_action != null) {
			learn(time);
		}
		
		// Update State with q,dq
		_state.setMatrix(0, 0, 0, _dimArm-1, _world.getArm().getArmPos());
		_state.setMatrix(0, 0, _dimArm, 2*_dimArm-1, _world.getArm().getArmSpeed());
		// Update current position
		_x = _world.getArm().getArmEndPointMatrix().copy();
		// dx untouched
		
		// Log
		logger.trace("t="+df4_2.format(time)+" S="+JamaU.vecToString(_state));
		logger.trace("t="+df4_2.format(time)+" _x="+JamaU.vecToString(_x));
	}
	/**
	 * Based upon the current perception of the World
	 * @param time is the current simulator time.
	 * @return
	 */
	AgentConsigne decide(double time) {
		// Set up desired dx
		_state.setMatrix(0, 0, 2*_dimArm, 2*_dimArm+1, _goal.minus(_x.getMatrix(0, 0, 0, 1)));

		// Regression to get _action
		// Find the nearest elements in memory
		if (_memory.size() > 5 ) {
			MaxHeap<Pair<Matrix>> nearest = _memory.findNearestNeighbors(_state.getColumnPackedCopy(), 5,  new SquareEuclideanDistanceFunction());
			ArrayList<Matrix> xNearest = new ArrayList<Matrix>();
			ArrayList<Matrix> yNearest = new ArrayList<Matrix>();
			
			int nbRetrieved = nearest.size();
			for( int i=0; i<nbRetrieved; i++) {
				Pair<Matrix> entry = nearest.getMax();
				xNearest.add(entry.first.copy());
				yNearest.add(entry.second.copy());
				nearest.removeMax();
			}
			logger.trace("LWR - build xNearest with "+xNearest.size()+" items");
			LWRegression lwr = new LWRegression();
			lwr._sigma = 1.0;
			try {
				Matrix u = lwr.predict(_state.copy(), xNearest, yNearest);
				_consigne = consigneFromU( u, time, 1.0 );
				logger.trace("LWR u="+JamaU.vecToString(u));
			} catch (Exception e) {
				_consigne = randomConsigne(time, 1.0);
			}
		}
		else {

			// Now use RandomConsigne for 1s.
			_consigne = randomConsigne(time, 1.0);
			
			logger.trace("RND u="+JamaU.vecToString(_consigne.getValConsigne(time)));
		}
		
		_action = _consigne.getValConsigne(time); // to Learn
		
		// Log
		logger.trace("t="+df4_2.format(time)+" S="+JamaU.vecToString(_state));
		logger.trace("t="+df4_2.format(time)+" _x="+JamaU.vecToString(_x));
		logger.trace("t="+df4_2.format(time)+" _action+"+JamaU.vecToString(_action));
		
		return _consigne;
	}
	/**
	 * Called after a decision has been applied on the World.
	 */
	void learn(double time) {
		// Compute realized dx : current.dx - stored.dx
		Matrix dx = _world.getArm().getArmEndPointMatrix().minus(_x);
		_state.setMatrix(0, 0, 2*_dimArm, 2*_dimArm+1, dx);
		
		// Store the result
		_memory.addPoint(_state.getColumnPackedCopy(), new Pair<Matrix>(_state.copy(),_action.copy()));
		
		
		// Log
		logger.trace("t="+df4_2.format(time)+" S="+JamaU.vecToString(_state));
		logger.trace("t="+df4_2.format(time)+" _x="+JamaU.vecToString(_x));
		logger.trace("t="+df4_2.format(time)+" End="+JamaU.vecToString(_world.getArm().getArmEndPointMatrix()));
		logger.trace("t="+df4_2.format(time)+" dx="+JamaU.vecToString(dx));
		
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
			if (prob < 0.25) {
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
	/**
	 * Create a new AgentConsigne from a Matrix of Command.
	 * 
	 * @param time startingTime
	 * @param timeLength duration
	 * 
	 * @return correctl initialized AgentConsigne
	 */
	AgentConsigne consigneFromU( Matrix u, double time, double timeLength) {
		AgentConsigne act = new AgentConsigne(time, timeLength);
		for (int i = 0; i < u.getColumnDimension(); i++) {
			act.setConsigne(i, u.get(0,i));
		}
		return act;
	}
}
