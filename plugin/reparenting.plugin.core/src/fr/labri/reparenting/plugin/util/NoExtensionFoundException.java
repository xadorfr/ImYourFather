package fr.labri.reparenting.plugin.util;

public class NoExtensionFoundException extends Exception {
	
	private static final long serialVersionUID = 1L;
	private String extensionId;
	
	public NoExtensionFoundException(String id) {
		this.extensionId = id;
	}
	
	@Override
	public String getMessage() {
		return this.extensionId + " extension not found";
	}
}
