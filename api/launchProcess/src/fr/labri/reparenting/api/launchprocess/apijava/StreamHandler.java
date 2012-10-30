package fr.labri.reparenting.api.launchprocess.apijava;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import fr.labri.reparenting.api.launchprocess.apijava.listener.OutstreamListener;


/**
 * 
 *
 */
class StreamHandler extends ProcessHandler {
	private typeHandler type;
	private List<OutstreamListener> listener;
	
	public enum typeHandler {STANDARD, ERROR};
	
	public StreamHandler(Process p, typeHandler type) {
		super.process = p;
		this.type = type;
		this.listener = new ArrayList<>();
	}
	
	/**
	 * 
	 * @param l
	 */
	public void addOutstreamListener(OutstreamListener l) {
		this.listener.add(l);
	}
	
	/**
	 * @see java.lang.Runnable
	 */
	public void run() {
		switch(this.type) {
		case STANDARD:
			this.handleOutstream();
			break;
		case ERROR:
			this.handleOutstream();
			break;
		}
	}
		
	private void handleOutstream() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String line;
			try {
				while((line = reader.readLine()) != null) {
					for(OutstreamListener l : listener) {
						l.HandleOutstreamNewline(line);
					}
				}
			} finally {
				reader.close();
			}
		} catch(IOException e) { // reader.readLine failed if process == null
			return;
		} catch (NullPointerException e) {
			return;
		}
	}
}
