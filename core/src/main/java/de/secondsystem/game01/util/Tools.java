package de.secondsystem.game01.util;

import org.jsfml.graphics.RectangleShape;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.ILayerObject;

public class Tools {
	
	private static final RectangleShape shape = new RectangleShape();
	
	public static boolean isInside(RectangleShape shape, Vector2f p) {
		for(int i=0; i < 4; i++) {
			Vector2f v1 = shape.getTransform().transformPoint(shape.getPoint(i));
			Vector2f v2 = shape.getTransform().transformPoint(shape.getPoint(i < 3 ? i+1 : 0));
			if( isPointLeft(v1, v2, p) )
				return false;
		}
		
		return true;
	}
	
	public static boolean isInside(ILayerObject layerObject, Vector2f p) {
		Vector2f ns = new Vector2f(layerObject.getWidth(), layerObject.getHeight());
		setRectangleShape(shape, ns, new Vector2f(ns.x / 2f, ns.y / 2f), layerObject.getPosition(), layerObject.getRotation());
		
		return isInside(shape, p);
	}
	
	public static boolean isPointLeft(Vector2f v1, Vector2f v2, Vector2f checkPoint) {
		return ((v2.x - v1.x)*(checkPoint.y - v1.y) - (v2.y - v1.y)*(checkPoint.x - v1.x)) < 0;
	}
	
	public static void setRectangleShape(RectangleShape shape, Vector2f size, Vector2f origin, Vector2f pos, float rotation) {
		shape.setSize(size);
		shape.setOrigin(origin);
		shape.setPosition(pos);
		shape.setRotation(rotation);
	}
}
