package fr.labri.reparenting.api.core;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Abstract WinIdentifier specialized in String prop
 * It is based on expreg match
 */
public abstract class WindowPropStringIndentifier<T extends Window> implements WindowIdentifier<T> {
	private Pattern assertValue;
	
	public WindowPropStringIndentifier(String assertValue) throws PatternSyntaxException {
			this.assertValue = Pattern.compile(assertValue);
	}
	
	@Override
	public boolean identify(T event) {
		String prop = getProp(event);
		if(prop == null) {
			return false;
		}
		return assertValue.matcher(prop).matches();
	}

	protected abstract String getProp(T win);
}
