package de.secondsystem.game01.impl.game.entities.effects;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.graphic.ParticleEmitter;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;
import de.secondsystem.game01.util.SerializationUtil;

public class GEParticleEffect implements IGameEntityEffect {

	private final ParticleEmitter pe;
	
	private final String texture;
	
	private final Vector2f offset;
	
	private final float offsetRotation;
	
	public GEParticleEffect(IGameMap map, int worldMask, Vector2f position, float rotation, String texture, int particles, float width, float height, int minTtl, int maxTtl, 
			float minXVelocity, float maxXVelocity, float minYVelocity, float maxYVelocity, float minRotationVelocity, float maxRotationVelocity, 
			Color minColor, Color maxColor, float minParticleSize, float maxParticleSize, float offsetX, float offsetY, float offsetRotation) {
		offset = new Vector2f(offsetX, offsetY);
		this.offsetRotation = offsetRotation;
		this.texture = texture;
		pe = new ParticleEmitter(texture, particles, Vector2f.add(position, offset), new Vector2f(width, height), minTtl, maxTtl, 
				new Vector2f(minXVelocity, minYVelocity), new Vector2f(maxXVelocity, maxYVelocity), minRotationVelocity, maxRotationVelocity, 
				minColor, maxColor, minParticleSize, maxParticleSize);
	}

	@Override
	public void onDestroy(IGameMap map) {
	}
	
	@Override
	public void draw(RenderTarget rt, Vector2f position, float rotation, int worldMask) {
		pe.setPosition(Vector2f.add(position, offset));
		pe.setRotation(rotation+offsetRotation);
		pe.draw(rt);
	}
	
	@Override
	public void update(long frameTimeMs) {
		pe.update(frameTimeMs);
	}

	@Override
	public Attributes serialize() {
		/*
		 *  String texture, int particles, Vector2f position, Vector2f size, int minTtl, int maxTtl, Vector2f minVelocity, 
			Vector2f maxVelocity, float minRotationVelocity, float maxRotationVelocity, Color minColor, Color maxColor, float minParticleSize, float maxParticleSize
		 */
		return new Attributes(
				new Attribute(EffectUtils.FACTORY, EffectUtils.normalizeHandlerFactory(ParticleFactory.class.getName())),
				new Attribute("x", offset.x),
				new Attribute("y", offset.y),
				new Attribute("rotation", offsetRotation),

				new Attribute("texture", texture),
				new Attribute("particles", pe.particles),
				new Attribute("width", pe.getWidth()),
				new Attribute("height", pe.getHeight()),
				new Attribute("minTtl", pe.minTtl),
				new Attribute("maxTtl", pe.maxTtl),
				new Attribute("minXVelocity", pe.minVelocity.x),
				new Attribute("maxXVelocity", pe.maxVelocity.x),
				new Attribute("minYVelocity", pe.minVelocity.y),
				new Attribute("maxYVelocity", pe.maxVelocity.y),
				new Attribute("minRotationVelocity", pe.minRotationVelocity),
				new Attribute("maxRotationVelocity", pe.maxRotationVelocity),
				new Attribute("minParticleSize", pe.minParticleSize),
				new Attribute("maxParticleSize", pe.maxParticleSize),
				new Attribute("minColor", SerializationUtil.encodeColor(pe.minColor)),
				new Attribute("maxColor", SerializationUtil.encodeColor(pe.maxColor))
		);
	}

}

class ParticleFactory implements IGameEntityEffectFactory {

	@Override
	public IGameEntityEffect create(IGameMap map, Attributes attributes, int worldMask, Vector2f position, float rotation) {
		return new GEParticleEffect(map, worldMask, position, rotation, 
				attributes.getString("texture"),
				attributes.getInteger("particles"), 
				attributes.getFloat("width"),
				attributes.getFloat("height"),
				attributes.getInteger("minTtl"),
				attributes.getInteger("maxTtl"),
				attributes.getFloat("minXVelocity"),
				attributes.getFloat("maxXVelocity"),
				attributes.getFloat("minYVelocity"),
				attributes.getFloat("maxYVelocity"),
				attributes.getFloat("minRotationVelocity"),
				attributes.getFloat("maxRotationVelocity"),
				SerializationUtil.decodeColor(attributes.getString("minColor")),
				SerializationUtil.decodeColor(attributes.getString("maxColor")),
				attributes.getFloat("minParticleSize"),
				attributes.getFloat("maxParticleSize"),
				
				attributes.getFloat("x"),
				attributes.getFloat("y"),
				attributes.getFloat("rotation") );
	}
	
}