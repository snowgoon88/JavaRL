/**
 * 
 */
package core;

/**
 * @author alain.dutech@loria.fr
 *
 */
public class Transition {
	public int _state;
	public int _action;
	public int _nextState;
	public double _reward;
	public boolean _absorb; // true if absorbing state
	
	/**
	 * Constructeur
	 */
	public Transition(int _state, int _action, int _nextState, double _reward,
			boolean _absorb) {
		super();
		this._state = _state;
		this._action = _action;
		this._nextState = _nextState;
		this._reward = _reward;
		this._absorb = _absorb;
	}

	@Override
	public String toString() {
		String str = "";
		
		str += _state+":"+_action;
		str += " -> ";
		str += _nextState+" ("+_reward+")";
		
		return str;
	}
	
	
}
