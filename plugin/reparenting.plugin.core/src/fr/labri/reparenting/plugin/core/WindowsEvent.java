package fr.labri.reparenting.plugin.core;

import fr.labri.reparenting.api.core.RuleEvent;
import fr.labri.reparenting.api.core.windows.WinWindow;
import fr.labri.reparenting.api.shared.GenericObserver;

public interface WindowsEvent extends GenericObserver<RuleEvent<WinWindow>>{
	
}
