package de.secondsystem.game01.impl.map.objects;

import java.util.Map;
import java.util.UUID;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.impl.map.LayerType;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.IUpdateable;

public class EntityLayerObject implements ILayerObject, IUpdateable {

	public final UUID uuid;
	
	public final String type;
	
	public final Attributes attributes;
	
	final IGameEntity entity;
	

	public EntityLayerObject(IGameEntity entity) {
		this.uuid = entity.uuid();
		this.type = entity.getEditableState()!=null ? entity.getEditableState().getArchetype() : null;
		this.attributes = entity.getEditableState()!=null ? entity.getEditableState().getAttributes() : null;
		this.entity = entity;
	}
	
	public EntityLayerObject(UUID uuid, String type, Attributes attributes) {
		this.uuid = uuid;
		this.type = type;
		this.attributes = attributes;
		this.entity = null;
	}

	public EntityLayerObject(IGameMap map, String archetype, Attributes attributes) {
		this( map.getEntityManager().createEntity(archetype, attributes) );
	}
	
	@Override
	public LayerType getLayerType() {
		return LayerType.OBJECTS;
	}
	@Override
	public void setLayerType(LayerType layerType) {
	}
	
	public void remove(IGameMap map) {
		map.getEntityManager().destroyEntity(uuid);
		entity.onDestroy();
	}
	
	public void updateAttributes() {
		attributes.put("width", getWidth());
		attributes.put("height", getHeight());
		attributes.put("x", getPosition().x);
		attributes.put("y", getPosition().y);
		attributes.put("rotation", getRotation());
	}
	
	@Override
	public void draw(RenderTarget renderTarget) {
		entity.draw(renderTarget);
	}

	@Override
	public boolean inside(Vector2f point) {
		return entity.inside(point);
	}

	@Override
	public void setPosition(Vector2f pos) {
		entity.setPosition(pos);
	}

	@Override
	public void setRotation(float degree) {
		entity.setRotation(degree);
	}

	@Override
	public void setDimensions(float width, float height) {
		entity.setDimensions(width, height);
	}

	@Override
	public float getHeight() {
		return entity.getHeight();
	}

	@Override
	public float getWidth() {
		return entity.getWidth();
	}

	@Override
	public float getRotation() {
		return entity.getRotation();
	}

	@Override
	public Vector2f getPosition() {
		return entity.getPosition();
	}

	@Override
	public LayerObjectType typeUuid() {
		return LayerObjectType.ENTITY;
	}
	
	@Override
	public boolean isInWorld(WorldId worldId) {
		return entity.isInWorld(worldId);
	}
	
	@Override
	public void setWorld(WorldId worldId, boolean exists) {
		if( exists )
			entity.setWorldMask(entity.getWorldMask()|worldId.id);
		else
			entity.setWorldMask(entity.getWorldMask()&~worldId.id);
	}
	
	@Override
	public Attributes serialize() {
		return attributes;
	}
	
	public IGameEntity getEntity() {
		return entity;
	}
	@Override
	public void update(long frameTimeMs) {
		entity.update(frameTimeMs);	
	}
	

	public static ILayerObject create(IGameMap map, Map<String, Object> attributes) {
		return new EntityLayerObject(map.getEntityManager().create(
				attributes.containsKey("uuid") ? UUID.fromString((String)attributes.get("uuid")) : null, 
				(String)attributes.get("archetype"), 
				attributes));
	}
}
