/**
 * 
 */
package test;

import java.util.ArrayList;

import algorithm.LSPI;
import problem.Chain;
import utils.JamaU;
import core.BasisPolyState;
import core.BasisTabular;
import core.PolicyBasic;
import core.PolicyWeighted;
import core.Simulator;
import core.Transition;

/**
 * @author alain.dutech@loria.fr
 *
 */
public class TestBasis {

	
	void testBasisTabular() {
		System.out.println("*** Basis TABULAR ***");
		BasisTabular base = new BasisTabular(4, 2);
		System.out.println(base.toString(2, 0));
	}
	void testBasisPolyState() {
		System.out.println("*** Basis POLY STATE ***");
		BasisPolyState base = new BasisPolyState(4, 2, 2);
		System.out.println(base.toString(2, 0));
	}
	void testWPolicy() {
		Chain chainPb = new Chain(4);
		BasisPolyState base = new BasisPolyState(chainPb._nbState, chainPb._nbAction, 2);
		
		PolicyWeighted pol = new PolicyWeighted(base, chainPb);
		System.out.println("*** Weighted Policy ***");
		System.out.println(pol.toString());
	}
	void testLSPQfast() {
		Chain chainPb = new Chain(4);
		PolicyBasic polExplore = new PolicyBasic();
		polExplore.createUniformRandomPolicy(chainPb._nbState, chainPb._nbAction);
		
		BasisPolyState base = new BasisPolyState(chainPb._nbState, chainPb._nbAction, 2);
		PolicyWeighted pol = new PolicyWeighted(base, chainPb);
		LSPI algo = new LSPI();
		
		// On veut des samples
		Simulator simu = new Simulator(chainPb);
		ArrayList<Transition> samples = simu.collectSamples(polExplore, 20, 10);
		
		algo.stepLSTDQfast(samples, pol, 0.9, true);
		
		System.out.println("newW=\n"+JamaU.matToString(algo._w));
	}
	void testLSPI() {
		Chain chainPb = new Chain(20);
		chainPb.initReward(true);
		PolicyBasic polExplore = new PolicyBasic();
		polExplore.createUniformRandomPolicy(chainPb._nbState, chainPb._nbAction);
		
		BasisPolyState base = new BasisPolyState(chainPb._nbState, chainPb._nbAction, 4);
		PolicyWeighted pol = new PolicyWeighted(base, chainPb);
		LSPI algo = new LSPI();
		
		// On veut des samples
		Simulator simu = new Simulator(chainPb);
		ArrayList<Transition> samples = simu.collectSamples(polExplore, 5000, 1);
		
		algo.runLSPI(samples, pol, 
				LSPI.AlgoEstimation.LSTDQFAST, 0.9, 20, 0.00001);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestBasis app = new TestBasis();
//		app.testBasisTabular();
//		app.testBasisPolyState();
//		app.testWPolicy();
//		app.testLSPQfast();
		app.testLSPI();
	}

}
