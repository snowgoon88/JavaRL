/**
 * 
 */
package core;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * @author alain.dutech@loria.fr
 *
 */
public class PolicyBasic implements PolicyI {
	
	boolean _fgRandom; // Politique Aléatoire (true) ou Déterministe (false)
	int [] _pol; // Une action par état
	double [][] _polRnd; // Distribution proba sur les actions par état
	
	/** Pour que les nombres soient joliment formatés */
	static DecimalFormat df4_3 = new DecimalFormat( "0.000" );
	/** Randomiser */
    static Random rnd = new Random();
	
    /* (non-Javadoc)
	 * @see core.PolicyI#getNextAction(int)
	 */
    @Override
	public int getNextAction( int state ) {
    	if (_fgRandom) {
			double proba = rnd.nextDouble();
			double sumProba = 0;
			for (int a = 0; a < _polRnd[state].length; a++) {
				sumProba += _polRnd[state][a];
				if (proba <= sumProba) {
					return a;
				}
			}
			// Renvoie la dernière action possible
			return _polRnd[state].length-1;
		}
    	else {
			return _pol[state];
		}
    }
    
    /**
     * Défini une politique déterministe.
     * @param pol Une action par état.
     */
	public void setPolicy( int [] pol) {
		_fgRandom = false;
		_pol = pol;
	}
	/**
	 * Défini une politique aléatoire.
	 * @param pol Une distribution de probabilité par état.
	 */
	public void setRandomPolicy( double [][] pol) {
		_fgRandom = true;
		_polRnd = pol;
	}
	/**
	 * Crée une politique aléatoire uniforme pour nbState et nbAction.
	 */
	public void createUniformRandomPolicy( int nbState, int nbAction ) {
		_polRnd = new double [nbState][nbAction];
		_fgRandom = true;
		
		for (int s = 0; s < nbState; s++) {
			for (int a = 0; a < nbAction; a++) {
				_polRnd[s][a] = 1.0 / nbAction;
			}
		}
	}
	
	@Override
	public String toString() {
		String str = "";
		
		if (_fgRandom) {
			for (int s = 0; s < _polRnd.length; s++) {
				str += s+" => {";
				for (int a = 0; a < _polRnd[s].length; a++) {
					str += a+":"+df4_3.format(_polRnd[s][a])+", ";
				}
				str += "}\n";
			}
		}
		return str;
	}

}
