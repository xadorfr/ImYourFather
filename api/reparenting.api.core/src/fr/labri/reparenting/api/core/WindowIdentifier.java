package fr.labri.reparenting.api.core;

/**
 * Base interface for the API classes or user classes implemented to specify 
 * the identification of a Window
 * @see Rule
 */
public interface WindowIdentifier<T extends Window> {
	boolean identify(T window);
}
