package fr.labri.reparenting.plugin.core.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.part.WorkbenchPart;

import fr.labri.reparenting.api.core.Window;
import fr.labri.reparenting.api.shared.SystemValidator;

/**
 * The ViewPart which is used to reparent every external process window
 */
public class ParentView extends ViewPart {
	public static final String ID = "cea.plugin.reparent.ParentView";
	private static final String baseSecondaryID = "parentView";
	private long handle;
	private Composite composite;
	private Window child;

	private static int count = 0;

	public ParentView() {
		super();
		this.handle = 0;
		this.child = null;
	}

	public static String generateSecondaryID() {
		String id = baseSecondaryID + count;
		ParentView.count++;
		return id;
	}

	public static String getSecondaryIdBase() {
		return baseSecondaryID;
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		this.composite = new Composite(parent, SWT.EMBEDDED);

		/*
		 * Below, the use of reflection aims to fill a SWT implementation
		 * difference between Windows and Linux We get the composite's handle
		 * throught the field "handle" on Windows, while we get it with the
		 * field embeddedHandle on Linux (maybe Unix's OS in general)
		 */
		long handle;
		try {
			if (SystemValidator.isJVM64()) {
				handle = composite.getClass()
						.getDeclaredField("embeddedHandle").getLong(composite);
			} else {
				handle = composite.getClass()
						.getDeclaredField("embeddedHandle").getInt(composite);
			}
		} catch (NoSuchFieldException | SecurityException
				| IllegalArgumentException | IllegalAccessException e) {
			handle = composite.handle;
			// macos : handle = composite.view.id;
		}

		this.handle = new Long(handle);
	}

	public void setChild(Window child) {
		this.child = child;
	}

	public void setPartTitle(String title) {
		/* UI work */
		final ParentView instance = this;
		final String fTitle = title;
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				instance._setTitle(fTitle);
			}
		});
	}

	/*
	 * has to be executed by the RCP UI thread
	 */
	private void _setTitle(String title) {
		this.setPartName(title);
		this.firePropertyChange(WorkbenchPart.PROP_TITLE);
	}

	@Override
	public void setFocus() {
		this.composite.setFocus();
	}

	public Long getHandle() {
		return this.handle;
	}

	/*
	 * Couple the ParentView destruction with the reparented window destruction
	 */
	@Override
	public void dispose() {
		super.dispose();
		if (child != null) {
			child.dispose();
		}
	}
}
