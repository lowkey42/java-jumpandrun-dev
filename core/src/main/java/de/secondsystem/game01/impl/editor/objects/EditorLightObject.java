package de.secondsystem.game01.impl.editor.objects;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2i;

import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.impl.map.LayerType;
import de.secondsystem.game01.impl.map.objects.LightLayerObject;

public class EditorLightObject extends EditorLayerObject {
	private LightLayerObject lightLayerObject;
	private float radius;
	private float degree;
	private float lastRadius;
	private float lastDegree;
	
	public EditorLightObject(Color outlineColor, float outlineThickness, Color fillColor, IGameMap map) {
		super(outlineColor, outlineThickness, fillColor, map);
	}
	
	@Override
	public void setLayerObject(ILayerObject layerObject) {
		super.setLayerObject(layerObject);
		lightLayerObject = (LightLayerObject) layerObject;
		
		radius = lightLayerObject.getRadius();
		degree = lightLayerObject.getDegree();
		lastRadius = radius;
		lastDegree = degree;
		
		width  = 50.f;
		height = 50.f;
	}
	
	public EditorLightObject() {
		mouseState = true;
	}
	
	public float getRadius() {
		return radius;
	}
	
	public void setRadius(float radius) {
		this.radius = radius;
	}
	
	public float getDegree() {
		return degree;
	}
	
	public void setDegree(float degree) {
		this.degree = degree;
	}
	
	@Override
	public void refresh() {
		lightLayerObject.setRotation(rotation);
		lightLayerObject.setRadius(radius);
		lightLayerObject.setDegree(degree);
	}

	@Override
	public void draw(RenderTarget renderTarget) {
		if( !mouseState )
			super.draw(renderTarget);
	}
	
	@Override
	public void create(IGameMap map) {
		this.map = map;	
		
		// temporary
//		Attribute radius = new Attribute("radius", 40.f);
//		Attribute rotation = new Attribute("rotation", 0.f);
//		Attribute x = new Attribute("x", 0.f);
//		Attribute y = new Attribute("y", 0.f);
//		Attribute world = new Attribute("world", map.getActiveWorldId().id);
//		Attribute color = new Attribute("color", SerializationUtil.encodeColor(new Color(100, 180, 150)) );
//		Attribute sizeDegree = new Attribute("sizeDegree", 360.f);
		
		lightLayerObject = null;// LightLayerObject.create(map, new Attributes(radius, rotation, x, y, world, color, sizeDegree));
		
		this.rotation = lightLayerObject.getRotation();
		
		width  = 20.f;
		height = 20.f;
		
		this.radius = lightLayerObject.getRadius();
		this.degree = lightLayerObject.getDegree();
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
		if( !mouseState ) {
			super.update(movedObj, rt, mousePosX, mousePosY, zoom, frameTimeMs);
		}
		else {
			setPosition(rt.mapPixelToCoords(new Vector2i(mousePosX, mousePosY)));
			lightLayerObject.setPosition(pos);
		}
	}

	@Override
	public void deselect() {
 		if( mouseState )
			map.getLightMap().destroyLight(lightLayerObject.getLight());	
	}
	
	@Override
	protected void mouseScaling() {
		if( scalingX != 0 ) {
			Vector3 v;
			if( scalingX == -1 )
				v = mouseScaling(scaleMarkers[0], 3, 0, scalingX, lastPos, lastRadius, true); 
			else
				v = mouseScaling(scaleMarkers[2], 1, 2, scalingX, lastPos, lastRadius, true); 
			
			setRadius( v.z );
		}
		else 
			lastRadius  = radius;
		
		if( scalingY != 0 ) {	
			Vector3 v;
			if( scalingY == -1 ) 
				v = mouseScaling(scaleMarkers[1], 0, 1, scalingY, lastPos, lastDegree, false); 
			else 
				v = mouseScaling(scaleMarkers[3], 2, 3, scalingY, lastPos, lastDegree, false); 
			
			setDegree( v.z );
		}
		else 
			lastDegree = degree;
		
		if( scalingX == 0 && scalingY == 0 )
			lastPos = pos;
		else
			scaling = true;
		
		if( radius < 5.f )
			setRadius( 5.f );
		
		if( degree < 5.f )
			setDegree( 5.f );
		
		if( degree > 360.f )
			setDegree( 360.f );
	}
	

}
