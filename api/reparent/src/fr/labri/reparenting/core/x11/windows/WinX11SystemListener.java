package fr.labri.reparenting.core.x11.windows;

import fr.labri.reparenting.api.core.exception.X11ConnexionException;
import fr.labri.reparenting.core.x11.X11SystemListener;

public class WinX11SystemListener extends X11SystemListener{

	public WinX11SystemListener(int displayNumber, int screenNumber) throws X11ConnexionException {
		super(displayNumber, screenNumber);
	}

	/* 
	 * @see X11Hook 
	 */
	@Override
	protected WinX11Window getX11Window(long handle, gnu.x11.Display display) {
			return new WinX11Window(new gnu.x11.Window(display, (int) handle));
	}
}
