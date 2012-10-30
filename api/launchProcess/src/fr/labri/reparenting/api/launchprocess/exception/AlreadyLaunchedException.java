package fr.labri.reparenting.api.launchprocess.exception;

public class AlreadyLaunchedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return "The process handled by this Launcher has already been launched"; 
	}
}
