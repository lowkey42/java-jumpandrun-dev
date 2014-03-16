package de.secondsystem.game01.impl.editor.objects;

import java.util.ArrayList;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2i;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.graphic.Light;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.LayerType;
import de.secondsystem.game01.impl.map.objects.EntityLayerObject;
import de.secondsystem.game01.impl.map.physics.IHumanoidPhysicsBody;
import de.secondsystem.game01.impl.map.physics.IPhysicsBody;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IMoveable;
import de.secondsystem.game01.model.IScalable;
import de.secondsystem.game01.model.IUpdateable;
import de.secondsystem.game01.model.Attributes.Attribute;
import de.secondsystem.game01.model.IDimensioned;

public class EditorEntity extends EditorLayerObject {
	private IGameEntity entity;
	private IPhysicsBody entityBody;
	private IDrawable entityRepresentation;
	private String currentArchetype;
	private ArrayList<String> archetypes;
	private int currentArchetypeIndex = 0;
	
	public EditorEntity(IGameMap map) {
		mouseState = true;
		
		archetypes = map.getEntityManager().getArchetypes();
		currentArchetype = archetypes.get(currentArchetypeIndex);
		this.map = map;
	}
	
	public EditorEntity(Color outlineColor, float outlineThickness, Color fillColor, IGameMap map) {
		super(outlineColor, outlineThickness, fillColor, map);
	}
	

	@Override
	public void update(boolean movedObj, RenderTarget rt, int mousePosX, int mousePosY, float zoom, long frameTimeMs) {
		if( mouseState ) {
			setPosition(rt.mapPixelToCoords(new Vector2i(mousePosX, mousePosY)));
			((IMoveable) entityRepresentation).setPosition(pos);
			
			if( entityRepresentation instanceof IUpdateable )
				((IUpdateable) entityRepresentation).update(frameTimeMs);
		}
		else {
			super.update(movedObj, rt, mousePosX, mousePosY, zoom, frameTimeMs);
		}
	}
	
	private void setEntity(IGameEntity entity) {
		this.entity = entity;
		entityBody = entity.getPhysicsBody();
		entityRepresentation = entity.getRepresentation();
	}

	@Override
	public void refresh() {
		if( mouseState ) {
			if( !(entityBody instanceof IHumanoidPhysicsBody) )
				((IMoveable) entityRepresentation).setRotation(rotation);
			
			if( entityRepresentation instanceof IScalable )
				((IScalable) entityRepresentation).setDimensions(width * zoom, height * zoom);
		}
		else {
			super.refresh();
		}
	}

	@Override
	public void draw(RenderTarget renderTarget) {
		if( mouseState ) {
			entityRepresentation.draw(renderTarget);	
		}
		else {
			super.draw(renderTarget);
		}
	}

	@Override
	public void create(IGameMap map) {
		this.map = map;
		setEntity( map.getEntityManager().create(currentArchetype, new Attributes(new Attribute("x", 0), new Attribute("y", 0))) );
		map.getEntityManager().destroy(entity.uuid());
		
		rotation = 0.f;
		zoom = 1.f;
		if( entityRepresentation instanceof IDimensioned ) {
			width  = ((IDimensioned) entityRepresentation).getWidth();
			height = ((IDimensioned) entityRepresentation).getHeight();	
		}
	}

	@Override
	public void changeSelection(int offset) {
		deselect();
		
		final int typeNum = archetypes.size();
		currentArchetypeIndex += offset;
		currentArchetypeIndex = currentArchetypeIndex < 0 ? typeNum - 1 : currentArchetypeIndex % typeNum;
		
		currentArchetype = archetypes.get(currentArchetypeIndex);
		create(map);	
	}

	@Override
	public void addToMap(LayerType currentLayer) {
		Attribute width = new Attribute("width", this.width*zoom);
		Attribute height = new Attribute("height", this.height*zoom);
		Attribute rotation = new Attribute("rotation", this.rotation);
		Attribute x = new Attribute("x", pos.x);
		Attribute y = new Attribute("y", pos.y);
		Attribute worldId = new Attribute("worldId", map.getActiveWorldId().id);
		
		map.getEntityManager().create(currentArchetype, new Attributes(width, height, rotation, x, y, worldId));
	}

	@Override
	public void deselect() {
		if( mouseState ) {
			if( entity.getRepresentation() instanceof Light )
				map.getLightMap().destroyLight((Light) entity.getRepresentation());
		}
		else {
			if( layerObject != null )
				recreate();
		}
	}
	
	private void recreate() {				
		Attribute width = new Attribute("width", this.width*zoom);
		Attribute height = new Attribute("height", this.height*zoom);
		Attribute rotation = new Attribute("rotation", this.rotation);
		Attribute x = new Attribute("x", pos.x);
		Attribute y = new Attribute("y", pos.y);
		Attribute worldId = new Attribute("worldId", map.getActiveWorldId().id);
		
		if( layerObject instanceof EntityLayerObject ) {
			IGameEntity entity = ((EntityLayerObject) layerObject).getEntity();
			map.getEntityManager().destroy(entity.uuid());
			map.getEntityManager().create(entity.uuid(), entity.getEditableState().getArchetype(), new Attributes(width, height, rotation, x, y, worldId));
		}
		else
			throw new RuntimeException("layerObject is not instance of EntityLayerObject");
	}
}
