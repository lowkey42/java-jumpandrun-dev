package de.secondsystem.game01.impl.map.objects;

import java.util.Map;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.graphic.ParticleEmitter;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;
import de.secondsystem.game01.model.Attributes.AttributeIf;
import de.secondsystem.game01.model.IUpdateable;
import de.secondsystem.game01.util.SerializationUtil;

public class ParticleEmitterLayerObject implements ILayerObject, IUpdateable {

	private final ParticleEmitter emitter;
	
	private final String texture;
	
	private int worldMask;
	
	public ParticleEmitterLayerObject(String texture, int particles, int worldMask, float x, float y, float width, float height, Float radius, int minTtl, int maxTtl, 
			float minXVelocity, float maxXVelocity, float minYVelocity, float maxYVelocity, float minRotationVelocity, float maxRotationVelocity, float minAngularVelocity, float maxAngularVelocity, 
			Color minColor, Color maxColor, float minParticleSize, float maxParticleSize ) {
		this.texture = texture;
		this.worldMask = worldMask;
		emitter = new ParticleEmitter(texture, particles, new Vector2f(x, y), new Vector2f(width, height), radius, minTtl, maxTtl, 
				new Vector2f(minXVelocity, minYVelocity), new Vector2f(maxXVelocity, maxYVelocity), minRotationVelocity, 
				maxRotationVelocity, minAngularVelocity, maxAngularVelocity, minColor, maxColor, minParticleSize, maxParticleSize);
	}

	@Override
	public void draw(RenderTarget renderTarget) {
		emitter.draw(renderTarget);
	}

	@Override
	public boolean inside(Vector2f point) {
		return emitter.inside(point);
	}

	@Override
	public void setPosition(Vector2f pos) {
		emitter.setPosition(pos);
	}

	@Override
	public void setRotation(float degree) {
		emitter.setRotation(degree);
	}

	@Override
	public float getRotation() {
		return emitter.getRotation();
	}

	@Override
	public Vector2f getPosition() {
		return emitter.getPosition();
	}

	@Override
	public float getHeight() {
		return emitter.getHeight();
	}

	@Override
	public float getWidth() {
		return emitter.getWidth();
	}

	@Override
	public void setDimensions(float width, float height) {
		emitter.setDimensions(width, height);
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
	}

	@Override
	public LayerObjectType typeUuid() {
		return LayerObjectType.PARTICLE_EMITTER;
	}

	@Override
	public Attributes serialize() {
		return new Attributes(
				new Attribute("$type", typeUuid().shortId),
				new Attribute("world", worldMask),
				new Attribute("texture", texture),
				new Attribute("particles", emitter.particles),
				new Attribute("x", getPosition().x),
				new Attribute("y", getPosition().y),
				new Attribute("rotation", getRotation()),
				new Attribute("width", getWidth()),
				new Attribute("height", getHeight()),
				new AttributeIf(emitter.radius!=null, "radius", emitter.radius),
				new Attribute("minTtl", emitter.minTtl),
				new Attribute("maxTtl", emitter.maxTtl),
				new Attribute("minXVelocity", emitter.minVelocity.x),
				new Attribute("maxXVelocity", emitter.maxVelocity.x),
				new Attribute("minYVelocity", emitter.minVelocity.y),
				new Attribute("maxYVelocity", emitter.maxVelocity.y),
				new Attribute("minRotationVelocity", emitter.minRotationVelocity),
				new Attribute("maxRotationVelocity", emitter.maxRotationVelocity),
				new Attribute("minAngularVelocity", emitter.minAngularVelocity),
				new Attribute("maxAngularVelocity", emitter.maxAngularVelocity),
				new Attribute("minParticleSize", emitter.minParticleSize),
				new Attribute("maxParticleSize", emitter.maxParticleSize),
				new Attribute("minColor", SerializationUtil.encodeColor(emitter.minColor)),
				new Attribute("maxColor", SerializationUtil.encodeColor(emitter.maxColor))
		);
	}
	
	public static ParticleEmitterLayerObject create(IGameMap map, Map<String, Object> args) {
		try {
			final Attributes attributes = new Attributes(args);
			
			return new ParticleEmitterLayerObject(
					attributes.getString("texture"),
					attributes.getInteger("particles"), 
					attributes.getInteger("world"),
					attributes.getFloat("x"),
					attributes.getFloat("y"),
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
					attributes.getFloat("maxParticleSize") );
		
		} catch( ClassCastException | NullPointerException e ) {
			throw new Error( "Invalid attributes: "+args, e );
		}
	}

	@Override
	public void update(long frameTimeMs) {
		emitter.update(frameTimeMs);
	}

}
