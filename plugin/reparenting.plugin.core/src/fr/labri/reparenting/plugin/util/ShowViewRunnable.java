package fr.labri.reparenting.plugin.util;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * Runnable in charge of loading a new ParentView
 * run() must be exectuted by the UI thread
 * if not workbench.getActiveWorkbenchWindow() returns null
 */
public class ShowViewRunnable implements Runnable {
	private IViewPart view;
	private String viewId;
	private String secondaryId;

	public ShowViewRunnable(String viewId, String secondaryId) {
		this.view = null;
		this.viewId = viewId;
		this.secondaryId = secondaryId;
	}
	
	@Override
	public void run() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow workbenchWin = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage page = workbenchWin.getActivePage();
		
		try {
			this.view = page.showView(this.viewId, this.secondaryId, 
					IWorkbenchPage.VIEW_CREATE);
		} catch (PartInitException e) {
			//
		}
	}
	
	/**
	 * @return created IViewPart
	 */
	public IViewPart getView() {
		return this.view;
	}
}
