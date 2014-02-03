package de.secondsystem.game01.impl.map.objects;

import java.util.Map;
import java.util.UUID;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.model.Attributes;

public class EntityLayerObject implements ILayerObject {

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

	@Override
	public void draw(RenderTarget renderTarget) {
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
		// ignored; throw something at caller, maybe?
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
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public static ILayerObject create(IGameMap map, WorldId worldId, Map<String, Object> attributes) {
		throw new UnsupportedOperationException();
	}
}
