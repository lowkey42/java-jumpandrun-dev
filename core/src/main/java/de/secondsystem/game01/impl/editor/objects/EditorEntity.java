package de.secondsystem.game01.impl.editor.objects;

import java.util.ArrayList;
import java.util.UUID;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2i;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.LayerType;
import de.secondsystem.game01.impl.map.objects.EntityLayerObject;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;

public class EditorEntity extends EditorLayerObject {
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
			layerObject.setPosition(pos);
			
			((EntityLayerObject) layerObject).update(frameTimeMs);
		}
		else {
			super.update(movedObj, rt, mousePosX, mousePosY, zoom, frameTimeMs);
		}
	}

	@Override
	public void refresh() {
		super.refresh();
	}

	@Override
	public void draw(RenderTarget renderTarget) {
		super.draw(renderTarget);
	}

	@Override
	public void create(IGameMap map) {
		this.map = map;
		layerObject = new EntityLayerObject(map, currentArchetype, new Attributes(new Attribute("x", 0), new Attribute("y", 0)));
		
		rotation = 0.f;
		
		width  = layerObject.getWidth();
		height = layerObject.getHeight();	
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
		addToMap(currentArchetype, UUID.randomUUID());
	}

	@Override
	public void deselect() {
		if( mouseState ) {
			((EntityLayerObject) layerObject).remove(map);
		}
		else
			if( layerObject != null ) {
				recreate();
			}
	}
	
	private void recreate() {				
		if( layerObject instanceof EntityLayerObject ) {
			((EntityLayerObject) layerObject).remove(map);
			IGameEntity entity = ((EntityLayerObject) layerObject).getEntity();
			addToMap(entity.getEditableState().getArchetype(), entity.uuid());
		}
		else
			throw new RuntimeException("layerObject is not instance of EntityLayerObject");
	}
	
	private void addToMap(String archetype, UUID uuid) {
		Attribute width = new Attribute("width", this.width);
		Attribute height = new Attribute("height", this.height);
		Attribute rotation = new Attribute("rotation", this.rotation);
		Attribute x = new Attribute("x", pos.x);
		Attribute y = new Attribute("y", pos.y);
		Attribute worldId = new Attribute("worldId", map.getActiveWorldId().id);
		
		map.getEntityManager().create(uuid, archetype, new Attributes(width, height, rotation, x, y, worldId));
	}
}
