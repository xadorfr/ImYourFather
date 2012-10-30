package fr.labri.reparenting.core.x11.xlib;

import com.sun.jna.Native;
import com.sun.jna.platform.unix.X11;

public class XlibFactory {
	private static XlibJnaInterface INSTANCE = null;

	public static XlibJnaInterface getXlib() {
		if (INSTANCE == null) {
			INSTANCE = (XlibJnaInterface) Native.loadLibrary("X11", XlibJnaInterface.class);
			INSTANCE.XSetErrorHandler(new X11.XErrorHandler() {
				@Override
				public int apply(X11.Display disp, X11.XErrorEvent event) {
					return 0;
				}

			});
		}
		return INSTANCE;
	}
}
