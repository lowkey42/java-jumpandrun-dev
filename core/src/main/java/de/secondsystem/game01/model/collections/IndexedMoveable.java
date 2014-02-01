package de.secondsystem.game01.model.collections;

import org.jsfml.system.Vector2f;

import de.secondsystem.game01.model.IMoveable;

@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class IndexedMoveable implements IMoveable {

	private ISpatialIndex index; 
	
	@Override
	public final void setPosition(Vector2f pos) {
		final Vector2f oldPos = getPosition();
		
		doSetPosition(pos);
		
		positionUpdated(oldPos);
	}
	
	protected final void positionUpdated(Vector2f oldPosition) {
		index.update(this, oldPosition);
	}
	
	protected abstract void doSetPosition(Vector2f pos);

	void setIndex(ISpatialIndex index) {
		this.index = index;
	}
	
}
