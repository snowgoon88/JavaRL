/**
 * 
 */
package core;

/**
 * Fonction de base polynomiales. 
 * Pour chaque action, il y a '_degPoly' fonctions : 1, state, state^2, ..., state^_degPoly
 * 
 * @author alain.dutech@loria.fr
 */
public class BasisPolyState {
	
	int _nbBase;
	int _nbAction;
	int _nbState;
	int _degPoly;
	
	/**
	 * Crée les fonction de bases polynomes de l'état.
	 * @param nbState nombre d'états;
	 * @param nbAction nombre d'actions
	 * @param degPoly Degré maximum des polynomes
	 */
	public BasisPolyState( int nbState, int nbAction, int degPoly) {
		_nbState = nbState;
		_nbAction = nbAction;
		_degPoly = degPoly;
		_nbBase = (_degPoly+1) * _nbAction;
	}

	/**
	 * Valeur de la fonction de base no 'index' pour 'state' et 'action'.
	 * la valeur de 's' est uniformisée entre 0 et 10.0
	 * @return 0.0 ou 1.0, state, state^2, ..., state^\degPoly
	 */
	public double getPhi( int index, int state, int action ) {
		int deg = index - action * (_degPoly+1);
		if (deg >= 0 && deg <= _degPoly) {
			return Math.pow((double) state * 10.0 / (double) _nbState, (double) deg);
		}
		else {
			return 0.0;
		}
	}

	/**
	 * Nombre de fonctions de base.
	 */
	public int getNbBase() {
		return _nbBase;
	}
	
	/**
	 * Les valeurs de Phi pour (state,action)
	 */
	public String toString( int state, int action) {
		String str = "Phi("+state+", "+action+")=[";
		for (int i = 0; i < getNbBase(); i++) {
			str += i+":"+getPhi(i,state,action)+", ";
		}
		str += "]";
		return str;
	}
}
