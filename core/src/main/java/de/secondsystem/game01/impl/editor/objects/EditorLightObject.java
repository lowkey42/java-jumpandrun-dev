package de.secondsystem.game01.impl.editor.objects;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2i;

import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.LayerType;
import de.secondsystem.game01.impl.map.objects.LightLayerObject;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;
import de.secondsystem.game01.util.SerializationUtil;

public class EditorLightObject extends EditorLayerObject {
	private LightLayerObject lightLayerObject;
	private IGameMap map;
	
	public EditorLightObject() {
		mouseState = true;
	}
	
	@Override
	public void refresh() {
		lightLayerObject.setRotation(rotation);
		lightLayerObject.setDimensions(width*zoom, height*zoom);
	}

	@Override
	public void draw(RenderTarget renderTarget) {
	}
	
	@Override
	public void create(IGameMap map) {
		this.map = map;	

		Attribute radius = new Attribute("radius", 40.f);
		Attribute rotation = new Attribute("rotation", 0.f);
		Attribute x = new Attribute("x", 0.f);
		Attribute y = new Attribute("y", 0.f);
		Attribute world = new Attribute("world", map.getActiveWorldId().id);
		Attribute color = new Attribute("color", SerializationUtil.encodeColor(new Color(100, 180, 150)) );
		Attribute sizeDegree = new Attribute("sizeDegree", 360.f);
		
		lightLayerObject = LightLayerObject.create(map, new Attributes(radius, rotation, x, y, world, color, sizeDegree));
		
		this.rotation = lightLayerObject.getRotation();
		
		zoom = 1.f;
		width  = lightLayerObject.getWidth();
		height = lightLayerObject.getHeight();
	}

	@Override
	public void changeSelection(int offset) {

	}

	@Override
	public void addToMap(LayerType currentLayer) {
		map.addNode(currentLayer, lightLayerObject);
		create(map);	
	}

	@Override
	public void update(boolean movedObj, RenderTarget rt, int mousePosX, int mousePosY, float zoom, long frameTimeMs) {
		setPosition(rt.mapPixelToCoords(new Vector2i(mousePosX, mousePosY)));
		lightLayerObject.setPosition(pos);
	}

	@Override
	public void deselect() {
		map.getLightMap().destroyLight(lightLayerObject.getLight());	
	}

}
