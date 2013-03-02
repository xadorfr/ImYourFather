package fr.labri.reparenting.api.core.windows;

import fr.labri.reparenting.api.shared.SystemValidator;
import fr.labri.reparenting.api.core.exception.LibraryLoadingException;

/**
 * Specific WinHook in order to hook 64 bits apps
 * It loads a 64 bits JNI dll
 * @see WinHook, WinHookFactory
 */
public class WinJniHook64 extends WinJniHook {
	public static String DLL64NAME = "HookReparent64";
	
	public WinJniHook64() throws LibraryLoadingException {
		super();
	}

	@Override
	protected void loadHookDLL() throws LibraryLoadingException {
		try {
			System.loadLibrary(DLL64NAME);
		} catch (UnsatisfiedLinkError e) {
			if(SystemValidator.isJVM64()) {
				throw new LibraryLoadingException(DLL64NAME, LibraryLoadingException.Type.NOTFOUND);
			} else {
				new LibraryLoadingException(DLL64NAME, LibraryLoadingException.Type.INCOMPATIBLE);
			}
		}
	}
}
