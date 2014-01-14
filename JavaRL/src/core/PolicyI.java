package core;

public interface PolicyI {

	/**
	 * Renvoie une action donnée par la politique dans l'état 'state'.
	 */
	public abstract int getNextAction(int state);

}