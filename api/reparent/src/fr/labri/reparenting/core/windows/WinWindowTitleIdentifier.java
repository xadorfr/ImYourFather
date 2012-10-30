package fr.labri.reparenting.core.windows;

import java.util.regex.PatternSyntaxException;
import fr.labri.reparenting.core.reparent.WindowPropStringIndentifier;
import fr.labri.reparenting.core.winapi.WinapiWrapper;

/**
 * Specific implementation of WindowPropStringIdentifier 
 * specialized in win32 window title (caption)
 */
public class WinWindowTitleIdentifier extends WindowPropStringIndentifier<WinWindow> {

	public WinWindowTitleIdentifier(String assertProp) throws PatternSyntaxException {
		super(assertProp);
	}

	@Override
	protected String getProp(WinWindow window) {
		return WinapiWrapper.GetWindowName(window.getHandle());
	}
}
