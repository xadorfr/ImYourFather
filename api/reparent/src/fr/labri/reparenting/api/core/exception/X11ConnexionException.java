package fr.labri.reparenting.api.core.exception;

public class X11ConnexionException extends Exception {
	private static final long serialVersionUID = 0L;


	private String id;
	
	public X11ConnexionException(int display, int screen) {
		this.id = ":" + display + "." + screen;
	}
	
	@Override
	public String getMessage() {
		return "Can't open display " + this.id;
	}
}
