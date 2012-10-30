package fr.labri.reparenting.core.windows;

import fr.labri.reparenting.core.reparent.Rule;
import fr.labri.reparenting.core.reparent.RuleEvent;

public class WinRuleEvent extends RuleEvent<WinWindow>{

	public WinRuleEvent(fr.labri.reparenting.core.reparent.RuleEvent.Type type,
			Rule<WinWindow> rule) {
		super(type, rule);
	}

}
