package fr.labri.reparenting.core.reparent;

/**
 * @param <T>
 */
public class RuleEvent<T extends Window> {
	public enum Type { ADD, REMOVE, STATE_CHANGE };
	private Rule<T> rule;
	private Type type;
	
	public RuleEvent(Type type, Rule<T> rule) {
		this.rule = rule;
		this.type = type;
	}
	
	public Rule<T> getRule() {
		return rule;
	}
	
	public Type getType() {
		return type;
	}
}
