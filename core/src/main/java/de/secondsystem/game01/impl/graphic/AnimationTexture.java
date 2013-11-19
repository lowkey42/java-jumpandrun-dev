package de.secondsystem.game01.impl.graphic;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jsfml.graphics.ConstTexture;
import org.jsfml.graphics.IntRect;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import de.secondsystem.game01.impl.ResourceManager;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.IAnimated.AnimationType;

public final class AnimationTexture {
	private final Map<AnimationType, AnimationData> map;
	
	public AnimationTexture(Path path) throws IOException {
		final JSONParser parser = new JSONParser();
		
		try ( Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8) ){
			@SuppressWarnings("unchecked")
			final Attributes obj = new Attributes( (Map<String, Object>) parser.parse(reader) );
			
			final Map<AnimationType, AnimationData> tMap = new HashMap<>();
			for( String animId : obj.keySet() ) {
				final Attributes animAttr = obj.getObject(animId);
				
				final AnimationType at = AnimationType.valueOf(animId);
				if( at==null )
					throw new IOException("Invalid AnimationType: "+animId);
				
				tMap.put(at, new AnimationData(animAttr));
			}
			
			map = Collections.unmodifiableMap(tMap); 
			
		} catch (IOException | ParseException e) {
			throw new IOException("Loading animation texture failed '"+path+"': "+e.getMessage(), e);
		}
	}
	
	public AnimationData get(AnimationType type) {
		return map.get(type);
	}
	
	
	public static final class AnimationData {
		public final ConstTexture texture;
		public final int frameWidth;
		public final int frameHeight;
		public final int numRowFrames;
		public final int frameStart;
		public final int frameEnd;
		public final float fps;
		private boolean animationFinished = false;
		
		public AnimationData(Attributes animAttr) throws IOException {
			this.texture 	  = ResourceManager.texture.get(animAttr.getString("texture"));
			this.frameWidth   = animAttr.getInteger("frameWidth");
			this.frameHeight  = animAttr.getInteger("frameHeight");
			this.numRowFrames = animAttr.getInteger("numRowFrames");
			this.frameStart   = animAttr.getInteger("frameStart");
			this.frameEnd     = animAttr.getInteger("frameEnd");
			this.fps          = animAttr.getFloat("fps");
			
		}
		
		public final float calculateNextFrame(float currentFrame, float deltaTime) {
			currentFrame += fps/1000.f * deltaTime;
			animationFinished = false;
			
			if( currentFrame > frameEnd )
			{
				currentFrame = ((currentFrame-frameStart) % (frameEnd-frameStart)) + frameStart;
				animationFinished = true;
			}
			
			return currentFrame;
		}
		
		public final IntRect calculateTextureFrame(float currentFrame)
		{
	    	int column = (int) currentFrame % numRowFrames;
	    	
	    	int row = (int) currentFrame / numRowFrames;
	    	
	    	return new IntRect(column * frameWidth, row * frameHeight, frameWidth, frameHeight);
		}
		
		public boolean isAnimationFinished()
		{
			return animationFinished;
		}
	}
	
	
}
