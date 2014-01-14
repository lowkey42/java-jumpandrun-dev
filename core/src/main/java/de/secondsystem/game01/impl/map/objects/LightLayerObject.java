package de.secondsystem.game01.impl.map.objects;

import java.util.HashMap;
import java.util.Map;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.graphic.Light;
import de.secondsystem.game01.impl.graphic.LightMap;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.util.SerializationUtil;

public class LightLayerObject implements ILayerObject {

	private final Light light;
	
	public LightLayerObject(LightMap lightMap, float x, float y, float rotation, float radius, float sizeDegree, Color color) {
		light = new Light(lightMap, new Vector2f(x, y), color, radius, sizeDegree, rotation);
	}

	@Override
	public void draw(RenderTarget renderTarget) {
	}

	void drawLight(LightMap lightMap) {
		lightMap.drawLight(light);
	}
	
	@Override
	public boolean inside(Vector2f point) {
		return light.inside(point);
	}

	@Override
	public void setPosition(Vector2f pos) {
		light.setPosition(pos);
	}

	@Override
	public void setRotation(float degree) {
		light.setRotation(degree);
	}

	@Override
	public float getRotation() {
		return light.getRotation();
	}

	@Override
	public Vector2f getPosition() {
		return light.getPosition();
	}

	@Override
	public float getHeight() {
		return light.getCenterDegree();
	}

	@Override
	public float getWidth() {
		return light.getRadius()*2;
	}

	@Override
	public void setDimensions(float width, float height) {
		light.setRadius(width/2);
		light.setCenterDegree(height);
	}

	@Override
	public LayerObjectType typeUuid() {
		return LayerObjectType.LIGHT;
	}

	@Override
	public Map<String, Object> getAttributes() {
		Map<String, Object> map = new HashMap<>();
		map.put("x", getPosition().x);
		map.put("y", getPosition().y);
		map.put("rotation", light.getCenterDegree());
		map.put("radius", light.getRadius());
		map.put("sizeDegree", light.getDegree());
		map.put("color", SerializationUtil.encodeColor(light.getColor()));
		
		return map;
	}

	public static LightLayerObject create(IGameMap map, WorldId worldId, Map<String, Object> attributes) {
		try {
			return new LightLayerObject(
					map.getLightMap(),
					((Number)attributes.get("x")).floatValue(),
					((Number)attributes.get("y")).floatValue(),
					((Number)attributes.get("rotation")).floatValue(),
					((Number)attributes.get("radius")).floatValue(),
					((Number)attributes.get("sizeDegree")).floatValue(),
					SerializationUtil.decodeColor((String) attributes.get("color")) );
		
		} catch( ClassCastException | NullPointerException e ) {
			throw new Error( "Invalid attributes: "+attributes, e );
		}
	}
}
