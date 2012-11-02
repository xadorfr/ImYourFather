package fr.labri.reparenting.api.core;

import fr.labri.reparenting.api.core.WindowIdentifier;

public class WindowProcessIdentifier<T extends Window> implements WindowIdentifier<T> {
	private int pid;
	
	public WindowProcessIdentifier(int pid) {
		this.pid = pid;
	}

	@Override
	public boolean identify(T window) {
		if(window.getPid() == pid) {
			return true;
		}
		return false;
	}
}
