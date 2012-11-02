package fr.labri.reparenting.api.launchprocess.apijava;

import java.io.IOException;

import fr.labri.reparenting.api.launchprocess.apijava.listener.DestroyListener;
import fr.labri.reparenting.api.launchprocess.apijava.listener.OutstreamListener;
import fr.labri.reparenting.api.launchprocess.exception.AlreadyLaunchedException;
import fr.labri.reparenting.api.launchprocess.exception.NotLaunchedException;

public interface Launcher {
	public void launchDetached() throws IOException;
	public void launch(String args) throws IOException, AlreadyLaunchedException;
	public int  kill() throws NotLaunchedException;
	public void addDestroyListener(DestroyListener l);
	public void addStandardOutstreamListener(OutstreamListener l);
	public void addErrorOutstreamListener(OutstreamListener l);
}
