package fr.labri.reparenting.core.x11;

import fr.labri.reparenting.core.reparent.ParentWindowManager;
import fr.labri.reparenting.core.reparent.Rule;

public class X11Rule extends Rule<X11Window>{
	private int displayNum;
	private int screenNum;
	
	public X11Rule(ParentWindowManager wm, String id, int displayNum, int screenNum) {
		super(wm, id);
		this.displayNum = displayNum;
		this.screenNum = screenNum;
	}

	public int getDisplayNum() {
		return this.displayNum;
	}
	
	public int getScreenNum() {
		return this.screenNum;
	}
}
