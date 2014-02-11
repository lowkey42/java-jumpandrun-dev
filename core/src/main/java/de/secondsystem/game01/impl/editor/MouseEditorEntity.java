package de.secondsystem.game01.impl.editor;

import java.util.ArrayList;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2i;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.graphic.ISpriteWrapper;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.physics.IHumanoidPhysicsBody;
import de.secondsystem.game01.impl.map.physics.IPhysicsBody;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IMoveable;
import de.secondsystem.game01.model.IUpdateable;
import de.secondsystem.game01.model.Attributes.Attribute;
import de.secondsystem.game01.model.IDimensioned;

public class MouseEditorEntity extends AbstractEditorObject {
	protected IGameEntity entity;
	protected IPhysicsBody entityBody;
	protected IDrawable entityRepresentation;
	protected String currentArchetype;
	private final ArrayList<String> archetypes;
	private int currentArchetypeIndex = 0;
	
	public MouseEditorEntity(IGameMap map) {
		archetypes = map.getEntityManager().getArchetypes();
		currentArchetype = archetypes.get(currentArchetypeIndex);
	}
	
	public void createEntity(IGameMap map) {
		setEntity( map.getEntityManager().create(currentArchetype, new Attributes(new Attribute("x", 0), new Attribute("y", 0))) );
		map.getEntityManager().destroy(entity.uuid());
		
		rotation = 0.f;
		zoom = 1.f;
		width  = ((IDimensioned) entityRepresentation).getWidth();
		height = ((IDimensioned) entityRepresentation).getHeight();		
	}
	
	public void addToWorld(IGameMap map) {
		Attribute width = new Attribute("width", this.width*zoom);
		Attribute height = new Attribute("height", this.height*zoom);
		Attribute rotation = new Attribute("rotation", this.rotation);
		Attribute x = new Attribute("x", pos.x);
		Attribute y = new Attribute("y", pos.y);
		Attribute worldId = new Attribute("worldId", map.getActiveWorldId().id);
		
		map.getEntityManager().create(currentArchetype, new Attributes(width, height, rotation, x, y, worldId));
	}	
	
	public void changeEntity(IGameMap map, int offset) {
		final int typeNum = archetypes.size();
		currentArchetypeIndex += offset;
		currentArchetypeIndex = currentArchetypeIndex < 0 ? typeNum - 1 : currentArchetypeIndex % typeNum;
		
		currentArchetype = archetypes.get(currentArchetypeIndex);
		createEntity(map);
	}

	public void update(boolean movedObj, RenderTarget rt, int mousePosX, int mousePosY, float zoom, long frameTimeMs) {
		setPosition(rt.mapPixelToCoords(new Vector2i(mousePosX, mousePosY)));
		((IMoveable) entityRepresentation).setPosition(pos);
		
		if( entityRepresentation instanceof IUpdateable )
			((IUpdateable) entityRepresentation).update(frameTimeMs);
	}
	
	public void setEntity(IGameEntity entity) {
		this.entity = entity;
		entityBody = entity.getPhysicsBody();
		entityRepresentation = entity.getRepresentation();
	}

	@Override
	public void refresh() {
		if( !(entityBody instanceof IHumanoidPhysicsBody) )
			((IMoveable) entityRepresentation).setRotation(rotation);
		
		if( entityRepresentation instanceof ISpriteWrapper ) /*TODO*/
			((ISpriteWrapper) entityRepresentation).setDimensions(width * zoom, height * zoom);
	}

	@Override
	public void draw(RenderTarget renderTarget) {
		entityRepresentation.draw(renderTarget);
		
	}
}
