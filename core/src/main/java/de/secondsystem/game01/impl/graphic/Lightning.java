package de.secondsystem.game01.impl.graphic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.jsfml.graphics.BlendMode;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.ConstTexture;
import org.jsfml.graphics.PrimitiveType;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Vertex;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.window.Mouse;
import org.jsfml.window.Window;

import de.secondsystem.game01.impl.ResourceManager;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IMoveable;
import de.secondsystem.game01.model.IUpdateable;
import de.secondsystem.game01.util.Tools;

public class Lightning implements IDrawable, IUpdateable {

	private final Random random = new Random();

	private long timeAcc;
	private final List<Vertex> vertices = new ArrayList<>();
	
	private IMoveable aObj;
	private Vector2f lastAPos;
	
	private IMoveable bObj;
	private Vector2f lastBPos;
	
	private final Bolt bolts[] = new Bolt[4];
	private int activeBolt = 0;
	private int nextBolt = 1;
	
	public Lightning() throws IOException {
	}
	
	@Override
	public void update(long frameTimeMs) {
		timeAcc+= frameTimeMs;
		if( timeAcc>=500 ) {
			timeAcc=0;
			activeBolt = nextBolt;
			nextBolt = random.nextInt(bolts.length);
			if( nextBolt==activeBolt )
				nextBolt = (nextBolt+1)%4;
			
			if( aObj!=null && (lastAPos==null || lastAPos.equals(aObj.getPosition()))
					|| bObj!=null && (lastBPos==null || lastBPos.equals(bObj.getPosition())) ) {
				
				if( aObj!=null )
					lastAPos = aObj.getPosition();
				
				if( bObj!=null )
					lastBPos = bObj.getPosition();
				
				updateBolts();
			}
		}
	}
	
	@Override
	public void draw(RenderTarget renderTarget) {
		
		vertices.clear();
		bolts[nextBolt].draw(vertices, timeAcc/500f);
		bolts[activeBolt].draw(vertices, 1-timeAcc/500f);
		
		renderTarget.draw(vertices.toArray(new Vertex[vertices.size()]), PrimitiveType.QUADS, new RenderStates(BlendMode.ADD));
		
	}

	
	protected void updateBolts() {
		for( int i=0; i<bolts.length; ++i ) {
			bolts[i] = createBolt();
		}
	}
	
	protected Bolt createBolt() {
		if( lastAPos!=null && lastBPos!=null )
			return createDirectedBolt(lastAPos, lastBPos);
		
		else if( lastAPos!=null )
			return createUndirectedBolt(lastAPos);

		else if( lastBPos!=null )
			return createUndirectedBolt(lastBPos);
		
		else
			return null;
	}
	
	protected Bolt createDirectedBolt(Vector2f source, Vector2f dest) {
		return null; // TODO
	}
	protected Bolt createUndirectedBolt(Vector2f source) {
		return null; // TODO
	}

	protected static List<Segment> CreateBolt(Vector2f source, Vector2f dest) {
		return CreateBolt(source, source, dest, 1);
	}
	protected static List<Segment> CreateBolt(Vector2f absSource, Vector2f source, Vector2f dest, int depth)
	{
		Random rand = new Random();
		
	    List<Segment> results = new ArrayList<>();
	    Vector2f tangent = Vector2f.sub(dest, source);
	    Vector2f normal = Tools.normalizedVector(tangent.y, -tangent.x);
	    float length = Tools.vectorLength(tangent);
	    float absLength = Tools.vectorLength(Vector2f.sub(dest, absSource));
	 
	    List<Float> positions = new ArrayList<>();
	  //  positions.add(0f);
	 
	    for (int i = 0; i < length/10; i++)
	        positions.add(rand.nextFloat());
	 
	    Collections.sort(positions);
	 
	    final float Sway = 80;
	    final float Jaggedness = 1 / Sway;

		 
	    results.add(new Segment(source.x, source.y));
	    
	    float prevDisplacement = 0;
	    for (int i = 1; i < positions.size(); i++)
	    {
	        float pos = positions.get(i);
	 
	        // used to prevent sharp angles by ensuring very close positions also have small perpendicular variation.
	        float scale = (length * Jaggedness) * (pos - positions.get(i - 1));
	 
	        // defines an envelope. Points near the middle of the bolt can be further from the central line.
	        float envelope = pos > 0.95f ? 20 * (1 - pos) : 1;
	 
	        float displacement = rand.nextFloat()*Sway*2 - Sway;
	        displacement -= (displacement - prevDisplacement) * (1 - scale);
	        displacement *= envelope;
	        prevDisplacement = displacement;
	 
	        Vector2f point = Vector2f.add(Vector2f.add(source, Vector2f.mul(tangent, pos)), Vector2f.mul(normal, displacement));
	        results.add(new Segment(point.x, point.y));
	        
	        
	        if( depth<3 && rand.nextInt(depth+10)==0 ) {
	        	final float degRot = rand.nextFloat()*40 - 20;
	    		final float rotation = (float) Math.toRadians(degRot<0 ? degRot-20 : degRot+20);
	    		final float rsin = (float) Math.sin(rotation);
	    		final float rcos = (float) Math.cos(rotation);
	    		
	    		final float diffX = dest.x - point.x;
	    		final float diffY = dest.y - point.y;
	    		
	        	Vector2f branchDest = new Vector2f(point.x+ diffX*rcos - diffY*rsin, point.y+ diffX*rsin + diffY*rcos); 
	        	results.get(results.size()-1).subTree.addAll( CreateBolt(absSource, point, branchDest, depth+1) );
	        }
	    }
	 
	    results.add(new Segment(dest.x, dest.y));
	 
	    return results;
	}

	private static class Bolt {
		
		private List<Segment> branches = new ArrayList<>();

		public void draw(List<Vertex> vertices, float alpha) {
			_draw(vertices, alpha, branches, 0);
		}
		private void _draw(List<Vertex> vertices, float alpha, List<Segment> seg, int depth) {
			Vector2f p = null;
			for( Segment c : seg ) {
				
				if( p!=null )
					createLine(vertices, p, c.point, 10, new Color(255, 200, 80, (int) (255*alpha*((4f-depth)/4)) ));
			
				_draw(vertices, alpha, c.subTree, depth+2);
				
				p = c.point;
			}
		}
		
	}
	
	private static class Segment {
		final Vector2f point;
		final List<Segment> subTree = new ArrayList<>();
		
		public Segment( float x, float y ) {
			this.point = new Vector2f(x, y);
		}
	}
	
	private static void createLine(List<Vertex> vertices, Vector2f a, Vector2f b, float thickness, Color color) {
		final Vector2f tangent = Vector2f.sub(b, a);
		final float rotation = (float)Math.atan2(tangent.y, tangent.x);
		final float rsin = (float) Math.sin(rotation) * thickness/2;
		final float rcos = (float) Math.cos(rotation) * thickness/2;
		
		vertices.add( new Vertex(new Vector2f(a.x+ -rcos - -rsin, a.y+ -rsin + -rcos), color) );
		vertices.add( new Vertex(new Vector2f(a.x+ -rcos -  rsin, a.y+ -rsin +  rcos), color) );
		vertices.add( new Vertex(new Vector2f(b.x+  rcos - -rsin, b.y+  rsin + -rcos), color) );
		vertices.add( new Vertex(new Vector2f(b.x+  rcos -  rsin, b.y+  rsin +  rcos), color) );
	}
	
}
