/**
 * 
 */
package simulator;

/**
 * Implements the sensori-motor loop between an Agent and a World.
 * 
 * @author alain.dutech@loria.fr
 */
public class Simulator {
	
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
}
