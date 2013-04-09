/**
 * 
 */
package test;

import java.util.ArrayList;
import java.util.Random;

import core.PolicyBasic;
import core.Simulator;
import core.Transition;

import problem.Chain;

/**
 * @author alain.dutech@loria.fr
 *
 */
public class TestChain {

	void testCreation() {
		Random rnd = new Random();
		
		Chain chainPb = new Chain(4);
		System.out.println("*** ChainWalk ***");
		System.out.println(chainPb.dumpModel());
		
		int state = 0;
		int action = 0;
		for (int i = 0; i < 10; i++) {
			action = rnd.nextInt(chainPb._nbAction);
			Transition t = chainPb.simulate(state, action);
			System.out.println(i+" "+t.toString());
			state = t._nextState;
		}
	}
	void testSamples() {
		Chain chainPb = new Chain(4);
		System.out.println("*** ChainWalk ***");
		System.out.println(chainPb.dumpModel());
		
		PolicyBasic pol = new PolicyBasic();
		pol.createUniformRandomPolicy(chainPb._nbState, chainPb._nbAction);
		System.out.println("*** Policy ***\n"+pol.toString());
		
		Simulator simu = new Simulator(chainPb);
		
		ArrayList<Transition> episode = simu.runEpisode(0, pol, 20);
		double sumReward = 0;
		for (Transition t : episode) {
			sumReward += t._reward;
			System.out.println(t.toString());
		}
		System.out.println("Total Reward = "+sumReward);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestChain app = new TestChain();
		app.testSamples();

	}

}
