package de.secondsystem.game01.impl.game.entities.effects;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.graphic.ParticleEmitter;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;
import de.secondsystem.game01.model.Attributes.AttributeIf;
import de.secondsystem.game01.util.SerializationUtil;

public class GEParticleEffect implements IGameEntityEffect {

	private final ParticleEmitter pe;
	
	private final String texture;
	
	private final Vector2f offset;
	
	private final float offsetRotation;
	
	public GEParticleEffect(IGameMap map, int worldMask, Vector2f position, float rotation, String texture, int particles, float width, float height, Float radius, int minTtl, int maxTtl, 
			float minXVelocity, float maxXVelocity, float minYVelocity, float maxYVelocity, float minRotationVelocity, float maxRotationVelocity, float minAngularVelocity, float maxAngularVelocity, 
			Color minColor, Color maxColor, float minParticleSize, float maxParticleSize, float offsetX, float offsetY, float offsetRotation) {
		offset = new Vector2f(offsetX, offsetY);
		this.offsetRotation = offsetRotation;
		this.texture = texture;
		pe = new ParticleEmitter(texture, particles, Vector2f.add(position, offset), new Vector2f(width, height), radius, minTtl, maxTtl, 
				new Vector2f(minXVelocity, minYVelocity), new Vector2f(maxXVelocity, maxYVelocity), minRotationVelocity, maxRotationVelocity, 
				minAngularVelocity, maxAngularVelocity, minColor, maxColor, minParticleSize, maxParticleSize);
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
				new AttributeIf(pe.radius!=null, "radius", pe.radius),
				new Attribute("minTtl", pe.minTtl),
				new Attribute("maxTtl", pe.maxTtl),
				new AttributeIf(pe.minVelocity.x!=0, "minXVelocity", pe.minVelocity.x),
				new AttributeIf(pe.maxVelocity.x!=0, "maxXVelocity", pe.maxVelocity.x),
				new AttributeIf(pe.minVelocity.y!=0, "minYVelocity", pe.minVelocity.y),
				new AttributeIf(pe.maxVelocity.y!=0, "maxYVelocity", pe.maxVelocity.y),
				new AttributeIf(pe.minRotationVelocity!=0, "minRotationVelocity", pe.minRotationVelocity),
				new AttributeIf(pe.maxRotationVelocity!=0, "maxRotationVelocity", pe.maxRotationVelocity),
				new AttributeIf(pe.minAngularVelocity!=0, "minAngularVelocity", pe.minAngularVelocity),
				new AttributeIf(pe.maxAngularVelocity!=0, "maxAngularVelocity", pe.maxAngularVelocity),
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
				attributes.getFloat("radius"),
				attributes.getInteger("minTtl"),
				attributes.getInteger("maxTtl"),
				attributes.getFloat("minXVelocity", 0),
				attributes.getFloat("maxXVelocity", 0),
				attributes.getFloat("minYVelocity", 0),
				attributes.getFloat("maxYVelocity", 0),
				attributes.getFloat("minRotationVelocity", 0),
				attributes.getFloat("maxRotationVelocity", 0),
				attributes.getFloat("minAngularVelocity", 0),
				attributes.getFloat("maxAngularVelocity", 0),
				SerializationUtil.decodeColor(attributes.getString("minColor")),
				SerializationUtil.decodeColor(attributes.getString("maxColor")),
				attributes.getFloat("minParticleSize"),
				attributes.getFloat("maxParticleSize"),
				
				attributes.getFloat("x", 0),
				attributes.getFloat("y", 0),
				attributes.getFloat("rotation", 0) );
	}
	
}