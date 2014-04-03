package de.secondsystem.game01.impl.graphic;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jsfml.audio.ConstSoundBuffer;
import org.jsfml.graphics.ConstTexture;
import org.jsfml.graphics.IntRect;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.secondsystem.game01.impl.ResourceManager;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.IAnimated.AnimationType;

public final class AnimationTexture {
	private final Map<AnimationType, AnimationData> animations;
	private final AnimationType defaultAnimationType; 
	
	public AnimationTexture(Path path) throws IOException {
		final JSONParser parser = new JSONParser();
		
		try ( Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8) ){
			@SuppressWarnings("unchecked")
			final Attributes obj = new Attributes( (Map<String, Object>) parser.parse(reader) );
			
			defaultAnimationType = AnimationType.valueOf(obj.getString("default", "IDLE"));
			
			final Map<AnimationType, AnimationData> tMap = new HashMap<>();
			for( AnimationType at : AnimationType.values() ) {
				final Attributes animAttr = obj.getObject(at.name());
				
				if( animAttr!=null )
					tMap.put(at, new AnimationData(animAttr));
			}
			
			animations = Collections.unmodifiableMap(tMap); 
			
		} catch (IOException | ParseException e) {
			throw new IOException("Loading animation texture failed '"+path+"': "+e.getMessage(), e);
		}
	}

	public AnimationType getDefaultType() {
		return defaultAnimationType;
	}
	public AnimationData getDefault() {
		AnimationData ad = get(defaultAnimationType);
		if( ad!=null )
			return ad;
		
		return animations.values().iterator().next();
	}
	public AnimationData get(AnimationType type) {
		return animations.get(type);
	}
	
	
	/**
	 * [immutable]
	 * @author lowkey
	 *
	 */
	public static final class AnimationData {
		public final ConstTexture texture;
		public final ConstSoundBuffer sound;
		public final int frameWidth;
		public final int frameHeight;
		public final int numRowFrames;
		public final int frameStart;
		public final int frameEnd;
		public final float fps;
		
		public AnimationData(Attributes animAttr) throws IOException {
			String texPath = animAttr.getString("texture");
			String guiTexPath = animAttr.getString("texture_gui");
			
			this.texture		= texPath != null ? ResourceManager.texture.get(texPath) : ResourceManager.texture_gui.get(guiTexPath);
			this.sound			= !animAttr.containsKey("sound") ? null : ResourceManager.sound.getNullable(animAttr.getString("sound"));
			this.frameWidth		= animAttr.getInteger("frameWidth");
			this.frameHeight	= animAttr.getInteger("frameHeight");
			this.numRowFrames	= animAttr.getInteger("numRowFrames");
			this.frameStart		= animAttr.getInteger("frameStart");
			this.frameEnd		= animAttr.getInteger("frameEnd");
			this.fps			= animAttr.getFloat("fps");
			
		}
		
		/**
		 * 
		 * @param currentFrame
		 * @param deltaTime
		 * @param repeated
		 * @return next frame or <0 after animation finished
		 */
		public final float calculateNextFrame(float currentFrame, float deltaTime, boolean repeated) {
			currentFrame += fps/1000.f * deltaTime;
			
			if( currentFrame > frameEnd )
			{
				currentFrame = !repeated ? -1 : ((currentFrame-frameStart) % (frameEnd-frameStart)) + frameStart;
			}
			
			return currentFrame;
		}
		
		public final float calculateLastFrame(float currentFrame, float deltaTime, boolean repeated) {
			currentFrame -= fps/1000.f * deltaTime;
			
			if( currentFrame < frameStart )
			{
				currentFrame = !repeated ? -1 : ((currentFrame-frameStart) % (frameEnd-frameStart)) + frameEnd;
			}
			
			return currentFrame;
		}
		
		public final IntRect calculateTextureFrame(float currentFrame) {
	    	int column = (int) currentFrame % numRowFrames;
	    	
	    	int row = (int) currentFrame / numRowFrames;
	    	
	    	return new IntRect(column * Math.abs(frameWidth), row * Math.abs(frameHeight), Math.abs(frameWidth), frameHeight);
		}
	}
	
	
}
