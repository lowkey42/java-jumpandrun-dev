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
import de.secondsystem.game01.model.IScalable;
import de.secondsystem.game01.model.IUpdateable;

public class ParticleEmitter implements IDrawable, IMoveable, IDimensioned, IUpdateable, IInsideCheck, IScalable {
	
	private static final Random RAND = new Random();

	private static final class Particle {
		final Vector2f velocity;
		final float rotationVelocity;
		final float width;
		final float height;
		float rotation;
		Vector2f pos;
		int ttd;
		Particle(Vector2f velocity, float rotationVelocity, float width, float height, Vector2f pos, int ttd) {
			this.velocity = velocity;
			this.rotationVelocity = rotationVelocity;
			this.height = height;
			this.width = width;
			this.pos = pos;
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
	
	public final float minRotationVelocity;
	public final float maxRotationVelocity;
	
	private Vector2f centerPosition;
	private Vector2f halfSize;
	
	public final float minParticleSize;
	public final float maxParticleSize;
	
	public final Color minColor;
	public final Color maxColor;
	
	private final ConstTexture texture;
	
	public ParticleEmitter(String texture, int particles, Vector2f centerPosition, Vector2f size, int minTtl, int maxTtl, Vector2f minVelocity, 
			Vector2f maxVelocity, float minRotationVelocity, float maxRotationVelocity, Color minColor, Color maxColor, float minParticleSize, float maxParticleSize) {
		particleData = new Particle[particles];
		vertexData = new Vertex[particles*4];

		this.particles = particles;
		this.minTtl = minTtl;
		this.maxTtl = maxTtl;
		this.minVelocity = minVelocity;
		this.maxVelocity = maxVelocity;
		this.minRotationVelocity = minRotationVelocity;
		this.maxRotationVelocity = maxRotationVelocity;
		this.centerPosition = centerPosition;
		this.halfSize = Vector2f.div(size, 2);
		this.minParticleSize = minParticleSize;
		this.maxParticleSize = maxParticleSize;
		this.minColor = minColor;
		this.maxColor = maxColor;
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
	
	@Override
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
		
		for( int i=0; i<vertexData.length/4; ++i ) {
			if( gameTime>=particleData[i].ttd ) {
				resetParticle(i);
				
			} else {
				particleData[i].rotation+=particleData[i].rotationVelocity*(frameTimeMs/1000.f);
				Vector2f pos = particleData[i].pos=Vector2f.add(particleData[i].pos, Vector2f.mul(particleData[i].velocity, frameTimeMs/1000.f));
				
				final float cosA = (float) Math.cos(particleData[i].rotation);
				final float sinA = (float) Math.sin(particleData[i].rotation);
				float h = particleData[i].height;
				float w = particleData[i].width; 

				vertexData[i*4 +0] = new Vertex(new Vector2f(pos.x + w/2*cosA - h/2*sinA, pos.y + h/2*cosA + w/2*sinA), vertexData[i*4].color,	new Vector2f(0,0));
				vertexData[i*4 +1] = new Vertex(new Vector2f(pos.x - w/2*cosA - h/2*sinA, pos.y + h/2*cosA - w/2*sinA), vertexData[i*4].color,	new Vector2f(texture.getSize().x,0));
				vertexData[i*4 +2] = new Vertex(new Vector2f(pos.x - w/2*cosA + h/2*sinA, pos.y - h/2*cosA - w/2*sinA), vertexData[i*4].color,	new Vector2f(texture.getSize().x,texture.getSize().y));
				vertexData[i*4 +3] = new Vertex(new Vector2f(pos.x + w/2*cosA + h/2*sinA, pos.y - h/2*cosA + w/2*sinA), vertexData[i*4].color,	new Vector2f(0,texture.getSize().y));
			}
		}
	}

	private final void resetParticle(int i) {
		final float a = random(minRotationVelocity, maxRotationVelocity);
		final float cosA = (float) Math.cos(a);
		final float sinA = (float) Math.sin(a);
		
		Color color = random(minColor, maxColor);
		Vector2f pos = random(Vector2f.sub(centerPosition, halfSize), Vector2f.add(centerPosition, halfSize));
		float h = random(minParticleSize, maxParticleSize);
		float w = random(minParticleSize, maxParticleSize); 
		
		particleData[i] = new Particle( random(minVelocity, maxVelocity), 
				a, 
				w, h,
				pos,
				gameTime+random(minTtl, maxTtl) );
		
		vertexData[i*4 +0] = new Vertex(new Vector2f(pos.x + w/2*cosA - h/2*sinA, pos.y + h/2*cosA + w/2*sinA), color,	new Vector2f(0,0));
		vertexData[i*4 +1] = new Vertex(new Vector2f(pos.x - w/2*cosA - h/2*sinA, pos.y + h/2*cosA - w/2*sinA), color,	new Vector2f(texture.getSize().x,0));
		vertexData[i*4 +2] = new Vertex(new Vector2f(pos.x - w/2*cosA + h/2*sinA, pos.y - h/2*cosA - w/2*sinA), color,	new Vector2f(texture.getSize().x,texture.getSize().y));
		vertexData[i*4 +3] = new Vertex(new Vector2f(pos.x + w/2*cosA + h/2*sinA, pos.y - h/2*cosA + w/2*sinA), color,	new Vector2f(0,texture.getSize().y));
	}
	
	
	private static int random(int min, int max) {
		if(min>=max)
			return min;
		
		return RAND.nextInt(max-min) + min;
	}
	private static float random(float min, float max) {
		return RAND.nextFloat()*(max-min) + min;
	}
	private static Vector2f random(Vector2f min, Vector2f max) {
		return new Vector2f(random(min.x, max.x), random(min.y, max.y));
	}
	private static Color random(Color min, Color max) {
		return new Color(random(min.r, max.r), random(min.g, max.g), random(min.b, max.b), random(min.a, max.a));
	}

	@Override
	public boolean inside(Vector2f point) {
		return point.x>=centerPosition.x-halfSize.x && point.x<=centerPosition.x+halfSize.x
				&& point.y>=centerPosition.y-halfSize.y && point.y<=centerPosition.y+halfSize.y;
	}
}
