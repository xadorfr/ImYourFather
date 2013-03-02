package fr.labri.reparenting.api.launchprocess;

import static org.junit.Assert.*;
import java.io.FileNotFoundException;
import org.junit.Test;

import fr.labri.reparenting.api.launchprocess.exception.AlreadyLaunchedException;
import fr.labri.reparenting.api.launchprocess.winapi.Process;

public class TUWinProcess {
	
	@Test(expected=FileNotFoundException.class)
	public void launchFailBadPath() throws FileNotFoundException {
		new Process("");
	}
	
	@Test
	public void launchAndClose() throws InterruptedException {
		Process p = null;
		try {
			 p = new Process("C:/Program Files (x86)/MATLAB/R2007b/bin/win32/MATLAB.exe");
			 p.launch("-nodesktop -nosplash");
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
		} catch (AlreadyLaunchedException e) {
			fail(e.getMessage());
		}
		
		assertTrue(p.getPID() > 0);
		System.out.println("PID: " + p.getPID());
		
		Thread.sleep(2000);
		
		p.terminate();
	}
}
