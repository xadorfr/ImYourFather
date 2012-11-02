package fr.labri.reparenting.api.core.x11;

import fr.labri.reparenting.api.core.exception.X11ConnexionException;

public class UnixX11SystemListener extends X11SystemListener{
	
	public UnixX11SystemListener(int displayNumber, int screenNumber) throws X11ConnexionException {
		super(displayNumber, screenNumber);
	}

	@Override
	protected X11Window getX11Window(long handle, gnu.x11.Display display) {
		return new X11Window(new gnu.x11.Window(display, (int) handle));
	}
}
