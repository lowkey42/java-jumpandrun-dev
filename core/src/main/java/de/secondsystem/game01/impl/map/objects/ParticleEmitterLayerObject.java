package de.secondsystem.game01.impl.map.objects;

import java.util.HashMap;
import java.util.Map;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.graphic.ParticleEmitter;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.ILayerObject;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.model.IUpdateable;

public class ParticleEmitterLayerObject implements ILayerObject, IUpdateable {

	private final ParticleEmitter emitter;
	
	private final String texture;
	
	public ParticleEmitterLayerObject(String texture, int particles, float x, float y, float width, float height, int minTtl, int maxTtl, 
			float minXVelocity, float maxXVelocity, float minYVelocity, float maxYVelocity, float minParticleSize, float maxParticleSize) {
		this.texture = texture;
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
	public LayerObjectType typeUuid() {
		return LayerObjectType.PARTICLE_EMITTER;
	}

	@Override
	public Map<String, Object> getAttributes() {
		Map<String, Object> map = new HashMap<>();
		map.put("texture", texture);
		map.put("particles", emitter.particles);
		map.put("x", getPosition().x);
		map.put("y", getPosition().y);
		map.put("width", getWidth());
		map.put("height", getHeight());
		map.put("minTtl", emitter.minTtl);
		map.put("maxTtl", emitter.maxTtl);
		map.put("minXVelocity", emitter.minVelocity.x);
		map.put("maxXVelocity", emitter.maxVelocity.x);
		map.put("minYVelocity", emitter.minVelocity.y);
		map.put("maxYVelocity", emitter.maxVelocity.y);
		map.put("minParticleSize", emitter.minParticleSize);
		map.put("maxParticleSize", emitter.maxParticleSize);
		
		return map;
	}
	
	public static ParticleEmitterLayerObject create(IGameMap map, WorldId worldId, Map<String, Object> attributes) {
		try {
			return new ParticleEmitterLayerObject(
					(String)attributes.get("texture"),
					((Number)attributes.get("particles")).intValue(), 
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
