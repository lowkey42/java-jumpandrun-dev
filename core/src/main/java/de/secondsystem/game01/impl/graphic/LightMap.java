package de.secondsystem.game01.impl.graphic;

import java.io.IOException;

import org.jsfml.graphics.BlendMode;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstShader;
import org.jsfml.graphics.ConstTexture;
import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.RenderTexture;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Shader;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.TextureCreationException;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector3f;

import de.secondsystem.game01.impl.ResourceManager;
import de.secondsystem.game01.model.GameException;
import de.secondsystem.game01.model.collections.ISpatialIndex;
import de.secondsystem.game01.model.collections.ISpatialIndex.EntryWalker;
import de.secondsystem.game01.model.collections.SpatialGrid;

public class LightMap extends RenderTargetWrapper {

	private final RenderTexture lightMap;
	
	private int lastWorldMask;
	
	private final Vector2f windowSize;
	
	private Color ambientLight;
	
	private final Shader normalShader;
	
	private final ISpatialIndex<Light>[] lights;
	
	@SuppressWarnings("unchecked")
	public LightMap( RenderTarget renderTarget, byte lightGroups, Vector2f windowSize, int width, int height ) {
		super(renderTarget);
		
		this.windowSize = windowSize;
		
		lights = new ISpatialIndex[lightGroups];
		for( byte g=0; g<lightGroups; ++g ) 
			lights[g] = new SpatialGrid<>(500, new Vector2f(-500, -1500), new Vector2f(5000, 1000));
		
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
	
	@Override
	public void setView( ConstView view ) {
		lightMap.setView(view);
		super.setView(view);
	}

	private static class Closure$getNMShader {
		Vector2f lightPos;
		double dist;
	}
	
	public ConstShader getNMShader(final Vector2f pos, Vector2f size, ConstTexture normalMap) {
		normalShader.setParameter("normalMapped", normalMap!=null ? 1.f : 0.f);
		
		if( normalMap!=null ) {
			normalShader.setParameter("normals", normalMap);

			final Closure$getNMShader c = new Closure$getNMShader();
			
			c.lightPos = pos;
			c.dist = Double.MAX_VALUE;
			
			int group = 0;
			int groupMask = lastWorldMask;
			while( groupMask!=0 ) {
				if( (groupMask&1)!=0 )
					lights[group].query(pos, (float) Math.sqrt(size.x*size.x+size.y*size.y), new EntryWalker<Light>() {
						@Override public void walk(Light l) {
							double d = Math.sqrt(
									(pos.x-l.getPosition().x)*(pos.x-l.getPosition().x) +
									(pos.y-l.getPosition().y)*(pos.y-l.getPosition().y) );
							
							if( d<c.dist ) {
								c.dist=d;
								c.lightPos = l.getPosition();
							}
						}
					});
				
				group++;
				groupMask= groupMask>>1;
			}
			
			normalShader.setParameter("lightPos0", new Vector3f(c.lightPos.x, c.lightPos.y, 0.08f));
		}
		
		normalShader.setParameter("lightmap", lightMap.getTexture());
		normalShader.setParameter("ambientColor", ambientLight!=null ? ambientLight : Color.WHITE);
		normalShader.setParameter("windowSize", renderTarget instanceof RenderWindow ? windowSize : new Vector2f(lightMap.getSize().x,lightMap.getSize().y) );
		
		return normalShader;
	}
	
	public void drawVisibleLights( int groupMask, FloatRect rect ) {
		lastWorldMask = groupMask;
		
		int group = 0;
		while( groupMask!=0 ) {
			
			if( (groupMask&1)!=0 )
				lights[group].query(rect, new EntryWalker<Light>() {
					@Override public void walk(Light l) {
						drawLight(l);
					}
				});
			
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
		for( ISpatialIndex<Light> l : lights )
			l.remove(light);
	}

	public void draw(Sprite sprite, ConstTexture normalMap) {
		final ConstShader shader = getNMShader(
				sprite.getPosition(), 
				new Vector2f(
					sprite.getTexture().getSize().x*sprite.getScale().x, 
					sprite.getTexture().getSize().y*sprite.getScale().y),
				normalMap);
		
		draw(sprite, new RenderStates(shader));
	}
	
	@Override
	public void clear(Color color) {
		lightMap.clear();
		super.clear(color);
	}
	
}
