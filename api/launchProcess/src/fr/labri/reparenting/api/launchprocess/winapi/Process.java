package fr.labri.reparenting.api.launchprocess.winapi;

import java.io.File;
import java.io.FileNotFoundException;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinBase.PROCESS_INFORMATION;
import com.sun.jna.platform.win32.WinBase.STARTUPINFO;

import fr.labri.reparenting.api.launchprocess.exception.AlreadyLaunchedException;

public class Process {
	
	private String path;
	private PROCESS_INFORMATION.ByReference processInformation;
	private STARTUPINFO startupInfo;
	boolean started;
	private Kernel32 kernel32;
	
	public Process(String path) throws FileNotFoundException {
		if(! new File(path).exists()) {
			throw new FileNotFoundException();
		}
		
		try {
			kernel32 = Kernel32.INSTANCE;
		} catch (UnsatisfiedLinkError e) {
			// TODO
		}
		
		this.path = path;
        this.processInformation = new PROCESS_INFORMATION.ByReference();
        this.startupInfo = new WinBase.STARTUPINFO();
        this.started = false;
	}

	public void launch(String args) throws AlreadyLaunchedException {
		if(started) {
			throw new AlreadyLaunchedException();
		}
		
		if(args == null) {
			args = "";
		}
		
        String strArgs = (args.isEmpty()) ? null : " " + args;
        boolean ret = kernel32.CreateProcess(
        		this.path, 
        		strArgs, 
        		null,
        		null, 
        		false, 
        		null, 
        		null, 
        		null, 
        		startupInfo, 
        		processInformation);
        
        if(! ret) {
        	// TODO
        	return;
        }

        
        this.started = true;
	}
	
	
	/**
	 * 
	 * @return -1 if process has not been launched
	 */
	public int getPID() {
		return processInformation.dwProcessId.intValue();
	}
	
	/**
	 * Kill process
	 * @return exit_value
	 */
	public void terminate() {
		int i = 0;
		kernel32.TerminateProcess(processInformation.hProcess, i);
	}
}
