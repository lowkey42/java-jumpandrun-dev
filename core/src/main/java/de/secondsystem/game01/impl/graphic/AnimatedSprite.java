package de.secondsystem.game01.impl.graphic;

import java.io.IOException;
import java.nio.file.Paths;

import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IMoveable;


// temporary solution, end solution depends on the animated sprites
/* TODO: should 
 * 	... implement IUpdateable, IDimensioned & IAnimated
 *  ... keep its current frame & animation stored inside (abstraction)
 *  ... not require the instantiating code to know about that shit: height/width of a single frame; number of frames; number/positions of animations
 *  ... just expose functionality defined in its interfaces (draw, update, set/getPosition, getHeight/Width, play)
 */
public class AnimatedSprite implements IDrawable, IMoveable{
	
	private Sprite  sprite = new Sprite();
	private int frameWidth;
	private int frameHeight;
	private int numFramesX;

	public AnimatedSprite(String textureFilename, float x, float y, int numFrames,
            int frameWidth, int frameHeight)
	{
		// create texture
		Texture tex = new Texture();
		
		try {
			// TODO: load textures from a file
			tex.loadFromFile(Paths.get("assets/sprites/"+textureFilename));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		sprite.setTexture(tex);
		sprite.setPosition(new Vector2f(x,y));
		sprite.setOrigin(frameWidth/2f, frameHeight/2f);
		
		numFramesX = (int) Math.round((double)tex.getSize().x / frameWidth);
		this.frameWidth  = frameWidth;
		this.frameHeight = frameHeight;
		
	}
	

	public Sprite getSprite()
	{
		return sprite;
	}
	
	public int getFrameWidth()
	{
		return frameWidth;
	}
	
	public int getFrameHeight()
	{
		return frameHeight;
	}

	@Override
	public void draw(RenderTarget renderTarget) {
    	renderTarget.draw(sprite);
	}



	public void flip() {
		sprite.scale(-1.f, 1.f);
	}


	public void setFrame(float frameNum) {
    	int column = (int) frameNum % numFramesX;
    	
    	int row = (int) frameNum / numFramesX;
    	
    	IntRect rect = new IntRect(column * frameWidth, row * frameHeight, frameWidth, frameHeight);
    	
    	sprite.setTextureRect(rect);
	}

	@Override
	public void setPosition(Vector2f pos) {
		sprite.setPosition(pos);	
	}

	@Override
	public void setRotation(float degree) {
		sprite.setRotation(degree);
	}

	@Override
	public float getRotation() {
		return sprite.getRotation();
	}

	@Override
	public Vector2f getPosition() {
		return sprite.getPosition();
	}
	
	
}
