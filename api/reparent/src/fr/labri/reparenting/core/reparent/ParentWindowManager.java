package fr.labri.reparenting.core.reparent;

/**
 * Interface to be implemented by the user of the API to give to a ReparentManager a way to manipulate 
 * parent windows
 */
public interface ParentWindowManager {	
	/**
	 * @param window
	 * @return the handle of a given parent window (specific to the underlying window system win32, X11...)  
	 */
	long getNewParentHandle();	
	
	/**
	 * @param window
	 * give an API entry to apply a post-reparent processing
	 */
	void  postReparent(Window window);
}
