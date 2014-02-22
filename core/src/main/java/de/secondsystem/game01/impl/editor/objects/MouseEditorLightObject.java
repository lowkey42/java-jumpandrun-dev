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

public class MouseEditorLightObject extends AbstractEditorObject implements IMouseEditorObject {
	private LightLayerObject light;
	private IGameMap map;
	
	@Override
	public void refresh() {
		light.setRotation(rotation);
		light.setDimensions(width*zoom, height*zoom);
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
		
		light = LightLayerObject.create(map, new Attributes(radius, rotation, x, y, world, color, sizeDegree));
		
		this.rotation = light.getRotation();
		
		zoom = 1.f;
		width  = light.getWidth();
		height = light.getHeight();
	}

	@Override
	public void changeSelection(int offset) {
		//
	}

	@Override
	public void addToMap(LayerType currentLayer) {
//		Attribute radius = new Attribute("radius", this.height*zoom / 2.f);
//		Attribute rotation = new Attribute("rotation", this.rotation);
//		Attribute x = new Attribute("x", pos.x);
//		Attribute y = new Attribute("y", pos.y);
//		Attribute world = new Attribute("world", map.getActiveWorldId().id);
//		Attribute color = new Attribute("color", new Color(200, 180, 150));
//		Attribute sizeDegree = new Attribute("sizeDegree", 0.f);
	}

	@Override
	public void update(boolean movedObj, RenderTarget rt, int mousePosX,
			int mousePosY, float zoom, long frameTimeMs) {
		setPosition(rt.mapPixelToCoords(new Vector2i(mousePosX, mousePosY)));
		light.setPosition(pos);
	}

}
