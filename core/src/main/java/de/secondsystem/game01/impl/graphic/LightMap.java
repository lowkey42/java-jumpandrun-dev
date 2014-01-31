package de.secondsystem.game01.impl.graphic;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.jsfml.graphics.BlendMode;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstShader;
import org.jsfml.graphics.ConstTexture;
import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.RenderTexture;
import org.jsfml.graphics.Shader;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector3f;

import de.secondsystem.game01.impl.ResourceManager;
import de.secondsystem.game01.model.GameException;

public class LightMap extends RenderTargetWrapper {

	private final RenderTexture lightMap;
	
	private final Vector2f windowSize;
	
	private Color ambientLight;
	
	private final Shader normalShader;
	
	private final Set<Light>[] lights;
	
	@SuppressWarnings("unchecked")
	public LightMap( RenderTarget renderTarget, byte lightGroups, Vector2f windowSize, int width, int height ) {
		super(renderTarget);
		
		this.windowSize = windowSize;
		
		lights = new Set[lightGroups];
		for( byte g=1; g<=lightGroups; ++g ) 
			lights[g-1] = new HashSet<>();
		
		lightMap = new RenderTexture();
		try {
			normalShader = (Shader) ResourceManager.shader.get("normalMapping");
			
		} catch (IOException e1) {
			throw new GameException("Unable to load normalShader");
		}
		
		try {
			lightMap.create(width, height);
			
		} catch (TextureCreationException e) {
			throw new GameException("Unable to create lightMap");
		}
	}
	
	public void setAmbientLight(Color ambientLight) {
		this.ambientLight = ambientLight;
	}
	
	public void setView( ConstView view ) {
		lightMap.setView(view);
	}

	public ConstShader getNMShader(Vector2f pos, int groupMask, ConstTexture normalMap) {
		normalShader.setParameter("normalMapped", normalMap!=null ? 1.f : 0.f);
		
		if( normalMap!=null ) {
			normalShader.setParameter("normals", normalMap);

			// TODO: determine lights by 'pos'
			normalShader.setParameter("lightPos0", new Vector3f(400,100, 0.07f));
		}
		
		normalShader.setParameter("lightmap", lightMap.getTexture());
		normalShader.setParameter("ambientColor", ambientLight!=null ? ambientLight : Color.WHITE);
		normalShader.setParameter("windowSize", windowSize);
		
		return normalShader; // TODO
	}
	
	public void drawVisibleLights( int groupMask, FloatRect rect ) {
		int group = 0;
		while( groupMask!=0 ) {
			
			if( (groupMask&1)!=0 )			
				for( Light l : lights[group] ) {
					drawLight(l);
				}
			
			group++;
			groupMask= groupMask>>1;
		}
		
		lightMap.display();
	}
	
	private void drawLight( Light light ) {
		lightMap.draw(light.getDrawable(), new RenderStates(BlendMode.ADD));
	}
	
	public Light createLight(int groupMask, Vector2f center, Color color, float radius, float degree, float centerDegree) {
		final Light l = new Light(center, color, radius, degree, centerDegree);
		
		int group = 0;
		while( groupMask!=0 ) {
			
			if( (groupMask&1)!=0 )			
				lights[group].add(l);
			
			group++;
			groupMask= groupMask>>1;
		}
		
		return l;
	}
	public void destroyLight(Light light) {
		for( Set<Light> l : lights )
			l.remove(light);
	}

	public void draw(Sprite sprite, ConstTexture normalMap, int worldMask) {
		final ConstShader shader = getNMShader(sprite.getPosition(), worldMask, normalMap);
		
		draw(sprite, new RenderStates(shader));
	}
	
	@Override
	public void clear(Color color) {
		lightMap.clear();
		super.clear(color);
	}
	
}
