package fr.labri.reparenting.plugin.core;

import fr.labri.reparenting.api.core.RuleEvent;
import fr.labri.reparenting.api.core.x11.X11Window;
import fr.labri.reparenting.api.shared.GenericObserver;

public interface X11Event extends GenericObserver<RuleEvent<X11Window>>{

}
