/**
 * 
 */
package algorithm;

import java.util.ArrayList;

import utils.JamaU;


import core.PolicyWeighted;
import core.Transition;

import Jama.Matrix;

/**
 * @author alain.dutech@loria.fr
 *
 */
public class LSPI {
	
	Matrix _PhiHat;
	Matrix _RHat;
	Matrix _PiPhiHat;
	
	public Matrix _A;
	public Matrix _b;
	public Matrix _w;
	
	public static enum AlgoEstimation {
		LSTDQ, LSTDQFAST;
	}
	
	/**
	 * Applique l'algorithme LSPI à partir d'une politique initiale et d'un ensemble
	 * d'échantillons. L'algorithme pour calculer la fonction de valeur des politiques
	 * doit aussi être choisie.
	 * @param samples Une liste d'échantillons (s,a,s',r)
	 * @param polInitial La politique initiale
	 * @param algo Choix entre LSTDQ, LSTDQFAST
	 * @param gamma Gamma utilisé
	 * @param maxIteration Combien d'itération de LSPI au maximum
	 * @param epsilon Critère d'arrêt : distance entre deux politiques successives.
	 * @return
	 */
	public ArrayList<PolicyWeighted> runLSPI(ArrayList<Transition> samples, PolicyWeighted polInitial,
			AlgoEstimation algo, double gamma,
			int maxIteration, double epsilon) {
		double distance = Double.MAX_VALUE;
		int iteration = 0;
		boolean firstTime = true;
		ArrayList<PolicyWeighted> listePol = new ArrayList<PolicyWeighted>();
		
		PolicyWeighted curPol = polInitial;
		System.out.println("curPol=\n"+curPol.toString());
		listePol.add(curPol);
		
		while ((iteration < maxIteration) && (distance > epsilon)) {
			// Maj iteration
			iteration += 1;
			if (iteration > 1) {
				firstTime = false;
			}
			System.out.println("**** LSPI : it="+iteration);
			
			// Ou pourrait générer de nouveaux samples ici,
			// mais dans ce cas ne pas oublier de remettre 'firstTime=true'
			
			// Evaluation de la politique courante et amélioration
			switch (algo) {
			case LSTDQ:
				stepLSTDQ(samples, curPol, gamma);
				break;
			case LSTDQFAST:
				stepLSTDQfast(samples, curPol, gamma, firstTime);
				break;
			default:
				System.err.println("LSPI : algo estimation not provided, using LSTQFAST");
				stepLSTDQfast(samples, curPol, gamma, firstTime);
				break;
			}
			PolicyWeighted newPol = new PolicyWeighted(curPol._base, curPol._pb);
			newPol._w = _w.getColumnPackedCopy();
			
			// Distance
			distance = 0;
			for (int i = 0; i < curPol._w.length; i++) {
				distance += (newPol._w[i] - curPol._w[i])*(newPol._w[i] - curPol._w[i]);
			}
			
			System.out.println("newPol=\n"+newPol.toString());
			System.out.println("LSPI : normL2="+distance);
			
			listePol.add(newPol);
			curPol = newPol;
		}
		
		// Info finales
		if (distance < epsilon) {
			System.out.println("LSPI : convergence ("+distance+") après it="+iteration);
		}
		else {
			System.out.println("LSPI : NON convergence ("+distance+") après it="+iteration);
		}
		
		return listePol;
	}
	
	/**
	 * Calcule de LSTDQ, batch, à partir d'un ensemble de samples.
	 * @param samples Une liste d'échantillons (s,a,s',r)
	 * @param pol La politique à évaluer
	 * @param gamma Gamma utilisé
	 * @param fgFirst Est-ce la première itération avec ces échantillons ET cette politique ?
	 * @return M-à-j de _A,_b et _w.
	 */
	public void stepLSTDQfast( ArrayList<Transition> samples, PolicyWeighted pol,
			double gamma, boolean fgFirst) {
		
		long startTime = System.currentTimeMillis();
		
		// Si c'est le premier pas, peut calculer PhiHat et RHat
		// qui ne dépendent pas de la politique
		if (fgFirst) {
			_PhiHat = new Matrix(samples.size(), pol._base.getNbBase());
			_RHat = new Matrix(samples.size(), 1);
			for (int i = 0; i < samples.size(); i++) {
				Transition t = samples.get(i);
				_RHat.set(i, 0, t._reward);
				for (int j = 0; j < pol._base.getNbBase(); j++) {
					_PhiHat.set(i, j, pol._base.getPhi(j, t._state, t._action));
				}
			}
		}
		
		// Calcul de PiPhiHat qui dépend de la politique
		_PiPhiHat = new Matrix(samples.size(), pol._base.getNbBase(), 0);
		for (int i = 0; i < samples.size(); i++) {
			Transition t = samples.get(i);
			for (int j = 0; j < pol._base.getNbBase(); j++) {
				_PiPhiHat.set(i, j, pol._base.getPhi(j, t._nextState, 
							pol.getNextAction(t._nextState)));
			}
		}
		
		// Calcul des matrices A et b
		_A = _PiPhiHat.times(gamma);
		_A = _PhiHat.minus(_A);
		_A = _PhiHat.transpose().times(_A);
		_b = _PhiHat.transpose().times(_RHat);
		
		System.out.println("A=\n"+JamaU.matToString(_A));
		System.out.println("b=\n"+JamaU.matToString(_b));
		
		long abTime = System.currentTimeMillis();
		System.out.println("LSTDQfast : Tps creation="+(abTime-startTime));
		
		int rankA = _A.rank();
		long rTime = System.currentTimeMillis();
		System.out.println("LSTDQfast : Tps rank="+(rTime-abTime)+" rk="+rankA);
		
		if (rankA == pol._base.getNbBase()) {
			// Matrice inversible
			_w = _A.solve(_b);
			long solveTime = System.currentTimeMillis();
			System.out.println("LSTDQfast : Tps solve="+(solveTime-rTime));
		}
		else {	
			_w = JamaU.pinv2(_A).times(_b);
			long solveTime = System.currentTimeMillis();
			System.out.println("LSTDQfast : Tps pinv="+(solveTime-rTime));
		}
	}
	/**
	 * Calcule de LSTDQ, "on-line", à partir d'un ensemble de samples.
	 * @param samples Une liste d'échantillons (s,a,s',r)
	 * @param pol La politique à évaluer
	 * @param gamma Gamma utilisé
	 * @return M-à-j de _A,_b et _w.
	 */
	void stepLSTDQ(ArrayList<Transition> samples, PolicyWeighted pol,
			double gamma) {
		
		long startTime = System.currentTimeMillis();
		
		// Initialisation des matrices
		_A = new Matrix(pol._base.getNbBase(), pol._base.getNbBase(), 0);
		_b = new Matrix(pol._base.getNbBase(), 1, 0);
		
		Matrix phi = new Matrix(pol._base.getNbBase(), 1, 0);
		Matrix nextPhi = new Matrix(pol._base.getNbBase(), 1, 0);

		// Pour chaque échantillon
		for (Transition t : samples) {

			// Calcul de Phi(s_t,a_t)
			for (int k = 0; k < pol._base.getNbBase(); k++) {
				phi.set(k, 0, pol._base.getPhi(k, t._state, t._action));
			}
			// nextPhi seulement si pas absorbant.
			if (t._absorb == false) {
				int nextAct = pol.getNextAction(t._nextState);
				for (int k = 0; k < pol._base.getNbBase(); k++) {
					nextPhi.set(k, 0, pol._base.getPhi(k, t._nextState, nextAct));
				}
			}
			else {
				for (int k = 0; k < pol._base.getNbBase(); k++) {
					nextPhi.set(k, 0, 0);
				}
			}

//		    A = A + phi * (phi - new_policy.discount * nextphi)';
//		    b = b + phi * samples(i).reward;
			// Màj A et b
			_A = _A.plus(phi.times( phi.minus(nextPhi.times(gamma)).transpose() ));
			_b = _b.plus(phi.times(t._reward));
		}
		System.out.println("A=\n"+JamaU.matToString(_A));
		System.out.println("b=\n"+JamaU.matToString(_b));
		
		long abTime = System.currentTimeMillis();
		System.out.println("LSTDQfast : Tps creation="+(abTime-startTime));
		
		int rankA = _A.rank();
		long rTime = System.currentTimeMillis();
		System.out.println("LSTDQfast : Tps rank="+(rTime-abTime)+" rk="+rankA);
		
		if (rankA == pol._base.getNbBase()) {
			// Matrice inversible
			_w = _A.solve(_b);
			long solveTime = System.currentTimeMillis();
			System.out.println("LSTDQfast : Tps solve="+(solveTime-rTime));
		}
		else {	
			_w = JamaU.pinv2(_A).times(_b);
			long solveTime = System.currentTimeMillis();
			System.out.println("LSTDQfast : Tps pinv="+(solveTime-rTime));
		}
	}
}
