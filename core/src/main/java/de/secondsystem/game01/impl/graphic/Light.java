package de.secondsystem.game01.impl.graphic;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.PrimitiveType;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Vertex;
import org.jsfml.graphics.VertexArray;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.model.IDimensioned;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IInsideCheck;
import de.secondsystem.game01.model.IMoveable;
import de.secondsystem.game01.model.IScalable;
import de.secondsystem.game01.model.collections.IndexedMoveable;
import de.secondsystem.game01.util.Tools;

public class Light extends IndexedMoveable implements IMoveable, IInsideCheck, IDrawable, IDimensioned, IScalable {

	private static final int SUBDIVISIONS = 32;
	
	private Vector2f center;
	
	private Color color;
	
	private float radius, degree, rotation;
	
	private VertexArray vertices;
	
	private boolean dirty;
	
	Light(Vector2f center, Color color, float radius, float degree, float rotation) {
		this.center = center;
		this.color = color;
		this.radius = radius;
		this.degree = degree;
		this.rotation = rotation;
		this.dirty = true;
	}

	VertexArray getDrawable() {
		if( dirty || vertices==null ) {
			dirty = false;
			
			return vertices=createDrawable(center, color, radius, (float)Math.toRadians(degree), (float)Math.toRadians(rotation));
		}
			
		return  vertices;
	}


	private static VertexArray createDrawable(Vector2f center, Color color, float radius,
			float degree, float rotation) {
		
		VertexArray v = new VertexArray(PrimitiveType.TRIANGLE_FAN);
		v.ensureCapacity(SUBDIVISIONS+1);
		
		v.add(new Vertex(center, color));
		
		final float startDegree = (float) (rotation-degree/2 -Math.PI/2);
		final float stepSize = (float) ((Math.PI*2)/SUBDIVISIONS);
		final Color borderColor = new Color(0, 0, 0, 0);
		
		for (float angle=0; angle<=Math.min(Math.PI*2, degree)+stepSize; angle+=stepSize ) {
			v.add(new Vertex(
					new Vector2f(
							radius*(float)Math.cos(angle+startDegree) + center.x,
                    		radius*(float)Math.sin(angle+startDegree) + center.y),
                    		borderColor ));
		}
		
		return v;
	}

	
	public void setColor(Color color) {
		this.dirty |= !color.equals(this.color);
		this.color = color;
	}
	public void setRadius(float radius) {
		this.dirty |= this.radius!=radius;
		this.radius = radius;
	}
	public void setDegree(float degree) {
//		float nDegree = (float) Math.toRadians(degree); // ?
		this.dirty |= !Tools.nearEqual(degree, this.degree);
		this.degree = degree;
	}

	public Color getColor() {
		return color;
	}
	public float getRadius() {
		return radius;
	}
	public float getDegree() {
		return degree;
	}

	@Override
	public boolean inside(Vector2f point) {
		return (point.x-center.x)*(point.x-center.x)+(point.y-center.y)*(point.y-center.y) <= radius*radius;
	}
	
	@Override
	protected void doSetPosition(Vector2f pos) {
		this.dirty |= !pos.equals(center);
		this.center = pos;
	}

	@Override
	public void setRotation(float degree) {
		this.dirty |= !Tools.nearEqual(degree, this.rotation);
		this.rotation = degree;
	}

	@Override
	public float getRotation() {
		return rotation;
	}

	@Override
	public Vector2f getPosition() {
		return center;
	}

	@Override
	public void draw(RenderTarget renderTarget) {
		// do nothing
	}

	@Override
	public float getHeight() {
		return radius*2;
	}

	@Override
	public float getWidth() {
		return radius*2;
	}

	@Override
	public void setDimensions(float width, float height) {
		setRadius(width/2);
//		setRotation(height);
	}
}
