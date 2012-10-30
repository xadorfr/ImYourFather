package fr.labri.reparenting.core.windows;

import java.util.regex.PatternSyntaxException;

import fr.labri.reparenting.core.reparent.WindowPropStringIndentifier;

/**
 * Specific implementation of WindowPropStringIdentifier 
 * specialized in win32 window classname
 */
public class WinWindowClassIdentifier extends WindowPropStringIndentifier<WinWindow> {

	public WinWindowClassIdentifier(String assertProp) throws PatternSyntaxException {
		super(assertProp);
	}

	@Override
	protected String getProp(WinWindow window) {
		return window.getClassName();
	}
}
