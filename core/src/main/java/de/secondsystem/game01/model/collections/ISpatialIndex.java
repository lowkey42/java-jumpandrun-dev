package de.secondsystem.game01.model.collections;

import org.jsfml.graphics.FloatRect;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.model.IMoveable;

public interface ISpatialIndex<T extends IMoveable> {

	void add(T obj);

	void remove(T obj);

	void update(T obj, Vector2f lastPosition);

	void query(FloatRect area, EntryWalker<T> walker);

	void query(Vector2f point, float range,	EntryWalker<T> walker);

	public abstract class EntryWalker<T extends IMoveable> {
		public abstract void walk(T entry);
		public void finished() {}
	}
}