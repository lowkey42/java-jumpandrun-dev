package de.secondsystem.game01.impl.graphic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jsfml.graphics.BlendMode;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.FloatRect;
import org.jsfml.graphics.PrimitiveType;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Vertex;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IMoveable;
import de.secondsystem.game01.model.collections.ISpatialIndex;
import de.secondsystem.game01.model.collections.ISpatialIndex.EntryWalker;
import de.secondsystem.game01.model.collections.SpatialGrid;
import de.secondsystem.game01.util.Tools;

public class LightningManager implements IDrawable {
	
	private static final long FADE_TIME = 250;
	
	public static interface ILightningSource extends IMoveable {
		float getMaxDist();

		int getWorldMask();
	}

	private final Random random = new Random();
	
	private List<Vertex> activeBoltVertices = new ArrayList<>();
	
	private List<Vertex> nextBoltVertices = new ArrayList<>();
	
	private final ISpatialIndex<ILightningSource>[] sources;

	private long timeAcc;
	
	
	@SuppressWarnings("unchecked")
	public LightningManager(byte groups) {
		sources = new ISpatialIndex[groups];
		for( byte g=0; g<groups; ++g ) 
			sources[g] = new SpatialGrid<>(500, new Vector2f(-500, -1500), new Vector2f(5000, 1000));
		
	}

	public void addSource(ILightningSource source, int groupMask) {
		int group = 0;
		while( groupMask!=0 ) {
			
			if( (groupMask&1)!=0 )			
				sources[group].add(source);
			
			group++;
			groupMask= groupMask>>1;
		}
	}
	public void removeSource(ILightningSource source) {
		for( ISpatialIndex<ILightningSource> l : sources )
			l.remove(source);
	}
	public void moveSource(ILightningSource source, int groupMask) {
		for( int i=0; i<sources.length; ++i )
			if( (groupMask&(1<<i))!=0 )
				sources[i].add(source);
			else
				sources[i].remove(source);
	}

	@Override
	public void draw(RenderTarget renderTarget) {
		renderTarget.draw(updateAlpha(activeBoltVertices, 1-timeAcc/(float)FADE_TIME), PrimitiveType.QUADS, new RenderStates(BlendMode.ADD));
		renderTarget.draw(updateAlpha(nextBoltVertices, timeAcc/(float)FADE_TIME), PrimitiveType.QUADS, new RenderStates(BlendMode.ADD));
	}

	public void update(long frameTimeMs, final int groupMask, ConstView view) {
		timeAcc+= frameTimeMs;
		if( timeAcc>=FADE_TIME ) {
			timeAcc=0;
			
			
			final List<Vertex> targetVert = activeBoltVertices;
			targetVert.clear();
			
			activeBoltVertices = nextBoltVertices;
			
			sources[groupMask].query(new FloatRect(view.getCenter().x-view.getSize().x/2, view.getCenter().y-view.getSize().y/2, view.getSize().x, view.getSize().y), 
				new EntryWalker<LightningManager.ILightningSource>() {
		
					@Override
					public void walk(final ILightningSource entry) {
						sources[groupMask].query(entry.getPosition(), entry.getMaxDist(), new EntryWalker<ILightningSource>() {
		
							@Override public void walk(ILightningSource other) {
								createDirectedBolt(targetVert, entry.getPosition(), other.getPosition());
							}
							
							@Override public void finished(int numFound) {
								if( numFound==0 )
									createUndirectedBolt(targetVert, entry.getPosition());
							}
							
						} );
					}
			});

			nextBoltVertices = targetVert;
			
		}
	}


	private void createDirectedBolt(List<Vertex> outVertices, Vector2f source, Vector2f dest) {
		// TODO: nicer bolts
		createJaggedLine(outVertices, source, dest, 10, 2, Color.YELLOW);
		createJaggedLine(outVertices, source, dest, 10, 2, Color.YELLOW);
	}

	private void createUndirectedBolt(List<Vertex> outVertices, Vector2f source) {
		// TODO Auto-generated method stub
	}

	private void createJaggedLine(List<Vertex> vertices, Vector2f source, Vector2f dest, int points, float thickness, Color color) {
		Vector2f tangent = Vector2f.sub(dest, source);
		Vector2f normal = Tools.normalizedVector(tangent.y, -tangent.x);
		final float length = Tools.vectorLength(tangent);
		

	    final float Sway = 80;
	    final float Jaggedness = 1 / Sway;

	    float pos = random.nextFloat()/points;
	    float lastPos = pos;
	    float px = source.x, py = source.y;
	    
	    float prevDisplacement = 0;
	    for (int i = 1; i < points; i++) {
	    	lastPos = pos;
	        pos += random.nextFloat()/points;
	 
	        // used to prevent sharp angles by ensuring very close positions also have small perpendicular variation.
	        float scale = (length * Jaggedness) * (pos - lastPos);
	 
	        // defines an envelope. Points near the middle of the bolt can be further from the central line.
	        float envelope = pos > 0.95f ? 20 * (1 - pos) : 1;
	 
	        float displacement = random.nextFloat()*Sway*2 - Sway;
	        displacement -= (displacement - prevDisplacement) * (1 - scale);
	        displacement *= envelope;
	        prevDisplacement = displacement;
	 
	        final float x = source.x + tangent.x*pos + normal.x*displacement;
	        final float y = source.y + tangent.y*pos + normal.y*displacement;
	        
	        createLine(vertices, px, py, x, y, thickness, color);
	        px=x;
	        py=y;
	    }
	    
        createLine(vertices, px, py, dest.x, dest.y, thickness, color);
	}
	
	private static void createLine(List<Vertex> vertices, float ax, float ay, float bx, float by, float thickness, Color color) {
		final float rotation = (float)Math.atan2(by-ay, bx-ax);
		final float rsin = (float) Math.sin(rotation) * thickness/2;
		final float rcos = (float) Math.cos(rotation) * thickness/2;
		
		vertices.add( new Vertex(new Vector2f(ax+ -rcos - -rsin, ay+ -rsin + -rcos), color) );
		vertices.add( new Vertex(new Vector2f(ax+ -rcos -  rsin, ay+ -rsin +  rcos), color) );
		vertices.add( new Vertex(new Vector2f(bx+  rcos - -rsin, by+  rsin + -rcos), color) );
		vertices.add( new Vertex(new Vector2f(bx+  rcos -  rsin, by+  rsin +  rcos), color) );
	}
	
	private static Vertex[] updateAlpha(List<Vertex> vertices, float alpha) {
		Vertex[] r = new Vertex[vertices.size()];
		int i=0;
		for( Vertex v : vertices )
			r[i++] = new Vertex(v.position, new Color(v.color, (int) (v.color.a*alpha)));
		
		return r;
	}
	
}
