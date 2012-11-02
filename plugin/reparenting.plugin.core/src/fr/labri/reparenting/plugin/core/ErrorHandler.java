package fr.labri.reparenting.plugin.core;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

public class ErrorHandler {
	public static void handleException(Throwable e) {
		displayError(e.getMessage());
	}
	private static void displayError(String message) {
		MessageDialog.openInformation(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Error", message);
	}
}
