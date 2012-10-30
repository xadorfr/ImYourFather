package fr.labri.reparenting.core.x11;

import com.sun.jna.NativeLong;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.platform.unix.X11.XWMHints;

import fr.labri.reparenting.core.reparent.Window;
import fr.labri.reparenting.core.x11.xlib.XlibFactory;
import fr.labri.reparenting.core.x11.xlib.XlibJnaInterface;
import gnu.x11.Window.Property;

public class X11Window extends Window {
	protected gnu.x11.Window window;

	public X11Window(gnu.x11.Window win) {
		super(win.id);
		this.window = win;
	}

	gnu.x11.Display getDisplay() {
		return window.display;
	}

	gnu.x11.Window getEscherWindow() {
		return this.window;
	}

	/**
	 * Common treatment to X11, whatever OS is used The window's state is
	 * checked (WM_STATE property) and the effective reparenting is called
	 */
	@Override
	protected void setParent(long parentHandle) {
		gnu.x11.Atom wm_state = gnu.x11.Atom.intern(window.display, "WM_STATE");
		Property p = window.get_property(false, wm_state, gnu.x11.Atom.INTEGER,
				0, 2);
		while (p.string_value().length() != 0) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				continue;
			}
		}

		this.setX11Parent(parentHandle);
	}

	/**
	 * Reparenting treatment <br>
	 * At the moment the API uses Xlib on Unix systems because no reparenting
	 * tries succeeded with the escher lib
	 * 
	 * @param parentHandle
	 */
	protected void setX11Parent(long handle) {
		XlibJnaInterface xlib = XlibFactory.getXlib();
		X11.Display xlibDisplay = xlib.XOpenDisplay(":"
				+ window.display.display_no + "."
				+ window.display.default_screen_no);
		
		long WindowGroupHint = (1L << 6);
		xlib.XSync(xlibDisplay, false);

		X11.Window win = new X11.Window(window.id);
		X11.Window parent = new X11.Window(handle);
		xlib.XUnmapWindow(xlibDisplay, win);

		XWMHints leaderChange = xlib.XGetWMHints(xlibDisplay, win);
		if (leaderChange != null) {
			long flags = leaderChange.flags.longValue();
			long newFlags = (flags | WindowGroupHint);

			leaderChange.flags = new NativeLong(newFlags);
			try {
				leaderChange.window_group = xlib.XRootWindow(xlibDisplay,
						window.display.default_screen_no);
			} catch (Exception e) {
				/* ERROR */
			}
		}

		xlib.XSync(xlibDisplay, false);
		xlib.XReparentWindow(xlibDisplay, win, parent, -9999, -9999);

		if (leaderChange != null) {
			xlib.XSetWMHints(xlibDisplay, win, leaderChange);
		}

		xlib.XMapWindow(xlibDisplay, win);
		xlib.XSync(xlibDisplay, false);
	}

	@Override
	public String getTitle() {
		try {
			return window.wm_name();
		} catch (Exception e) {
			//
		} catch (Error e) {
			// BadWindow
		}
		return null;
	}

	@Override
	public void destroy() {
		this.window.destroy();
	}

	@Override
	public String getClassName() {
		try {
			return window.wm_class();
		} catch (Exception e) {
			//
		} catch (Error e) {
			// BadWindow
		}
		return null;
	}
	
	@Override
	public int getPid() {
		try {
			return window.wm_pid();
		} catch (Exception e) {
			//
		} catch (Error e) {
			// BadWindow
		}
		return -1;
	}
}
