package fr.labri.reparenting.api.core.x11;

import java.net.Socket;

import fr.labri.reparenting.api.shared.StoppableThread;
import fr.labri.reparenting.api.shared.SystemValidator;
import fr.labri.unixsocket.UnixSocket;

import fr.labri.reparenting.api.core.SystemListener;
import fr.labri.reparenting.api.core.exception.X11ConnexionException;
import gnu.x11.event.CreateNotify;
import gnu.x11.event.DestroyNotify;
import gnu.x11.event.Event;

/**
 * Common approach for a X11 hook on Unix and Windows
 */
public abstract class X11SystemListener extends SystemListener<X11Window> {
	private gnu.x11.Display display;
	private StoppableThread msgHandler;
	private gnu.x11.Window rootWindow;
	private static String UNIX_SOCKET_PATH = "/tmp/.X11-unix/X";

	public X11SystemListener(int displayNumber, int screenNumber)
			throws X11ConnexionException {
		this("localhost", displayNumber, screenNumber);
	}

	public X11SystemListener(String hostname, int displayNumber, int screenNumber)
			throws X11ConnexionException {
		try {
			if(SystemValidator.isWindows()) {
                this.display = new gnu.x11.Display(hostname, displayNumber,
                                                   screenNumber);
			} else if(SystemValidator.isUnix()){
				Socket unixSock = new UnixSocket(UNIX_SOCKET_PATH + displayNumber);
				this.display = new gnu.x11.Display(unixSock, null, displayNumber, screenNumber);
			} else {
				throw new Exception("Platform not supported");
			}
		} catch (Exception e) {
			/*
			 * java.net.ConnectException : can't connect to X11 serv
			 * java.lang.RuntimeException : can't reach X11 serv
			 */
			throw new X11ConnexionException(displayNumber, screenNumber);
		}

		this.rootWindow = display.default_root;
	}

	@Override
	public void install() {
		msgHandler = new StoppableThread(new Runnable() {
			@Override
			public void run() {
				handleMessages();
			}
		});
		msgHandler.start();
	}

	@Override
	public void uninstall() {
		rootWindow.select_input(gnu.x11.event.Input.NO_EVENT_MASK);

		this.msgHandler.sendStopSignal();
	}

	private void handleMessages() {
		rootWindow.select_input(gnu.x11.event.Input.SUBSTRUCTURE_NOTIFY_MASK);
		display.flush();

		/* event loop */
		Event event;
		while (true) {
			try {
				event = this.display.next_event();
				switch (event.code) {

				case gnu.x11.event.CreateNotify.CODE: {
					CreateNotify createEvent = (CreateNotify) event;
					int createdWinId = createEvent.window_id;
					if (createdWinId == 0) {
						continue;
					}

					/* notification */
					X11Window createdWindow = getX11Window(createdWinId,
							this.display);
					if (createdWindow != null) {
						super.notifyCreation(createdWindow);
					}
					break;
				}

				case gnu.x11.event.DestroyNotify.CODE: {
					DestroyNotify destroyEvent = (DestroyNotify) event;
					int destroyedWinId = destroyEvent.window_id;
					if (destroyedWinId == 0) {
						continue;
					}

					super.notifyDestruction(destroyedWinId);
				}
				}

				/* stopability */
				try {
					this.msgHandler.checkMessages();
				} catch (InterruptedException e) {
					return;
				}
			} catch (gnu.x11.Error e) {
				// bad window...
			}
		}
	}

	/**
	 * @param handle
	 * @param display
	 * @return a Window which extends X11Window
	 */
	protected abstract X11Window getX11Window(long handle,
			gnu.x11.Display display);
}
