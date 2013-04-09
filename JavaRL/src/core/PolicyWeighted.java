/**
 * 
 */
package core;

import java.text.DecimalFormat;

import problem.Chain;

/**
 * Une "politique" s'exprime sous la forme de 
 * pol(s) = argmax_A {w_i * Phi_i(s,a)}
 * ou Phi est une fonction de Base.
 * 
 * @author alain.dutech@loria.fr
 */
public class PolicyWeighted implements PolicyI{
	
	public double [] _w;
	public BasisPolyState _base;
	public Chain _pb;
	
	/** Pour que les nombres soient joliment formatés */
	static DecimalFormat df4_3 = new DecimalFormat( "0.000" );
	
    /**
     * Création à partir d'un ensemble de fonctions de base.
     * Les poids '_w' sont initialisés à 0.
	 * @param _base
	 */
	public PolicyWeighted(BasisPolyState _base, Chain pb) {
		this._base = _base;
		_pb = pb;
		_w = new double [_base.getNbBase()];
		for (int i = 0; i < _w.length; i++) {
			_w[i] = 0;
		}
	}
	public double Qvalue( int state, int action ) {
		double qval = 0;
		for (int i = 0; i < _w.length; i++) {
			qval += _w[i] * _base.getPhi(i, state, action);
		}
		return qval;
	}
	
	@Override
    public int getNextAction( int state ) {
    	int bestA = 0;
    	double maxQVal = -Double.MAX_VALUE;
    	for (int a = 0; a < _pb._nbAction; a++) {
    		double qval = Qvalue(state, a);
    		if (qval > maxQVal) {
    			bestA = a;
    			maxQVal = qval;
    		}
    	}
		return bestA;
    }
    
	@Override
	public String toString() {
		String str = "";
		
		for (int s = 0; s < _pb._nbState; s++) {
			str += s+" => {";
			for (int a = 0; a < _pb._nbAction; a++) {
				str += a+":"+df4_3.format(Qvalue(s, a))+", ";
			}
			str += "} best="+getNextAction(s)+"\n";
		}
	return str;
	}

}
