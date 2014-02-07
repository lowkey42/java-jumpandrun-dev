package de.secondsystem.game01.impl.map.objects;

import java.util.Map;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.graphic.ParticleEmitter;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;
import de.secondsystem.game01.model.IUpdateable;

public class ParticleEmitterLayerObject implements ILayerObject, IUpdateable {

	private final ParticleEmitter emitter;
	
	private final String texture;
	
	private int worldMask;
	
	public ParticleEmitterLayerObject(String texture, int particles, int worldMask, float x, float y, float width, float height, int minTtl, int maxTtl, 
			float minXVelocity, float maxXVelocity, float minYVelocity, float maxYVelocity, float minParticleSize, float maxParticleSize) {
		this.texture = texture;
		this.worldMask = worldMask;
		emitter = new ParticleEmitter(texture, particles, new Vector2f(x, y), new Vector2f(width, height), minTtl, maxTtl, 
				new Vector2f(minXVelocity, minYVelocity), new Vector2f(maxXVelocity, maxYVelocity), minParticleSize, maxParticleSize);
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
				new Attribute("minTtl", emitter.minTtl),
				new Attribute("maxTtl", emitter.maxTtl),
				new Attribute("minXVelocity", emitter.minVelocity.x),
				new Attribute("maxXVelocity", emitter.maxVelocity.x),
				new Attribute("minYVelocity", emitter.minVelocity.y),
				new Attribute("maxYVelocity", emitter.maxVelocity.y),
				new Attribute("minParticleSize", emitter.minParticleSize),
				new Attribute("maxParticleSize", emitter.maxParticleSize)
		);
	}
	
	public static ParticleEmitterLayerObject create(IGameMap map, Map<String, Object> attributes) {
		try {
			return new ParticleEmitterLayerObject(
					(String)attributes.get("texture"),
					((Number)attributes.get("particles")).intValue(), 
					((Number)attributes.get("world")).intValue(),
					((Number)attributes.get("x")).floatValue(),
					((Number)attributes.get("y")).floatValue(),
					((Number)attributes.get("width")).floatValue(),
					((Number)attributes.get("height")).floatValue(),
					((Number)attributes.get("minTtl")).intValue(),
					((Number)attributes.get("maxTtl")).intValue(),
					((Number)attributes.get("minXVelocity")).floatValue(),
					((Number)attributes.get("maxXVelocity")).floatValue(),
					((Number)attributes.get("minYVelocity")).floatValue(),
					((Number)attributes.get("maxYVelocity")).floatValue(),
					((Number)attributes.get("minParticleSize")).floatValue(),
					((Number)attributes.get("maxParticleSize")).floatValue() );
		
		} catch( ClassCastException | NullPointerException e ) {
			throw new Error( "Invalid attributes: "+attributes, e );
		}
	}

	@Override
	public void update(long frameTimeMs) {
		emitter.update(frameTimeMs);
	}

}
