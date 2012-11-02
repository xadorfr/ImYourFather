package fr.labri.reparenting.plugin.core.extension;

import fr.labri.reparenting.api.core.Rule;
import fr.labri.reparenting.api.core.Window;
import fr.labri.reparenting.api.core.WindowProcessIdentifier;

public class PidListener<T extends Window> {
	private Rule<T> pidRule;
	
	public PidListener(Rule<T> rule) {
		this.pidRule = rule;
	}
	
	public void notifyPid(int pid) {
		pidRule.addIdentifiers(new WindowProcessIdentifier<T>(pid));
		pidRule.activate();
	}
}
