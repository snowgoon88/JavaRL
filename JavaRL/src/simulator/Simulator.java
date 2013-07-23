/**
 * 
 */
package simulator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import viewer.SCompleteArm;

/**
 * Implements the sensori-motor loop between an Agent and a World.
 * 
 * @author alain.dutech@loria.fr
 */
public class Simulator {
	
	DecimalFormat df3_5 = new DecimalFormat( "000.00000" );
	
	DynSystem _syst;
	public double _timeSimu;
	
	public Simulator() {
		// Initialisation
		_syst = new DynSystem();
	}
	
	/**
	 * Set World in a starting state : position and consigne.
	 */
	public void reset() {
		_timeSimu = 0.0;
		_syst.reset();
	}
	
	public void step( double deltaT ) {
		_syst.updateAgents(_timeSimu);
		_syst.updateWorld(_timeSimu, deltaT);
		
		_timeSimu += deltaT;
	}
	
	/**
	 * Run in Batch mode with a given set of Parameters
	 * @param param
	 */
	public void runBatch( Parameters param ) {
		// Observer
		SCompleteArm armV = new SCompleteArm();
		_syst._world.addObserver(armV);
		
		FileWriter logFile = null;
		BufferedWriter logWriter = null;
		
		if (param.logScreen ) {
			System.out.println("# time\t"+armV.explainStr);
		}
		if (param.logFile != "") {
			try {
				logFile = new FileWriter( param.logFile);
				logWriter = new BufferedWriter(logFile);
				logWriter.write("# time\t"+armV.explainStr+"\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logFile = null;
				logWriter = null;
			}
		}
		
		reset();
		if (param.logScreen) {
			System.out.println(df3_5.format(_timeSimu)+"\t"+armV.viewStr);
		}
		if (logFile != null) {
			try {
				logWriter.write(df3_5.format(_timeSimu)+"\t"+armV.viewStr+"\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		while (_timeSimu < param.maxTime ) {
			step(param.deltaTime);
			if (param.logScreen) {
				System.out.println(df3_5.format(_timeSimu)+"\t"+armV.viewStr);
			}
			if (logFile != null) {
				try {
					logWriter.write(df3_5.format(_timeSimu)+"\t"+armV.viewStr+"\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (logWriter != null) {
			try {
				logWriter.close();
				logFile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}
