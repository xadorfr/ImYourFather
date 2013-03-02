package fr.labri.reparenting.plugin.core.extension;

import org.eclipse.core.runtime.IExecutableExtensionFactory;

public interface RuleProvider extends IExecutableExtensionFactory{
	void fillRuleManager(RuleManager rm);
}
