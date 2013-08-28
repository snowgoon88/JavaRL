/**
 * 
 */
package simulator;


import Jama.Matrix;
import model.Command;
import model.CommandSequence;
import model.Consignes;

/**
 * A Consignes with 6 CommandSequences made of 2 Command,
 * which is valid for t in  [0,timeLength[ :
 * <li> Command at t=0, val to be defined (with setConsigne)</li>
 * <li> Command at t=timeLength/2, with val=0 </li>
 * 
 * @author alain.dutech@loria.fr
 */
public class AgentConsigne extends Consignes {

	
	/** Nb of Consigne */
	static int _nbConsignes = 6;
	/** Starting time */
	double _startingTime;
	/** Lenth of this Command */
	double _timeLength;
	
	/**
	 * Creation with a duration.
	 * @param startingTime of this AgentConsigne
	 * @param timeLength (in s).
	 */
	public AgentConsigne(double startingTime, double timeLength) {
		super(_nbConsignes);
		_startingTime = startingTime;
		_timeLength = timeLength;
		
		// Initialize Commandes
		for (int i=0; i < size(); i++) {
			CommandSequence cs = get(i);
			cs.add(new Command(0,0));
			cs.add(new Command(_timeLength/2.0,0));
		}
	}
	
	/**
	 * Change the val of Consigne 'numConsigne' at time 0.
	 * @param numConsigne
	 * @param val Nouvelle valeur.
	 */
	public void setConsigne( int numConsigne, double val) {
		CommandSequence cs = get(numConsigne);
		cs.changeCommand(cs.get(0), 0, val);
	}
	
	/**
	 * Update Consigne Matrix 'u' with proper command.
	 * @param u Consigne Matrix (1 x nb_muscles)
	 * @param time (in s) since AgentConsigne started.
	 */
	public void getConsigne( Matrix u, double time ) {
		for (int i = 0; i < size(); i++) {
			CommandSequence cs = get(i);
			// la valeur de la consigne est copiée dans le vecteur u
			u.set(0,i, cs.getValAtTimeFocussed(time - _startingTime));
		}
	}
	/**
	 * Test if AgentConsigne still gives valid Command.
	 * 
	 * @param time
	 * @return ((time - _startingTime) < _timeLength)
	 */
	public boolean isStillValid( double time ) {
		return ((time - _startingTime) < _timeLength);
	}
}
