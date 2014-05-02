package de.secondsystem.game01.impl.graphic;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstShader;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.RenderTexture;
import org.jsfml.graphics.Shader;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.ResourceManager;
import de.secondsystem.game01.model.GameException;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IUpdateable;

public class Glow implements IDrawable, IUpdateable {

	private final RenderTexture buffer;
	
	private final Sprite bufferSprite;
	
	private final ConstShader shader;
	
	private final SpriteWrappper base;
	
	private final Color secColor;
	
	private final float sizeDecayPerSec;
	
	private int lastClipState = -1;
	
	private float size;
	
	private float waveSize;
	
	private float passedSecs = 0;
	
	public Glow(SpriteWrappper base, Color color, Color secColor, float size, float waveSize, float sizeDecayPerSec) {
		this.base = base;
		this.size = size;
		this.waveSize = waveSize;
		this.sizeDecayPerSec = sizeDecayPerSec;
		this.secColor = secColor;
		
		this.buffer = new RenderTexture();
		
		try {
			buffer.create((int) (base.getWidth()+Math.max(size,waveSize)*2), (int) (base.getHeight()+Math.max(size,waveSize)*2));
			shader = ResourceManager.shader_frag.get("glow.frag");
			((Shader) shader).setParameter("noiseTexture", ResourceManager.texture.get("noise.jpg"));
			
		} catch (TextureCreationException e) {
			throw new GameException(e);
		}
		
		this.bufferSprite = new Sprite(buffer.getTexture());
		this.bufferSprite.setOrigin(buffer.getSize().x/2, buffer.getSize().y/2);
		this.bufferSprite.setColor(color);
		updateBuffer();
	}
	
	private void updateBuffer() {
		if( lastClipState!=base.getClipState() ) {
			Vector2f p = base.getPosition();
			
			buffer.setView(new View(new Vector2f(p.x, p.y), new Vector2f(buffer.getSize().x, buffer.getSize().y)));
			((View) buffer.getView()).setRotation(base.getRotation());
			buffer.clear(Color.TRANSPARENT);
			base.draw(buffer);
			buffer.display();
			
			lastClipState = base.getClipState();
		}
	}
	
	@Override
	public void draw(RenderTarget renderTarget) {
		updateBuffer();
		bufferSprite.setPosition(base.getPosition());
		bufferSprite.setRotation(base.getRotation());
		
		((Shader) shader).setParameter("noiseOffsetFac", new Vector2f((float)Math.sin(passedSecs), (float)Math.cos(passedSecs)));
		((Shader) shader).setParameter("size", new Vector2f(size/buffer.getSize().x, size/buffer.getSize().y));
		((Shader) shader).setParameter("waveSize", new Vector2f(waveSize/buffer.getSize().x, waveSize/buffer.getSize().y));
		((Shader) shader).setParameter("secColor", secColor);
		renderTarget.draw(bufferSprite, new RenderStates(shader));
	}

	@Override
	public void update(long frameTimeMs) {
		passedSecs+=frameTimeMs/1000.f;
		
		size=size - (sizeDecayPerSec*frameTimeMs/1000.f);
		waveSize=waveSize - (sizeDecayPerSec*frameTimeMs/1000.f);
	}
}
