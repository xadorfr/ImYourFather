package fr.labri.reparenting.core.windows;

import fr.labri.reparenting.core.reparent.ParentWindowManager;
import fr.labri.reparenting.core.reparent.Rule;

public class WinRule extends Rule<WinWindow>{
	public WinRule(ParentWindowManager wm, String id) {
		super(wm, id);
	}
}
