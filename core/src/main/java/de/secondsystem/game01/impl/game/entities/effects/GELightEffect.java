package de.secondsystem.game01.impl.game.entities.effects;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.graphic.Light;
import de.secondsystem.game01.impl.graphic.LightMap;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;
import de.secondsystem.game01.util.SerializationUtil;

public class GELightEffect implements IGameEntityEffect {

	private final LightMap lm;
	
	private final int worldMask;
	
	private final Light light;
	
	private final Vector2f offset;
	
	private final float offsetRotation;
	
	public GELightEffect(IGameMap map, int worldMask, Vector2f position, float rotation, Color color, float radius, float degree, float offsetX, float offsetY, float offsetRotation) {
		offset = new Vector2f(offsetX, offsetY);
		this.offsetRotation = offsetRotation;
		light = map.getLightMap().createLight(worldMask, Vector2f.add(position, offset), color, radius, degree, rotation+offsetRotation);
		
		lm = map.getLightMap();
		this.worldMask = worldMask;
	}

	@Override
	public void onDestroy(IGameMap map) {
		map.getLightMap().destroyLight(light);
	}
	
	@Override
	public void draw(RenderTarget rt, Vector2f position, float rotation, int worldMask) {
		light.setPosition(Vector2f.add(position, offset));
		light.setRotation(rotation+offsetRotation);
		if( this.worldMask!=worldMask )
			lm.moveLight(light, worldMask);
	}
	
	@Override
	public void update(long frameTimeMs) {
	}

	@Override
	public Attributes serialize() {
		return new Attributes(
				new Attribute(EffectUtils.FACTORY, EffectUtils.normalizeHandlerFactory(LightFactory.class.getName())),
				new Attribute("color", SerializationUtil.encodeColor(light.getColor())),
				new Attribute("radius", light.getRadius()),
				new Attribute("degree", light.getDegree()),
				new Attribute("x", offset.x),
				new Attribute("y", offset.y),
				new Attribute("rotation", offsetRotation)
		);
	}

}

class LightFactory implements IGameEntityEffectFactory {

	@Override
	public IGameEntityEffect create(IGameMap map, Attributes attributes, int worldMask, Vector2f position, float rotation) {
		return new GELightEffect(map, worldMask, position, rotation, 
				SerializationUtil.decodeColor(attributes.getString("color")),
				attributes.getFloat("radius"),
				attributes.getFloat("degree"),
				attributes.getFloat("x"),
				attributes.getFloat("y"),
				attributes.getFloat("rotation") );
	}
	
}