/**
 * 
 */
package simulator;

import java.text.DecimalFormat;

import org.kohsuke.args4j.Option;

/**
 * Implements the sensori-motor loop between an Agent and a World.
 * 
 * @author alain.dutech@loria.fr
 */
public class Simulator {
	
	DecimalFormat df3_5 = new DecimalFormat( "000.00000" );
	
	/** Max simulation time allowed */
	@Option(name="-mt",aliases={"--maxTime"},usage="Max simulation time allowed")
	public double _maxTime = 5.0;
	/** Update interval for Simulation */
	@Option(name="-dt",aliases={"--deltaTime"},usage="Update interval for Simulation")
	public double _deltaTime = 0.010;
	/** Nb time the XP is repeated */
	@Option(name="-n",aliases={"--nbXP"},usage="Nb of time XP is repeated")
	int _nbXP = 1;
	
	/** Experience to run */
	XPDefault _xp;
	
	/** Time of the Simulation */
	public double _timeSimu;
	
	/**
	 * Create.
	 * @param xp
	 */
	public Simulator(XPDefault xp) {
		// Initialisation
		_xp = xp;
	}
	
	/**
	 * Run in Batch mode. Parameters set with ParameterFactory
	 */
	public void runBatch() {
		_xp.init();
		
		for( int i=0; i< _nbXP; i++) {
			// runStart
			_timeSimu = 0.0;
			_xp.reset(i);
			// while not runEnd
			while (_timeSimu < _maxTime) {
				_xp.updateAgents(_timeSimu);
				_xp.updateWorld(_timeSimu, _deltaTime);
				
				_timeSimu += _deltaTime;
			}
			// runWrap
			_xp.wrapUp(_timeSimu);
		}
		
		_xp.end();
	}

//	/**
//	 * Run in Batch mode with Logging.
//	 * @param param
//	 */
//	public void runBatch( Parameters param ) {
//		// Observer
//		SCompleteArm armV = new SCompleteArm(_xp._world);
//		_xp._world.addObserver(armV);
//		
//		Log<String> logFile = null;
//		
//		if (param.logScreen ) {
//			System.out.println("#"+String.format("%8s", "time")+"\t"+armV.explainStr);
//		}
//		if (param.logFile != "") {
//			logFile = new Log<String>(param.logFile);
//			logFile.writeLine("#"+String.format("%8s", "time")+"\t"+armV.explainStr);
//		}
//		
//		// We do this 10 times
//		for( int i=0; i<10;i++) {
//			reset();
//			if (param.logScreen) {
//				System.out.println(df3_5.format(_timeSimu)+"\t"+armV.viewStr);
//			}
//			if (logFile != null) {
//				logFile.write(df3_5.format(_timeSimu)+"\t"+armV.viewStr);
//			}
//			while (_timeSimu < param.maxTime ) {
//				step(param.deltaTime);
//				if (param.logScreen) {
//					System.out.println(df3_5.format(_timeSimu)+"\t"+armV.viewStr);
//				}
//				if (logFile != null) {
//					logFile.write(df3_5.format(_timeSimu)+"\t"+armV.viewStr);
//				}
//			}
//			wrapUp();
//		}
//		
//		end();
//		
//		if (logFile != null) {
//			logFile.close();
//		}
//	}
	
//	// populate the State x Action space
//	int nbTraj = 10;
//	for( int i=0; i<nbTraj; i++) {
//		// runStart
//		reset();
//		// while not runEnd
//		while (_timeSimu < param.maxTime) {
//			step(param.deltaTime);
//		}
//		// runWrap
//		wrapUp();
//	}
//
//	// test that nearest are the nearest
//	return _syst.testMemory();
}
