package fr.labri.reparenting.api.launchprocess.exception;

public class NotLaunchedException extends Exception {

	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return "Process not launched";
	}
}
