package de.secondsystem.game01.impl.map.objects;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.graphic.Light;
import de.secondsystem.game01.impl.graphic.LightMap;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.impl.map.LayerType;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;
import de.secondsystem.game01.util.SerializationUtil;

public class LightLayerObject implements ILayerObject {

	private final LightMap lightMap;
	
	private final Light light;
	
	private int worldMask;
	
	public LightLayerObject(LightMap lightMap, int worldMask, float x, float y, float rotation, float radius, float sizeDegree, Color color) {
		light = lightMap.createLight(worldMask, new Vector2f(x, y), color, radius, sizeDegree, rotation);
		this.worldMask = worldMask;
		this.lightMap = lightMap;
	}
	
	@Override
	public void setLayerType(LayerType layerType) {
	}
	@Override
	public LayerType getLayerType() {
		return LayerType.LIGHTS;
	}
	
	public Light getLight() {
		return light;
	}
	
	public void setDegree(float degree) {
		light.setDegree(degree);
	}
	
	public void setRadius(float radius) {
		light.setRadius(radius);
	}
	
	public float getDegree() {
		return light.getDegree();
	}
	
	public float getRadius() {
		return light.getRadius();
	}

	@Override
	public void draw(RenderTarget renderTarget, WorldId worldId) {
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
		return light.getHeight();
	}

	@Override
	public float getWidth() {
		return light.getWidth();
	}

	@Override
	public void setDimensions(float width, float height) {
		light.setDimensions(width, height);
	}

	@Override
	public boolean isInWorld(WorldId worldId) {
		return (worldMask & worldId.id)!=0;
	}

	@Override
	public void setWorld(WorldId worldId, boolean exists) {
		if( exists )
			worldMask|=worldId.id;
		else
			worldMask&=~worldId.id;
		
		if( worldMask==0 )
			lightMap.destroyLight(light);
	}

	@Override
	public void flipHoriz() {
	}

	@Override
	public void setFlipHoriz(boolean flip) {
	}

	@Override
	public boolean isFlippedHoriz() {
		return false;
	}

	@Override
	public void flipVert() {
	}

	@Override
	public void setFlipVert(boolean flip) {
	}

	@Override
	public boolean isFlippedVert() {
		return false;
	}

	@Override
	public LayerObjectType typeUuid() {
		return LayerObjectType.LIGHT;
	}

	@Override
	public Attributes serialize() {
		return new Attributes(
				new Attribute("$type", typeUuid().shortId),
				new Attribute("world", worldMask),
				new Attribute("color", SerializationUtil.encodeColor(light.getColor())),
				new Attribute("x", getPosition().x),
				new Attribute("y", getPosition().y),
				new Attribute("rotation", getRotation()),
				new Attribute("radius", light.getRadius()),
				new Attribute("sizeDegree", light.getDegree())
		);
	}
	static final class Factory implements ILayerObjectFactory {
		@Override
		public LightLayerObject create(IGameMap map, Attributes attributes) {
			try {
				return new LightLayerObject(
						map.getLightMap(),
						attributes.getInteger("world"),
						attributes.getFloat("x"),
						attributes.getFloat("y"),
						attributes.getFloat("rotation"),
						attributes.getFloat("radius"),
						attributes.getFloat("sizeDegree"),
						SerializationUtil.decodeColor(attributes.getString("color")) );
			
			} catch( ClassCastException | NullPointerException e ) {
				throw new Error( "Invalid attributes: "+attributes, e );
			}
		}
	}
}
