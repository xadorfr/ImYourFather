package fr.labri.reparenting.core.reparent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.labri.reparenting.api.shared.GenericObservable;

/**
 * 
 */
public abstract class SystemListener<T extends Window> extends GenericObservable<Window> {
	private Set<ReparentManager<T>> reparentManagers;
	private Map<Long, Window> reparentedWindows;

	protected SystemListener() {
		this.reparentManagers = new HashSet<ReparentManager<T>>();
		this.reparentedWindows = new HashMap<Long, Window>();
	}

	/**
	 * Associate a ReparentManager with SystemListener
	 * 
	 * @param a
	 *            ReparentManager
	 */
	public void addReparentManager(ReparentManager<T> manager) {
		reparentManagers.add(manager);
	}

	/**
	 * Disassociate a ReparentManager from the SystemListener
	 * 
	 * @param a
	 *            previously associated ReparentManager
	 */
	public void removeParentManager(ReparentManager<T> manager) {
		reparentManagers.remove(manager);
	}

	/**
	 * This method is called by the implementations of this abstract class It
	 * notifies ReparentManager(s) coupled with the hook about a window's
	 * creation <br>
	 * If one reparent manager successfully reparented (i.e. reparent() returns
	 * true) the window, the function quits
	 * 
	 * @param window
	 */
	protected void notifyCreation(T window) {
		for (ReparentManager<T> rm : reparentManagers) {
			if (rm.checkWindow(window)) {
				this.reparentedWindows.put(window.getHandle(), window);
				break;
			}
		}
	}

	/**
	 * This method is intended to be called by the implementations of this
	 * abstract class every time a window is destroyed. <br>
	 * The API lets the user subscribe to this event and handle it Notification
	 * only occurs when a reparented window is destroyed
	 * 
	 * @param window
	 * @see tagWindow(), isTagged()
	 */
	protected void notifyDestruction(long handle) {
		// we check if the handle match to a reparented window
		Window w = this.reparentedWindows.get(handle);
		if (w != null) {
			// we check that the window hasn't already been destroyed by 
			// user call to Window.destroy()
			if(w.isDestroyed()) { 
				reparentedWindows.remove(handle);
				return;
			}
			notifyObservers(w);
		}
	}

	/**
	 * System dependent method
	 */
	public abstract void install();

	/**
	 * System dependent method
	 */
	public abstract void uninstall();
}
