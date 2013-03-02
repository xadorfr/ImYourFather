package fr.labri.reparenting.api.core.windows;

public class WinWindowEvent {
	public static enum EventType { WINWINDOW_CREATION , WINWINDOW_DESTRUCTION };
	private EventType event;
	private int handle;
	
	public WinWindowEvent(EventType event, int handle) {
		this.event = event;
		this.handle = handle;
	}

	/**
	 * @return the handle
	 */
	public int getHandle() {
		return handle;
	}

	/**
	 * @return the event
	 */
	public EventType getEvent() {
		return event;
	}	
}
