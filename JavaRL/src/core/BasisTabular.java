/**
 * 
 */
package core;

/**
 * Fonction de base "exactes", sans approximation.
 * Il y a nbState * nbAction fonction, qui permettent de représenter
 * la fonction Q de manière tabulaire.
 * 
 * @author alain.dutech@loria.fr
 */
public class BasisTabular {
	
	int _nbBase;
	int _nbAction;
	
	/**
	 * Crée les fonction de bases "tabulaires".
	 * @param nbState nombre d'états
	 * @param nbAction nombre d'actions
	 */
	public BasisTabular( int nbState, int nbAction) {
		_nbAction = nbAction;
		_nbBase = nbState * _nbAction;
	}

	/**
	 * Valeur de la fonction de base no 'index' pour 'state' et 'action'.
	 * @return 1.0 ou 0.0.
	 */
	public double getPhi( int index, int state, int action ) {
		if (index == (state*_nbAction+action)) {
			return 1.0;
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
