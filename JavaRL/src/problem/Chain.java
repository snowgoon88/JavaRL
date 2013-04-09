/**
 * 
 */
package problem;

import java.util.Random;

import core.Transition;

/**
 * Simulation du 'ChainWalk' problem comme décrit dans [Lagoudakis03].
 * 
 * @author alain.dutech@loria.fr
 */
public class Chain {
	
	double [][][] _prTrans;
	double [] _reward;
	public int _nbState;
	public int _nbAction = 2;
	static int left = 0;
	static int right = 1;
	
	Random _rnd = new Random(); 
	
	/**
	 * Crée un problème 'ChainWalk' avec un nombre d'états donnés ('nbState')
	 * @param _nbState
	 */
	public Chain(int nbState) {
		this._nbState = nbState;
		initModel();
		initReward( false );
	}

	/**
	 * Initialise les proba de Transition.
	 */
	void initModel( ) {
		_prTrans = new double [_nbState][_nbAction][_nbState];
		
		double prSuccess = 0.9;
		double prFail = 0.1;
		
		for (int s = 0; s < _nbState; s++) {
			for (int a = 0; a < _nbAction; a++) {
				for (int ns = 0; ns < _nbState; ns++) {
					_prTrans[s][a][ns] = 0;
					if (ns == s-1) {
						_prTrans[s][left][ns] = prSuccess;
						_prTrans[s][right][ns] = prFail;
					}
					else if (ns == s+1) {
						_prTrans[s][left][ns] = prFail;
						_prTrans[s][right][ns] = prSuccess;
					}
					else if (s == 0) {
						_prTrans[s][left][0] = prSuccess;
						_prTrans[s][left][1] = prFail;
						_prTrans[s][right][0] = prFail;
					}
					else if (s == _nbState-1) {
						_prTrans[s][right][s] = prSuccess;
						_prTrans[s][right][s-1] = prFail;
						_prTrans[s][left][s] = prFail;
					}
				}
			}
		}
	}
	public void initReward( boolean fgExtReward ) {
		_reward = new double [_nbState];
		
		for (int s = 0; s < _nbState; s++) {
			_reward[s] = 0;
			if ((fgExtReward == true) && (s==0 || s==_nbState-1)) {
				_reward[s] = 1;
			}
			else if ((fgExtReward == false) && (s>0) && (s < _nbState-1) ) {
				_reward[s] = 1;
			}
		}
	}

	/**
	 * Simule une Transition.
	 * @param s State
	 * @param a Action
	 * @return a new Transition (with _nextState == -1 if trouble)
	 */
	public Transition simulate( int s, int a ) {
		double proba = _rnd.nextDouble();
		Transition tr = new Transition(s, a, -1, 0, false);
		
		double sumPr = 0;
		for (int ns = 0; ns < _nbState; ns++) {
			sumPr += _prTrans[s][a][ns];
			if (proba <= sumPr) {
				tr._nextState = ns;
				tr._reward = _reward[ns];
				break;
			}
		}
		if (tr._nextState < 0) {
			System.err.println("Chain::simulate pb avec proba="+proba+", t="+tr.toString());
		}
		
		return tr;
	}
	/**
	 * Renvoie un état initial possible.
	 * @return Choix aléatoire entre ° et _nbState.
	 */
	public int getStartState() {
		return _rnd.nextInt(_nbState);
	}
	
	/**
	 * Renvoie un String décrivant les probabilités de Transition. 
	 * @return String str
	 */
	public String dumpModel() {
		String str = "";
		for (int s = 0; s < _nbState; s++) {
			str += "Etat : "+s+" rew="+_reward[s]+"\n";
			for (int a = 0; a < _nbAction; a++) {
				str += "  "+a+" => {";
				for (int ns = 0; ns < _nbState; ns++) {
					str += ns+":"+_prTrans[s][a][ns]+", ";
				}
				str += "}\n";
			}
		}
		return str;
	}

}
