package fr.labri.reparenting.api.shared;

public class StoppableThread extends Thread {
	private boolean stopThread = false;
	
	public StoppableThread(Runnable r) {
		super(r);
	}

	public StoppableThread() {
		super();
	}

	public synchronized void checkMessages() throws InterruptedException {
	        if( stopThread ) {
	             throw new InterruptedException();
	        }
	}
	
	public synchronized void sendStopSignal() {
	        this.stopThread = true;
	} 
}
