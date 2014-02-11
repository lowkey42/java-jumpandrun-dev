package de.secondsystem.game01.util;

import org.jsfml.graphics.RectangleShape;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.graphic.ISpriteWrapper;
import de.secondsystem.game01.impl.map.ILayerObject;

public class Tools {
	
	public static boolean isInside(RectangleShape shape, Vector2f p) {
		// inside check for OBB
		for(int i=0; i < 4; i++) {
			Vector2f v1 = shape.getTransform().transformPoint(shape.getPoint(i));
			Vector2f v2 = shape.getTransform().transformPoint(shape.getPoint(i < 3 ? i+1 : 0));
			if( isPointLeft(v1, v2, p) )
				return false;
		}
		
		return true;
	}
	
	public static boolean isPointLeft(Vector2f v1, Vector2f v2, Vector2f checkPoint) {
		return ((v2.x - v1.x)*(checkPoint.y - v1.y) - (v2.y - v1.y)*(checkPoint.x - v1.x)) < 0;
	}
	
	public static void setRectangleShape(RectangleShape shape, Vector2f size, Vector2f origin, Vector2f pos, float rotation) {
		shape.setSize(size);
		shape.setOrigin(origin);
		shape.setRotation(rotation);
		shape.setPosition(pos);	
	}
	
	public static float vectorLength(float x, float y) {
		return (float) Math.sqrt(x*x + y*y);
	}
	
	public static float vectorLength(Vector2f v) {
		return vectorLength(v.x, v.y);
	}
	
	public static Vector2f normalizedVector(Vector2f v) {
		return normalizedVector(v.x, v.y);
	}
	
	public static Vector2f normalizedVector(float x, float y) {
		float length = vectorLength(x,y);
		if( length > 0 )
			return new Vector2f(x / length, y / length);
		else
			return new Vector2f(0.f, 0.f);
	}
	
	public static boolean nearEqual(float a, float b) {
		return Math.abs(a-b) <= 0.000001f;
	}
	
	public static float clampedRotation(float rotation) {
		double a = rotation % 360;
		return (float) (a < 0 ? 360 + a : a);
	}

	public static Vector2f shapeNormal(RectangleShape shape, int vertex1, int vertex2) {
		Vector2f v1 = shape.getTransform().transformPoint(shape.getPoint(vertex1));
		Vector2f v2 = shape.getTransform().transformPoint(shape.getPoint(vertex2));
		
		return normalizedVector(Vector2f.sub(v2, v1));
	}
	
	public static float dot(Vector2f v1, Vector2f v2) {
		return v1.x * v2.x + v1.y * v2.y;
	}
	
	public static Vector2f distanceVector(RectangleShape shape, int vertex1, int vertex2, Vector2f p) {
		Vector2f v1 = shape.getTransform().transformPoint(shape.getPoint(vertex1));
		Vector2f v2 = shape.getTransform().transformPoint(shape.getPoint(vertex2));
		
		return distanceVector(v1, v2, p);
	}
	
	public static Vector2f distanceVector(Vector2f v1, Vector2f v2, Vector2f p) {
		Vector2f n = normalizedVector(Vector2f.sub(v1, v2));
		Vector2f c1 = Vector2f.sub(v1, p);
		Vector2f c2 = Vector2f.mul(n, dot(c1, n));
			
		return Vector2f.sub(c1, c2);
	}
	
	public static float distance(Vector2f v1, Vector2f v2, Vector2f p) {
		return vectorLength(distanceVector(v1, v2, p));
	}
}
