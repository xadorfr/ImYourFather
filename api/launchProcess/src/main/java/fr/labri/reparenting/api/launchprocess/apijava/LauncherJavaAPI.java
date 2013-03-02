package fr.labri.reparenting.api.launchprocess.apijava;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.labri.reparenting.api.launchprocess.apijava.listener.DestroyListener;
import fr.labri.reparenting.api.launchprocess.apijava.listener.OutstreamListener;
import fr.labri.reparenting.api.launchprocess.exception.AlreadyLaunchedException;

public class LauncherJavaAPI implements Launcher {
	private File executable;
	private Process process;
	private String path;
	
	private boolean launched;
	private boolean detached;

	private StreamHandler standardStreamHandler;
	private StreamHandler errorStreamHandler;
	private DestroyWatcher destroyWatcher;
	
	private List<Thread> workers;

	public LauncherJavaAPI(String path) throws IOException {
		this.executable = new File(path);
		if (! executable.exists())
			throw new IOException("Invalid executable path");

		this.path = path;
		this.process = null;
		this.launched = false;
		
		standardStreamHandler = null;
		errorStreamHandler = null;
		destroyWatcher = null;
		
		this.workers = new ArrayList<>();
	}

	public void launchDetached() throws IOException {
			Desktop.getDesktop().open(executable);
			this.launched = true;
			this.detached = true;
	}
	
	public void launch(String args) throws IOException, AlreadyLaunchedException{
		if(this.launched)
			throw new AlreadyLaunchedException();
			
		this.process = Runtime.getRuntime().exec(path + " " + args);
		this.launched = true;
		this.detached = false;
		
		standardStreamHandler = new StreamHandler(this.process, StreamHandler.typeHandler.STANDARD);
		errorStreamHandler = new StreamHandler(this.process, StreamHandler.typeHandler.ERROR);
		destroyWatcher = new DestroyWatcher(this.process);
		
		workers.add(new Thread(destroyWatcher));
		workers.add(new Thread(standardStreamHandler));
		workers.add(new Thread(errorStreamHandler));
		
		for(Thread t : workers) {
			t.start();
		}	
	}
	
	public int kill() {
		if(! this.launched || this.detached) {
			return -1;
		}
		
		destroyWatcher.setProcess(null);
		standardStreamHandler.setProcess(null);
		errorStreamHandler.setProcess(null);
		
		this.process.destroy();
		
		this.launched = false;
		
		return this.process.exitValue();
	}
	
	public void addDestroyListener(DestroyListener l) {
		if(destroyWatcher == null) {
			return;
		}
		
		destroyWatcher.addDestroyListener(l);
	}

	public void addStandardOutstreamListener(OutstreamListener l) {
		if(standardStreamHandler == null) {
			return;
		}
		
		standardStreamHandler.addOutstreamListener(l);
	}

	public void addErrorOutstreamListener(OutstreamListener l) {
		if(errorStreamHandler == null) {
			return;
		}
		
		errorStreamHandler.addOutstreamListener(l);
	}
}
