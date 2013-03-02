package fr.labri.reparenting.plugin.core.extension;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import fr.labri.reparenting.api.core.ParentWindowManager;
import fr.labri.reparenting.api.core.Window;
import fr.labri.reparenting.plugin.core.view.ParentView;
import fr.labri.reparenting.plugin.util.ShowViewRunnable;

/**
 * RCP implementation of the reparent API interface ParentWindowManager
 */
public class RCPParentWindowManager implements ParentWindowManager {
	private ParentView parentView;
	private static Map<Window, ParentView> reparentMap = new HashMap<Window, ParentView>();

	@Override
	public long getNewParentHandle() {
		ShowViewRunnable showViewR = new ShowViewRunnable(ParentView.ID,
				ParentView.generateSecondaryID());
		Display.getDefault().syncExec(showViewR);

		parentView = (ParentView) showViewR.getView();
		return parentView.getHandle();
	}

	@Override
	public void postReparent(Window win) {
		String title = win.getTitle();
		if (title != null) {
			parentView.setPartTitle(title);
		}
		parentView.setChild(win);
		reparentMap.put(win, parentView);
	}

	public static void destroyParent(Window child) {
		final ParentView pv = reparentMap.get(child);
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(pv);
			}
		});
	}
}
