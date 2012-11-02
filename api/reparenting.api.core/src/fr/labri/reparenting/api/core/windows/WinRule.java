package fr.labri.reparenting.api.core.windows;

import fr.labri.reparenting.api.core.ParentWindowManager;
import fr.labri.reparenting.api.core.Rule;

public class WinRule extends Rule<WinWindow>{
	public WinRule(ParentWindowManager wm, String id) {
		super(wm, id);
	}
}
