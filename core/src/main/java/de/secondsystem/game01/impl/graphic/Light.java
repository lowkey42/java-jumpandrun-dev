package de.secondsystem.game01.impl.graphic;

import org.jsfml.graphics.PrimitiveType;
import org.jsfml.graphics.VertexArray;
import org.jsfml.system.Vector2f;

public class Light {

	private Vector2f center;
	
	private float intensity, radius, degree, centerDegree;
	
	private VertexArray vertices;
	
	private boolean dirty;
	
	
	VertexArray getDrawable() {
		return dirty || vertices==null ? vertices=createDrawable(intensity, radius, degree, centerDegree) : vertices;
	}


	private static VertexArray createDrawable(float intensity2, float radius2,
			float degree2, float centerDegree2) {
		VertexArray v = new VertexArray(PrimitiveType.TRIANGLE_FAN);
		
		return v;
	}
	
}
