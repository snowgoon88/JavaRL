/**
 * 
 */
package test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import problem.Chain;
import algorithm.LSPI;
import core.BasisPolyState;
import core.PolicyBasic;
import core.PolicyWeighted;
import core.Simulator;
import core.Transition;

/**
 * @author alain.dutech@loria.fr
 *
 */
public class TestLSPI {

	Chain _pb;
	
	
	void testLSPI() {
		_pb = new Chain(20);
		_pb.initReward(true);
		PolicyBasic polExplore = new PolicyBasic();
		polExplore.createUniformRandomPolicy(_pb._nbState, _pb._nbAction);
		
		BasisPolyState base = new BasisPolyState(_pb._nbState, _pb._nbAction, 4);
		PolicyWeighted pol = new PolicyWeighted(base, _pb);
		LSPI algo = new LSPI();
		
		// On veut des samples
		Simulator simu = new Simulator(_pb);
		ArrayList<Transition> samples = simu.collectSamples(polExplore, 5000, 1);
		
		ArrayList<PolicyWeighted> resPolFast = algo.runLSPI(samples, pol, 
				LSPI.AlgoEstimation.LSTDQFAST, 0.9, 20, 0.00001);
		ArrayList<PolicyWeighted> resPol = algo.runLSPI(samples, pol, 
				LSPI.AlgoEstimation.LSTDQ, 0.9, 20, 0.00001);
		try {
			saveQValues(resPolFast, "lspi_chain_fast.out");
			saveQValues(resPol, "lspi_chain.out");
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void saveQValues(ArrayList<PolicyWeighted> listePol, String filename) throws IOException {
		// Ouvre fichier
		FileWriter file = new FileWriter( filename );
		BufferedWriter bw = new BufferedWriter( file );
		
		bw.write("# state");
		for (int i = 0; i < listePol.size(); i++) {
			for (int a = 0; a < _pb._nbAction; a++) {
				bw.write("\tQ_"+i+"(.,"+a+")");
			}
		}
		bw.newLine();
		
		for (int s = 0; s < _pb._nbState; s++) {
			bw.write(""+s);
			for (PolicyWeighted pol : listePol) {
				for (int a = 0; a < _pb._nbAction; a++) {
					bw.write("\t"+pol.Qvalue(s, a));
				}
			}
			bw.newLine();
		}
		
		bw.close();
		file.close();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestLSPI app = new TestLSPI();
		app.testLSPI();

	}

}
