package fr.labri.reparenting.plugin.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import fr.labri.reparenting.api.core.Rule;
import fr.labri.reparenting.api.core.Window;
import fr.labri.reparenting.api.core.windows.WinRule;
import fr.labri.reparenting.api.core.windows.WinWindow;
import fr.labri.reparenting.api.core.windows.WinWindowClassIdentifier;
import fr.labri.reparenting.api.core.windows.WinWindowTitleIdentifier;
import fr.labri.reparenting.api.core.x11.X11Rule;
import fr.labri.reparenting.api.core.x11.X11Window;
import fr.labri.reparenting.api.core.x11.X11WindowClassIdentifier;
import fr.labri.reparenting.api.core.x11.X11WindowTitleIdentifier;
import fr.labri.reparenting.plugin.core.extension.PidListener;
import fr.labri.reparenting.plugin.core.extension.PidProvider;
import fr.labri.reparenting.plugin.core.extension.RCPParentWindowManager;
import fr.labri.reparenting.plugin.core.extension.RuleManager;
import fr.labri.reparenting.plugin.core.extension.RuleProvider;
import fr.labri.reparenting.plugin.core.view.ControlView;
import fr.labri.reparenting.plugin.util.ExtensionLoader;
import fr.labri.reparenting.plugin.util.NoExtensionFoundException;

public class ExtensionManager {
	private static String EXT_ID = "cea.plugin.reparent.ruleProvider";

	/* Windows contrib elts id */
	private final static String WIN_RULE = "windowsRule";
	private final static String WIN_FILTER = "winFilter";
	private final static String WIN_RULE_BITNESS = "bitness";
	private final static String BITNESS_32 = "32 bits";
	private final static String BITNESS_64 = "64 bits";
	private final static String BITNESS_BOTH = "both";

	/* X11 contrib elts id */
	private final static String X11_RULE = "x11Rule";
	private final static String X11_SCREEN_ATTR = "screenNum";
	private final static String X11_DISPLAY_ATTR = "displayNum";
	private final static String X11_SIMPLE_FILTER = "x11SimpleFilter";

	/* java contrib elts id */
	private final static String JAVA_RULE_PROVIDER = "javaRuleProvider";
	private final static String JAVA_CLASS = "class";

	/* PID */
	private final static String PID_FILTER = "pidFilter";
	private final static String PID_PROVIDER = "pidProvider";

	/* Common to X11 and Windows rules */
	private final static String ID_ATTR = "id";
	private final static String DESCR_ATTR = "description";
	private final static String FILTER_PROP_ATTR = "property";
	private final static String FILTER_PROP_TITLE = "title";
	private final static String FILTER_PROP_CLASS = "class";
	private final static String FILTER_PROP_VALUE = "value";

	private static RuleManager ruleManager;

	public static void loadContributions(ControlView view) {

		IConfigurationElement[] contribs;
		try {
			contribs = ExtensionLoader.getConfigurationElements(EXT_ID);
		} catch (NoExtensionFoundException e) {
			ErrorHandler.handleException(e);
			return;
		}

		ruleManager = new RuleManager(view);

		for (IConfigurationElement contrib : contribs) {
			switch (contrib.getName()) {
			case WIN_RULE:
				loadWinContrib(contrib);
				break;
			case X11_RULE:
				loadX11Contrib(contrib);
				break;
			case JAVA_RULE_PROVIDER:
				loadJavaContrib(contrib);
				break;
			default:
				// contribution not supported yet
				break;

			}
		}
	}

	private static void loadJavaContrib(IConfigurationElement contrib) {
		RuleProvider ruleP;
		try {
			ruleP = (RuleProvider) contrib
					.createExecutableExtension(JAVA_CLASS);
		} catch (CoreException | ClassCastException e) {
			return;
		}
		ruleP.fillRuleManager(ruleManager);
	}

	private static void loadX11Contrib(IConfigurationElement x11Contrib) {
		try {
			int dispNum = Integer.valueOf(
					x11Contrib.getAttribute(X11_DISPLAY_ATTR)).intValue();
			int screenNum = Integer.valueOf(
					x11Contrib.getAttribute(X11_SCREEN_ATTR)).intValue();
			X11Rule x11Rule = new X11Rule(new RCPParentWindowManager(), "",
					dispNum, screenNum);
			configRule(x11Rule, x11Contrib);

			loadX11Filters(x11Rule, x11Contrib.getChildren());

			ruleManager.addX11Rule(x11Rule);
		} catch (Throwable t) {
			// bad value ; ...
			return;
		}
	}

	private static void loadWinContrib(IConfigurationElement winContrib) {
		try {
			WinRule winRule = new WinRule(new RCPParentWindowManager(), "");
			configRule(winRule, winContrib);
			String bitness = winContrib.getAttribute(WIN_RULE_BITNESS);

			loadWinFilters(winRule, winContrib.getChildren());

			switch (bitness) {
			case BITNESS_32:
				ruleManager.addWin32Rule(winRule);
				break;
			case BITNESS_64:
				ruleManager.addWin64Rule(winRule);
				break;
			case BITNESS_BOTH:
				ruleManager.addWin32Rule(winRule);
				ruleManager.addWin64Rule(winRule);
				break;
			default:
				// not expected
				return;
			}
		} catch (Throwable t) {
			// bad value ; ...
			return;
		}
	}

	private static void configRule(Rule<? extends Window> rule,
			IConfigurationElement contrib) {
		String id = contrib.getAttribute(ID_ATTR);
		rule.setId(id);

		String descr = contrib.getAttribute(DESCR_ATTR);
		if (descr != null) {
			rule.setDescription(descr);
		}
	}

	private static void loadWinFilters(WinRule winRule,
			IConfigurationElement[] filters) {
		boolean start = true;
		
		for (IConfigurationElement filter : filters) {
			switch (filter.getName()) {
			case WIN_FILTER:
				String property = filter.getAttribute(FILTER_PROP_ATTR);
				String value = filter.getAttribute(FILTER_PROP_VALUE);
				switch (property) {
				case FILTER_PROP_TITLE:
					winRule.addIdentifiers(new WinWindowTitleIdentifier(value));
					break;
				case FILTER_PROP_CLASS:
					winRule.addIdentifiers(new WinWindowClassIdentifier(value));
					break;
				}
				break;
			case PID_FILTER:
				try {
					PidProvider pidProv = (PidProvider) filter
							.createExecutableExtension(PID_PROVIDER);
					pidProv.setPidListener(new PidListener<WinWindow>(winRule));
				} catch (CoreException e) {
					//
				}
				start = false;
				break;
			default:
				// not expected
			}
		}
		
		winRule.activate();
		if(start) {
			winRule.setComplete();
		}
	}

	private static void loadX11Filters(X11Rule x11Rule,
			IConfigurationElement[] filters) {
		boolean start = true;
		for (IConfigurationElement filter : filters) {
			switch (filter.getName()) {
			case X11_SIMPLE_FILTER:
				String property = filter.getAttribute(FILTER_PROP_ATTR);
				String value = filter.getAttribute(FILTER_PROP_VALUE);
				switch (property) {
				case FILTER_PROP_TITLE:
					x11Rule.addIdentifiers(new X11WindowTitleIdentifier(value));
					break;
				case FILTER_PROP_VALUE:
					x11Rule.addIdentifiers(new X11WindowClassIdentifier(value));
					break;
				}
				break;
			case PID_FILTER:
				PidProvider pidProv;
				try {
					pidProv = (PidProvider) filter
							.createExecutableExtension(PID_PROVIDER);
					pidProv.setPidListener(new PidListener<X11Window>(x11Rule));
				} catch (CoreException e) {
					//
				}
				start = false;
				break;
			default:
				// not expected
			}
		}
		
		x11Rule.activate();
		if(start) {
			x11Rule.setComplete();
		}
	}
}
