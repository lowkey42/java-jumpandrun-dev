package de.secondsystem.game01.impl.editor;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.graphic.ISpriteWrapper;
import de.secondsystem.game01.impl.map.physics.IHumanoidPhysicsBody;
import de.secondsystem.game01.impl.map.physics.IPhysicsBody;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IMoveable;

public abstract class AbstractEditorEntity extends AbstractEditorObject {
	protected IGameEntity entity;
	protected IPhysicsBody entityBody;
	protected IDrawable entityRepresentation;
	protected String currentArchetype;
	
	public void setEntity(IGameEntity entity) {
		this.entity = entity;
		entityBody = entity.getPhysicsBody();
		entityRepresentation = entity.getRepresentation();
	}
	
	@Override
	public void draw(RenderTarget renderTarget) {
		entityRepresentation.draw(renderTarget);
	}

	@Override
	public boolean isPointInside(Vector2f p) {
		return entity != null && ((ISpriteWrapper) entityRepresentation).inside(p);
	}
	
	@Override
	public void refresh() {
		if( !(entityBody instanceof IHumanoidPhysicsBody) )
			((IMoveable) entityRepresentation).setRotation(rotation);
		
		((ISpriteWrapper) entityRepresentation).setDimensions(width * zoom, height * zoom);
	}
	
}
