/**
 * 
 */
package core;

import java.util.ArrayList;

import problem.Chain;

/**
 * @author alain.dutech@loria.fr
 *
 */
public class Simulator {

	Chain _pb;

	/**
	 * @param pb Un problème (MDP ou POMDP)
	 */
	public Simulator(Chain pb) {
		this._pb = pb;
	}
	
	/** 
	 * Simule 'nbEpoch' épisode de taille maximale 'maxLength'.
	 * @param startState Etat intial.
	 * @param pol Politique à utiliser.
	 * @param maxLength Taille maximale de l'épisode.
	 * @return ArrayList<Transitions>
	 */
	public ArrayList<Transition> runEpisode( int startState, PolicyI pol,
			int maxLength) {
		ArrayList<Transition> trans = new ArrayList<Transition>();
		
		int state = startState;
		for (int i = 0; i < maxLength; i++) {
			Transition t = _pb.simulate(state, pol.getNextAction(state));
			trans.add(t);
			state = t._nextState;

			// stop si état absorbant.
			if (t._absorb) {
				break;
			}
		}
		return trans;
	}
	/**
	 * Simule plusieurs episodes en commençant par un état choisi aléatoirement.
	 * @param pol Politique d'exploration à utiliser.
	 * @param maxLength Taille maximale de l'épisode.
	 * @param nbEpoch Nombre d'épisodes
	 * @return ArrayList<Transitions>
	 */
	public ArrayList<Transition> collectSamples( PolicyI pol,int maxLength, int nbEpoch) {
		ArrayList<Transition> trans = new ArrayList<Transition>();
		
		for (int epoch = 0; epoch < nbEpoch; epoch++) {
			int startState = _pb.getStartState();
			trans.addAll(runEpisode(startState, pol, maxLength));
		}
		return trans;
	}
	
}
