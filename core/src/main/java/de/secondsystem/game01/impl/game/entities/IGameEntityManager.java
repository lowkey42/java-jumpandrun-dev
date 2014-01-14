package de.secondsystem.game01.impl.game.entities;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;
import org.json.simple.JSONArray;

import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.model.IUpdateable;

public interface IGameEntityManager extends IUpdateable {

	Set<String> listArchetypes();
	
	IGameEntity create(String type, Map<String, Object> attributes);
	IGameEntity create(UUID uuid, String type, Map<String, Object> attributes);
	IControllableGameEntity createControllable(String type, Map<String, Object> attributes);
	
	void destroy(UUID eId);
	IGameEntity findEntity(Vector2f pos);
	
	IGameEntity get(UUID eId);
	IWeakGameEntityRef getRef(UUID eId);
	
	void deserialize(JSONArray jArray);
	JSONArray serialize();
	
	ArrayList<String> getArchetypes();
//	void deserialize( Iterator<SerializedEntity> iter );
//	Iterable<SerializedEntity> serialize();

	void draw(WorldId worldId, RenderTarget rt);
	
//	public interface SerializedEntity {
//		UUID uuid();
//		String archetype();
//		Map<String, Object> attributes();
//	}
	
}
