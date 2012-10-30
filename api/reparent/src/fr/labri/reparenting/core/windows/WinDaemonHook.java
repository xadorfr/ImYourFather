package fr.labri.reparenting.core.windows;

import java.io.IOException;

import fr.labri.reparenting.api.shared.GenericObservable;
import fr.labri.reparenting.api.shared.GenericObserver;

/**
 * Specific WinHook in order to hook 32 bits apps
 * This Hook needs to be coupled with a hook service : 
 * an external application which sends handles to WinDaemonHook
 * 
 * @see WinHook64, WinHookFactory
 */
public class WinDaemonHook extends WinHook {
	private int port;
	private WinEventCatcher eventCatcher;

	public WinDaemonHook(int port) {
		super();
		this.port = port;
	}

	/**
	 * Specifies the installation of the Hook for Windows
	 */
	@Override
	public void install() {
		try {
			this.eventCatcher = new WinEventCatcher(this.port);
			
			this.eventCatcher.addObserver(new GenericObserver<WinWindowEvent>() {
				@Override
				public void update(GenericObservable<WinWindowEvent> observable,
						WinWindowEvent event) {
					switch (event.getEvent()) {
					case WINWINDOW_CREATION:
						notifyCreation(new WinWindow(event.getHandle()));
						break;
					case WINWINDOW_DESTRUCTION:
						notifyDestruction(event.getHandle());
					}
				}
			});
		} catch (IOException e) {
			// TODO
		}
	}

	/**
	 * Specifies the uninstallation of the Hook for Windows
	 */
	@Override
	public void uninstall() {
		if (this.eventCatcher != null) {
			this.eventCatcher.stop();
		}
	}


}
