package de.secondsystem.game01.impl.game.entities;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IUpdateable;

public interface IGameEntityManager extends IDrawable, IUpdateable {

	Set<String> listArchetypes();
	
	IGameEntity create(String type, Map<String, Object> attributes);
	IGameEntity create(UUID uuid, String type, Map<String, Object> attributes);
	IControllableGameEntity createControllable(String type, Map<String, Object> attributes);
	
	void destroy(UUID eId);

	IGameEntity get(UUID eId);

	void deserialize( Iterator<SerializedEntity> iter );
	Iterable<SerializedEntity> serialize();
	
	public interface SerializedEntity {
		UUID uuid();
		String archetype();
		Map<String, Object> attributes();
	}
	
}
