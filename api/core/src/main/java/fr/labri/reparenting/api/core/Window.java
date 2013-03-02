package fr.labri.reparenting.api.core;

/**
 * API abstraction of a system's window
 */
public abstract class Window {
	private long windowHandle;
	private boolean destroyed;
	private boolean reparented;

	public Window(long handle) {
		this.windowHandle = handle;
		this.destroyed = false;
		this.reparented = false;
	}

	/**
	 * @return the window's system handle
	 */
	public long getHandle() {
		return windowHandle;
	}

	/**
	 * @return window's caption/title
	 */
	public abstract String getTitle();
	
	/**
	 * @return window's caption/title
	 */
	public abstract String getClassName();
	
	/**
	 * @return PID of the application linked to this window
	 */
	public abstract int getPid();

	/**
	 * Destroy the window
	 */
	public void dispose() {
		if (destroyed) {
			return;
		}
		destroyed = true;
		this.destroy();
	}

	/**
	 * Indicates if the window has been destroyed by the API user
	 * 
	 * @return window's state : destroyed/not destroyed
	 */
	public boolean isDestroyed() {
		return this.destroyed;
	}

	protected abstract void destroy();

	/**
	 * This abstract method is specific to a window system
	 * 
	 * @param handle
	 *            of the new parent
	 */
	protected abstract void setParent(long parentHandle);
	
	
	/* 
	 * method only used in root package
	 */
	void setReparented() {
		reparented = true;
	}
	
	/* 
	 * method only used in root package
	 */
	boolean isReparented() {
		return this.reparented;
	}
}
