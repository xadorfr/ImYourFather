package fr.labri.reparenting.api.core;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import fr.labri.reparenting.api.core.winapi.WinapiWrapper;
import fr.labri.reparenting.api.shared.SystemValidator;

/**
 * 
 * Test des appels syst�mes findWindow et SetParent par l'interm�diaire de
 * com.sun.jna.platform.win32.User32
 * 
 */
public class TestJnaWinapi {
	public static void main(String[] args) {
		tag();
	}

	public static void reparent() {
		System.out.println("x64? : " + SystemValidator.isJVM64());

		long f1 = WinapiWrapper.findWindow(null, "Ordinateur");
		long f2 = WinapiWrapper.findWindow(null, "Bureau");

		if (f1 == 0 || f2 == 0) {
			System.exit(0);
		}

		WinapiWrapper.reparent(f1, f2);
	}

	public static void tag() {
		Display disp = new Display();
		Shell shell = new Shell(disp);
		shell.setSize(30, 30);
		shell.setText("Test");
		
		Composite comp = new Composite(shell, SWT.EMBEDDED);
		
		if(! WinapiWrapper.hasProp(comp.handle, "TEST")) {
			System.out.println("TAG TEST : absent - OK");
		} else {
			System.out.println("TAG TEST : d�j� pr�sent ? - FAIL");
		}
		
		System.out.println(WinapiWrapper.GetWindowName(shell.handle));
		WinapiWrapper.SetProp(comp.handle, "TEST", comp.handle);
		
		if(WinapiWrapper.hasProp(comp.handle, "TEST")) {
			System.out.println("TAG TEST : pr�sent - OK");
		} else {
			System.out.println("TAG TEST absent - FAIL");
		}
		
		shell.open();

		while (!shell.isDisposed()) {
			if (!disp.readAndDispatch())
				disp.sleep();
		}
		disp.dispose();
	}

}