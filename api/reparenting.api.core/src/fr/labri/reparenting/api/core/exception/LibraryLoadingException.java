package fr.labri.reparenting.api.core.exception;

/**
 * 
 * Exception overriding the UnsatisfiedLinkError runtime exception
 * to force user to handle it
 */
public class LibraryLoadingException extends Exception {
	private static final long serialVersionUID = 1L;
	public static enum Type {NOTFOUND, INCOMPATIBLE}; 
	
	private String message;
	
	public LibraryLoadingException(String libName, Type type) {
		switch(type) {
		case NOTFOUND: 
			this.message = "Loading of the " + libName + " library failed (check your path)";
			break;
		
		case INCOMPATIBLE: 
			this.message = libName + " : incombatible bitness type with your JVM version";
			break;
		}
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
