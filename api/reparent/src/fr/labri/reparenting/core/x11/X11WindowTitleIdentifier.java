package fr.labri.reparenting.core.x11;

import java.util.regex.PatternSyntaxException;

import fr.labri.reparenting.core.reparent.WindowPropStringIndentifier;

public class X11WindowTitleIdentifier extends
		WindowPropStringIndentifier<X11Window> {

	public X11WindowTitleIdentifier(String assertValue)
			throws PatternSyntaxException {
		super(assertValue);
	}

	@Override
	protected String getProp(X11Window window) {
		return window.getTitle();
	}
}
