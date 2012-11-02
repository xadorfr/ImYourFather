package fr.labri.reparenting.api.core.x11.xlib;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.ptr.PointerByReference;

public interface XlibJnaInterface extends X11 {
	public static long WindowGroupHint = (1L << 6);

	int XSetWMHints(Display display, Window w, XWMHints wmhints);
	int XReparentWindow(Display display, Window w, Window parent, int x, int y); 
	int XFetchName(Display display, Window w, PointerByReference nameReturn);
	int XWithdrawWindow(Display display, Window w, int screen_number);
}