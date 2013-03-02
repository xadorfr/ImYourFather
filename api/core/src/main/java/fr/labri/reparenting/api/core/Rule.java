package fr.labri.reparenting.api.core;

import java.util.HashSet;
import java.util.Set;

import fr.labri.reparenting.api.shared.GenericObservable;

/**
 * A Rule manages a set of WindowIdentifier It can be associated with a specific
 * parent through the ParentWindowManager
 * 
 * @see WindowIdentifier
 */
public class Rule<T extends Window> extends GenericObservable<RuleEvent<T>> {
	private Set<WindowIdentifier<T>> identifiers;
	private boolean isActive;
	private boolean isComplete;

	private String id;
	private String description;

	private Set<T> standbyWindows;

	/* delegation */
	protected ParentWindowManager winManager;

	/**
	 * @param wm
	 * @param id
	 */
	public Rule(ParentWindowManager wm, String id) {
		this.winManager = wm;
		this.identifiers = new HashSet<WindowIdentifier<T>>();
		this.standbyWindows = new HashSet<T>();
		this.isActive = false;
		this.isComplete = false;
		this.id = id;
	}

	/**
	 * Id getter
	 * 
	 * @return id
	 */

	public String getId() {
		return id;
	}

	/**
	 * Id Setter
	 * 
	 * @param id
	 */
	public void setId(String id) {
		if (!id.equals(this.id)) {
			this.id = id;
			notifyObservers(new RuleEvent<T>(RuleEvent.Type.STATE_CHANGE, this));
		}
	}

	/**
	 * Description getter
	 * 
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Description setter
	 */
	public void setDescription(String description) {
		if (!description.equals(this.description)) {
			this.description = description;
			notifyObservers(new RuleEvent<T>(RuleEvent.Type.STATE_CHANGE, this));
		}
	}

	/**
	 * Add a WindowIdentifier to the list of WindowIdentifier managed by the
	 * ReparentManager
	 * 
	 * @param id
	 */
	public void addIdentifiers(WindowIdentifier<T> identifier) {
		identifiers.add(identifier);
	}

	/**
	 * Remove a WindowIdentifier to the list of WindowIdentifier managed by the
	 * ReparentManager
	 * 
	 * @param WindowIdentifier
	 */
	public void removeIdentifiers(WindowIdentifier<T> identifier) {
		identifiers.remove(identifier);
	}

	/**
	 * Change the rule's state from incomplete to complete
	 */
	public void setComplete() {
		if(this.isComplete) {
			return;
		}
		
		this.isComplete = true;
		
		for (T w : standbyWindows) {
			check(w);
		}
		
		notifyObservers(new RuleEvent<T>(RuleEvent.Type.STATE_CHANGE, this));
	}

	/**
	 * Activate the rule
	 */
	public void activate() {
		if (! this.isActive) {
			this.isActive = true;
			notifyObservers(new RuleEvent<T>(RuleEvent.Type.STATE_CHANGE, this));	
		}
	}

	/**
	 * Desactivate the rule
	 */
	public void desactivate() {
		if (this.isActive) {
			this.isActive = false;
			notifyObservers(new RuleEvent<T>(RuleEvent.Type.STATE_CHANGE, this));
		}
	}

	/**
	 * 
	 * @return boolean (active/inactive)
	 */
	public boolean isActive() {
		return this.isActive;
	}

	/**
	 * Check if the windows fits to rules and reparent it if it does
	 * 
	 * @param Window
	 * 
	 * @return boolean (reparented/not reparented)
	 */
	boolean check(T window) {
		if (window.isReparented() || !this.isActive) {
			return false;
		}

		if (!checkIdentifiers(window, false)) {
			return false;
		}

		/*
		 * if the rule has not been initialized yet, it means that new
		 * identifiers may appear. We want the rule to be retroactive: we store
		 * the window in order to check it again when the rule will be initialized (start())
		 */
		if (!this.isComplete) {
			standbyWindows.add(window);
			return false;
		}
		
		/* get a handle to the new parent window and reparent */
		long parentHandle = winManager.getNewParentHandle();
		window.setParent(parentHandle);
		winManager.postReparent(window);
		window.setReparented();

		return true;
	}

	/*
	 * Check every identifier with a AND and a OR logic
	 */
	private boolean checkIdentifiers(T window, boolean ORCheck) {
		if (!ORCheck) {
			for (WindowIdentifier<T> wi : identifiers) {
				if (!wi.identify(window)) {
					return false;
				}
			}
		} else {
			for (WindowIdentifier<T> wi : identifiers) {
				if (wi.identify(window)) {
					return true;
				}
			}
		}

		return (!ORCheck);
	}
}