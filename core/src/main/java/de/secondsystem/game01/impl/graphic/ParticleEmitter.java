package de.secondsystem.game01.impl.graphic;

import java.io.IOException;
import java.util.Random;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstTexture;
import org.jsfml.graphics.PrimitiveType;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Vertex;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.ResourceManager;
import de.secondsystem.game01.model.GameException;
import de.secondsystem.game01.model.IDimensioned;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IInsideCheck;
import de.secondsystem.game01.model.IMoveable;
import de.secondsystem.game01.model.IUpdateable;

public class ParticleEmitter implements IDrawable, IMoveable, IDimensioned, IUpdateable, IInsideCheck {
	
	private static final Random RAND = new Random();

	private static final class Particle {
		final Vector2f velocity;
		int ttd;
		Particle(Vector2f velocity, int ttd) {
			this.velocity = velocity;
			this.ttd = ttd;
		}
	}
	
	/**
	 * Time passed for this ParticleEmitter (update-Calls)
	 * Uses int instead of long, because thats enough for about 1 month of play.
	 * The first one who triggers a bug by playing the same level for >24 days wins a crate of beer. 
	 */
	private int gameTime;
	
	private final Particle particleData[];
	
	private final Vertex vertexData[];
	
	public final int particles;
	
	public final int minTtl;
	public final int maxTtl;
	
	public final Vector2f minVelocity;
	public final Vector2f maxVelocity;
	
	private Vector2f centerPosition;
	private Vector2f halfSize;
	
	public final float minParticleSize;
	public final float maxParticleSize;
	
	private final ConstTexture texture;
	
	public ParticleEmitter(String texture, int particles, Vector2f centerPosition, Vector2f size, int minTtl, int maxTtl, Vector2f minVelocity, 
			Vector2f maxVelocity, float minParticleSize, float maxParticleSize) {
		particleData = new Particle[particles];
		vertexData = new Vertex[particles*4];

		this.particles = particles;
		this.minTtl = minTtl;
		this.maxTtl = maxTtl;
		this.minVelocity = minVelocity;
		this.maxVelocity = maxVelocity;
		this.centerPosition = centerPosition;
		this.halfSize = Vector2f.div(size, 2);
		this.minParticleSize = minParticleSize;
		this.maxParticleSize = maxParticleSize;
		try {
			this.texture = ResourceManager.texture.get(texture);
		} catch (IOException e) {
			throw new GameException(e);
		}
		
		for(int i=0; i<particles; ++i)
			resetParticle(i);
	}

	@Override
	public float getHeight() {
		return halfSize.x*2;
	}

	@Override
	public float getWidth() {
		return halfSize.x*2;
	}
	public void setDimensions(float width, float height) {
		halfSize = new Vector2f(width/2, height/2);
	}

	@Override
	public void setPosition(Vector2f pos) {
		centerPosition = pos;
	}

	@Override
	public void setRotation(float degree) {
	}

	@Override
	public float getRotation() {
		return 0;
	}

	@Override
	public Vector2f getPosition() {
		return centerPosition;
	}

	
	
	@Override
	public void draw(RenderTarget renderTarget) {
		renderTarget.draw(vertexData, PrimitiveType.QUADS, new RenderStates(texture));
	}

	@Override
	public void update(long frameTimeMs) {
		gameTime+=frameTimeMs;
		
		for( int i=0; i<vertexData.length; ++i ) {
			if( i%4==0 && gameTime>=particleData[i/4].ttd ) {
				resetParticle(i/4);
				i=(i/4+ 1)*4;
				
			} else {
				vertexData[i] = new Vertex(
						Vector2f.add(vertexData[i].position, Vector2f.mul(particleData[i/4].velocity, frameTimeMs/1000.f)),
						vertexData[i].color,
						vertexData[i].texCoords
				);
			}
		}
	}

	private final void resetParticle(int i) {
		particleData[i] = new Particle(random(minVelocity, maxVelocity), gameTime+random(minTtl, maxTtl));
		
		Color color = new Color(255, 255, 255, 255);
		Vector2f pos = random(Vector2f.sub(centerPosition, halfSize), Vector2f.add(centerPosition, halfSize));
		float h = random(minParticleSize, maxParticleSize);
		float w = h; 
				
		vertexData[i*4 +0] = new Vertex(new Vector2f(pos.x,   pos.y  ), color,	new Vector2f(0,0));
		vertexData[i*4 +1] = new Vertex(new Vector2f(pos.x,   pos.y+h), color,	new Vector2f(texture.getSize().x,0));
		vertexData[i*4 +2] = new Vertex(new Vector2f(pos.x+w, pos.y+h), color,	new Vector2f(texture.getSize().x,texture.getSize().y));
		vertexData[i*4 +3] = new Vertex(new Vector2f(pos.x+w, pos.y  ), color,	new Vector2f(0,texture.getSize().y));
	}
	
	
	private static int random(int min, int max) {
		return RAND.nextInt(max-min) + min;
	}
	private static float random(float min, float max) {
		return RAND.nextFloat()*(max-min) + min;
	}
	private static Vector2f random(Vector2f min, Vector2f max) {
		return new Vector2f(random(min.x, max.x), random(min.y, max.y));
	}

	@Override
	public boolean inside(Vector2f point) {
		return point.x>=centerPosition.x-halfSize.x && point.x<=centerPosition.x-halfSize.x
				&& point.y>=centerPosition.y-halfSize.y && point.y<=centerPosition.y-halfSize.y;
	}
}
