package de.secondsystem.game01.impl.game.entities;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.gui.ThumbnailButton.ThumbnailData;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.ISerializable;
import de.secondsystem.game01.model.IUpdateable;

public interface IGameEntityManager extends IUpdateable, ISerializable {

	List<String> listArchetypes();
	List<ThumbnailData> generateThumbnails(WorldId currentWorld);
	
	IGameEntity create(String type, Map<String, Object> attributes);
	IGameEntity create(UUID uuid, String type, Map<String, Object> attributes);
	IControllableGameEntity createControllable(String type, Map<String, Object> attributes);
	
	
	void destroy(UUID eId);
	List<IGameEntity> findEntities(WorldId worldId, Vector2f point);
	Set<IGameEntity> listByGroup(String group);
	
	IGameEntity get(UUID eId);
	IWeakGameEntityRef getRef(UUID eId);
	
	void deserialize(Attributes attributes);

	void draw(WorldId worldId, RenderTarget rt);
	
}
