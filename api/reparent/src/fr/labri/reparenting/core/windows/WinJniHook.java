package fr.labri.reparenting.core.windows;

import fr.labri.reparenting.api.shared.StoppableThread;
import fr.labri.reparenting.api.core.exception.LibraryLoadingException;

/**
 * 
 * Specific WinHook in order to hook 32 bits apps
 * It loads a 32 bits JNI dll
 * @see WinHook64, WinHookFactory
 */
public class WinJniHook extends WinHook {
	
	private StoppableThread hookThread;
	public static String DLL32NAME = "HookReparent";

	/* JNI functions */
	private native void nativeInstall();
	private native void nativeUninstall();
	
	public WinJniHook() throws LibraryLoadingException {
		super();
		loadHookDLL();

		hookThread = null;
		hookThread = new StoppableThread(new Runnable() {
			@Override
			public void run() {
				nativeInstall();
			}
		});
	}

	protected void loadHookDLL() throws LibraryLoadingException {
		try {
			System.loadLibrary(DLL32NAME);
		} catch (UnsatisfiedLinkError e) {
			throw new LibraryLoadingException(DLL32NAME, LibraryLoadingException.Type.NOTFOUND);
		}
	}

	/**
	 * Specifies the installation of the Hook for Windows
	 */
	@Override
	public void install() {
		hookThread.start();
	}

	/**
	 * Specifies the uninstallation of the Hook for Windows
	 */
	@Override
	public void uninstall() {
		if (hookThread == null)
			return;

		nativeUninstall();
		hookThread.sendStopSignal();
	}

	/* called from native context (lib/native/HookReparent*.dll) */
	private void nativeCallback(long hwnd) {
		super.notifyCreation(new WinWindow(hwnd));
	}

	private void nativeDestroyCallback(long hwnd) {
		super.notifyDestruction(hwnd);
	}
	
	private void checkStopSignal() {
		try {
			hookThread.checkMessages();
		} catch (InterruptedException e) {
			return;
		}
	}
}
