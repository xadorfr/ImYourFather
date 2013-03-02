package fr.labri.reparenting.api.shared;

import java.util.HashSet;
import java.util.Set;

public abstract class GenericObservable<T> {
	private Set<GenericObserver<T>> listeners;
	
	public GenericObservable() {
		listeners = new HashSet<>();
	}
	
	public void addObserver(GenericObserver<T> obs) {
		this.listeners.add(obs);
	}
	
	public void removeObserver(GenericObserver<T> obs) {
		this.listeners.remove(obs);
	}
	
	protected void notifyObservers(T obj) {
		for(GenericObserver<T> l : listeners){
			l.update(this, obj);
		}
	}
}