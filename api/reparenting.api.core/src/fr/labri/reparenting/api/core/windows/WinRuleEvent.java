package fr.labri.reparenting.api.core.windows;

import fr.labri.reparenting.api.core.Rule;
import fr.labri.reparenting.api.core.RuleEvent;

public class WinRuleEvent extends RuleEvent<WinWindow>{

	public WinRuleEvent(fr.labri.reparenting.api.core.RuleEvent.Type type,
			Rule<WinWindow> rule) {
		super(type, rule);
	}

}
