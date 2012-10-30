package fr.labri.reparenting.core.test;

import java.io.IOException;
import java.net.UnknownHostException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Test;

import fr.labri.reparenting.api.core.exception.X11ConnexionException;
import fr.labri.reparenting.core.reparent.ParentWindowManager;
import fr.labri.reparenting.core.x11.UnixX11SystemListener;
import fr.labri.reparenting.core.x11.X11ReparentManager;
import fr.labri.reparenting.core.x11.X11Rule;
import fr.labri.reparenting.core.x11.X11WindowClassIdentifier;
import fr.labri.reparenting.core.x11.X11WindowTitleIdentifier;
import fr.labri.reparenting.core.x11.windows.WinX11SystemListener;

public class TUX11 {
	//private static gnu.x11.Display display;
	private ParentWindowManager wManager;
	private boolean stopHook = false;
	private static Display swtDisp;

	@Before
	public void pre() {
		swtDisp = new Display();
		wManager = new ParentWindowManager() {
			private fr.labri.reparenting.core.test.TUX11.CreateNewSwtWin swtRunnable;

			@Override
			public long getNewParentHandle() {
				swtRunnable = new CreateNewSwtWin();
				Display.getDefault().syncExec(swtRunnable);
				long handle;
				Composite composite = swtRunnable.getComposite();
			    try {
					handle = composite.getClass().getDeclaredField("embeddedHandle").getInt(composite);
				} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
					handle = composite.handle;
				}
				return handle;
			}

			@Override
			public void postReparent(fr.labri.reparenting.core.reparent.Window window) {
				//
			}
		};
	}
	
	private static class CreateNewSwtWin implements Runnable {	
		private Composite nativeComp;
		private Shell shell;
		
		@Override
		public void run() {
			if(nativeComp == null) {
				// 1st call
				try {
					shell = new Shell(swtDisp);
					nativeComp = new Composite(shell, SWT.EMBEDDED);
					shell.setLayout(new FillLayout());
					shell.setSize(800, 600);
					shell.open();
					} catch (Exception e) {
						return;
					}
			} else {
				// 2nd call
			}

		}
		
		public Composite getComposite() {
			return nativeComp;
		}
	}
	
	@Test
	public void winX11Hook() throws X11ConnexionException {
		WinX11SystemListener hk = new WinX11SystemListener(0, 0);
		X11ReparentManager rm = new X11ReparentManager();
		X11Rule rule = new X11Rule(wManager, "TestX11", 0, 0);
		rule.addIdentifiers(new X11WindowTitleIdentifier("Calculator"));
		rule.addIdentifiers(new X11WindowClassIdentifier("xcalc.*"));
		rule.setComplete();
		rm.addRule(rule);
		hk.addReparentManager(rm);
		
		hk.install();
		
		while (! stopHook) {
			if (! swtDisp.readAndDispatch()) {
				swtDisp.sleep();
			}
		}
		
		swtDisp.dispose();
		hk.uninstall();
	}
	
	@Test
	public void UnixX11Hook() throws X11ConnexionException, UnknownHostException, IOException {
		UnixX11SystemListener hk = new UnixX11SystemListener(0, 0);
		X11ReparentManager rm = new X11ReparentManager();
		X11Rule rule = new X11Rule(wManager, "TestX11", 0, 0);
		rule.addIdentifiers(new X11WindowTitleIdentifier("Calculator"));
		rm.addRule(rule);
		hk.addReparentManager(rm);
		
		hk.install();
		
		while (! stopHook) {
			if (! swtDisp.readAndDispatch()) {
				swtDisp.sleep();
			}
		}
		
		swtDisp.dispose();
		hk.uninstall();
	}
}
