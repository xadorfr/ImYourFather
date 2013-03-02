package fr.labri.reparenting.api.core;

import static org.junit.Assert.fail;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.labri.reparenting.api.launchprocess.apijava.LauncherJavaAPI;
import fr.labri.reparenting.api.core.ParentWindowManager;
import fr.labri.reparenting.api.core.Window;
import fr.labri.reparenting.api.core.exception.LibraryLoadingException;
import fr.labri.reparenting.api.core.windows.WinDaemonHook;
import fr.labri.reparenting.api.core.windows.WinJniHook;
import fr.labri.reparenting.api.core.windows.WinReparentManager;
import fr.labri.reparenting.api.core.windows.WinRule;
import fr.labri.reparenting.api.core.windows.WinWindowClassIdentifier;
import fr.labri.reparenting.api.core.windows.WinWindowTitleIdentifier;

public class TUWindows {
	private static boolean stopHook = false;
	private static boolean stopReparent = false;
	private static ParentWindowManager wManager;
	private static Display disp;
	private static CreateNewSwtWin swtRunnable;

	@BeforeClass
	public static void pre() {
		wManager = new ParentWindowManager() {
			@Override
			public long getNewParentHandle() {
				swtRunnable = new CreateNewSwtWin();
				Display.getDefault().syncExec(swtRunnable);
				return (long) swtRunnable.getComposite().handle;
			}

			@Override
			public void postReparent(Window win) {
				Display.getDefault().syncExec(swtRunnable);
			 // stopHook = true;
			}
		};
	}

	static class CreateNewSwtWin implements Runnable {
		private Composite nativeComp;
		private Shell shell;

		@Override
		public void run() {
			if (nativeComp == null) {
				// 1st call
				try {
					shell = new Shell(disp);
					nativeComp = new Composite(shell, SWT.EMBEDDED);
					shell.setLayout(new FillLayout());
				} catch (Exception e) {
					return;
				}
			} else {
				// 2nd call
				shell.setSize(800, 600);
				shell.open();
			}

		}

		public Composite getComposite() {
			return nativeComp;
		}

		public Shell getShell() {
			return this.shell;
		}
	}

	@Test
	public void windowsDaemonHook() {
		disp = new Display();

		/* launch daemon */
		try {
			new LauncherJavaAPI("win32/HookInstaller32.exe").launch("32000");
			//new LauncherJavaAPI("win64/HookInstaller64.exe").launch("64000");

			/* identification param */
			WinReparentManager rm = new WinReparentManager();
			WinRule winRule = new WinRule(wManager, "testRule");
			winRule.addIdentifiers(new WinWindowTitleIdentifier(".*Bloc-notes"));
			winRule.addIdentifiers(new WinWindowClassIdentifier("Notepad"));
			winRule.setComplete();
			winRule.activate();
			
			rm.addRule(winRule);
			WinDaemonHook hk = new WinDaemonHook(32000);
			hk.addReparentManager(rm);

			hk.install();

			while (!stopHook) {
				if (!disp.readAndDispatch()) {
					disp.sleep();
				}
			}

			disp.dispose();
			hk.uninstall();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void windowsJniHook() throws InterruptedException,
			LibraryLoadingException {
		/* creation fenetre parente test */
		disp = new Display();
		WinJniHook hk = new WinJniHook();

		/* definition reparentage */
		WinRule rm = new WinRule(wManager, "test");
		rm.addIdentifiers(new WinWindowTitleIdentifier("Figure [1-9]"));

		hk.install();

		while (!stopReparent) {
			if (!disp.readAndDispatch()) {
				disp.sleep();
			}
		}

		disp.dispose();
		hk.uninstall();
	}
}
