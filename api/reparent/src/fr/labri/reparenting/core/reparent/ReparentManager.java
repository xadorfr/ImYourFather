package fr.labri.reparenting.core.reparent;

import java.util.HashSet;
import java.util.Set;

import fr.labri.reparenting.api.shared.GenericObserver;
import fr.labri.reparenting.api.shared.GenericObservable;

/**
 * A ReparentManager is a kind of reparenting profile.
 * It manages a set of Rule.
 */
public class ReparentManager<T extends Window> extends
		GenericObservable<RuleEvent<T>> {
	
	private GenericObserver<RuleEvent<T>> observer;
	private Set<Rule<T>> rules;

	public ReparentManager() {
		this.rules = new HashSet<Rule<T>>();
		this.observer = new GenericObserver<RuleEvent<T>>() {
			@Override
			public void update(GenericObservable<RuleEvent<T>> rule,
					RuleEvent<T> event) {
				notifyObservers(event);
			}
		};
	}

	/**
	 * Add a rule to be managed by the ReparentManager
	 * 
	 * @param rule
	 */
	public void addRule(Rule<T> rule) {
		if (rules.add(rule)) {
			rule.addObserver(this.observer);
			notifyObservers(new RuleEvent<T>(RuleEvent.Type.ADD, rule));
		}
	}

	/**
	 * Remove the rule of the list from rules managed by the ReparentManager
	 * 
	 * @param rule
	 */
	public void removeRule(Rule<T> rule) {
		if (rules.remove(rule)) {
			rule.removeObserver(this.observer);
			notifyObservers(new RuleEvent<T>(RuleEvent.Type.REMOVE, rule));
		}
	}

	/**
	 * Called by the Hook whose rule is related to<br>
	 * The method returns as soon as a Rule identified the Window
	 * 
	 * @param window
	 * @return true if one of the Rule has reparented the window, and false
	 *         otherwise
	 */
	boolean checkWindow(T window) {
		for (Rule<T> rule : rules) {
			if (rule.check(window)) {
				return true;
			}
		}
		return false;
	}
}
