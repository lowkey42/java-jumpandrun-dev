package de.secondsystem.game01.impl.editor;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.map.GameMap;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;

public class SelectedEditorEntity extends AbstractEditorEntity implements ISelectedEditorObject {
	private SelectedEditorObject selectedObject = new SelectedEditorObject(Color.BLUE, 2.0f, Color.TRANSPARENT);
	private final IGameMap map;
	private int currentWorldId;
	
	public SelectedEditorEntity(IGameMap map) {
		this.map = map;
	}
	
	@Override
	public void update(boolean movedObj, RenderTarget rt, int mousePosX, int mousePosY, float zoom, long frameTimeMs) {	
		selectedObject.update(movedObj, rt, mousePosX, mousePosY, this);
		
		if( entity != null ) {
			Attribute width = new Attribute("width", this.width*this.zoom);
			Attribute height = new Attribute("height", this.height*this.zoom);
			Attribute rotation = new Attribute("rotation", this.rotation);
			Attribute x = new Attribute("x", pos.x);
			Attribute y = new Attribute("y", pos.y);
			Attribute worldId = new Attribute("worldId", currentWorldId);
			
			map.getEntityManager().destroy(entity.uuid());
			setEntity( map.getEntityManager().create(currentArchetype, new Attributes(width, height, rotation, x, y, worldId)) );
		}
	}
	
	public void setEntity(IGameEntity entity) {
		if( entity != null ) {
			super.setEntity(entity);
			
			rotation = entity.getRotation();
			zoom = 1.0f;
			height = entity.getPhysicsBody().getHeight();
			width = entity.getPhysicsBody().getWidth();
			setPosition(entity.getPosition());
			currentArchetype = entity.getEditableState().getArchetype();
			currentWorldId   = entity.getWorldId().id;
		}
	}
	
	public IGameEntity getEntity() {
		return entity;
	}
	
	public void removeFromWorld(GameMap map) {
		map.removeEntity(entity);
		entity = null;
	}

	@Override
	public void setLastMappedMousePos(Vector2f pos) {
		selectedObject.setLastMappedMousePos(pos);
	}

	@Override
	public void resetScalingDirection() {
		selectedObject.resetScalingDirection();
	}

	@Override
	public void checkScaleMarkers(Vector2f p) {
		selectedObject.checkScaleMarkers(p);
	}
	
	@Override
	public void draw(RenderTarget rt) {
		super.draw(rt);
		
		selectedObject.draw(rt);
	}
	
}
