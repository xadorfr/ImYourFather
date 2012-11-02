package fr.labri.reparenting.api.core.x11.windows;

import fr.labri.reparenting.api.core.winapi.WinapiWrapper;
import fr.labri.reparenting.api.core.x11.X11Window;
import gnu.x11.Window.Property;

/**
 * This class handle X11 Window created on a Windows plateform by a X.org server
 * running with a window manager which links X11 and Windows in the manner of
 * the -internalwm mode of Xming.
 */
public class WinX11Window extends X11Window {

	/**
	 * winapi handle to the window
	 */
	private static String X11_HWND_PROP = "_WINDOWSWM_NATIVE_HWND";
	
	public WinX11Window(gnu.x11.Window win) {
		super(win);
	}

	/**
	 * reparent the Windows window linked
	 * with this window
	 */
	@Override
	protected void setX11Parent(long handle) {
		/* we reparent the windows window attached to this X11 window*/
		Property p = window.get_property(false,
				gnu.x11.Atom.intern(super.window.display, X11_HWND_PROP),
				gnu.x11.Atom.INTEGER, 0, 1);
		int winParentHandle = p.value();
		WinapiWrapper.reparent(winParentHandle, handle);
	}
}
