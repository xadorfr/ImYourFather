package fr.labri.reparenting.api.launchprocess.apijava;

import java.util.ArrayList;
import java.util.List;

import fr.labri.reparenting.api.launchprocess.apijava.listener.DestroyListener;


/**
 * 
 *
 */
class DestroyWatcher extends ProcessHandler {
	private List<DestroyListener> listener;

	public DestroyWatcher(Process p) {
		super.process = p;
		listener = new ArrayList<>();
	}

	/**
	 * @param l
	 */
	public void addDestroyListener(DestroyListener l) {
		this.listener.add(l);
	}

	@Override
	public void run() {
		try {
			int status = super.process.waitFor();
			for (DestroyListener l : listener) {
				l.HandleProcessDestroy(status);
			}
		} catch (InterruptedException e) {
			return;
		} catch (NullPointerException e) {
			return;
		}
	}
}