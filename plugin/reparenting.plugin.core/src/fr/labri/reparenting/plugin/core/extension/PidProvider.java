package fr.labri.reparenting.plugin.core.extension;

import org.eclipse.core.runtime.IExecutableExtensionFactory;

import fr.labri.reparenting.api.core.Window;

public interface PidProvider extends IExecutableExtensionFactory {
	public void setPidListener(PidListener<? extends Window> p);
}
