package fr.labri.reparenting.api.launchprocess.apijava;

/**
 * Abstract class common
 * @StreamHandler 
 * @see DestroyWatcher
 */
abstract class ProcessHandler implements Runnable {
	protected Process process;
	
	Process getProcess() {
		return this.process;
	}
	
	void setProcess(Process p) {
		this.process = p;
	}
}
