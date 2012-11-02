package fr.labri.reparenting.api.shared;

public interface GenericObserver<T> {
	void update(GenericObservable<T> observable, T arg);
}
