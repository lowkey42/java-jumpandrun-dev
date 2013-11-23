package de.secondsystem.game01.impl.game;

import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.ICameraController;
import de.secondsystem.game01.model.IUpdateable;

public class Camera implements IUpdateable {
	
	private float x;
	private float y;
	
	private ICameraController cameraController;
	
	private long notMovingTime = 0;
	
	private boolean freezeCam = false;
	
	private boolean recentering = true;
	
	private Vector2f recenterStarted;
	private Vector2f recenterTarget;
	
	private float timeAcc = 0;
	
	public Camera(Vector2f center) {
		this.x = center.x;
		this.y = center.y;
		recenterStarted = new Vector2f(x, y);
	}
	public Camera(ICameraController cameraController) {
		this(cameraController.getPosition());
		this.cameraController = cameraController;
	}
	
	@Override
	public void update(long frameTimeMs) {
		final Vector2f nc = cameraController.getPosition();
		
		if( inDistance(nc, 20) ) {
			if( !freezeCam && (notMovingTime+=frameTimeMs) >= 2000 ) {
				freezeCam = true;
				System.out.println("freeze");
			}
		
		} else {
			notMovingTime = 0;
			if( !inDistance(nc, 200) ) {
				freezeCam = false;
				recentering = true;
			}
		}

		
		if( !freezeCam ) {
			if( recentering ) {
				if( recenterTarget==null || Math.pow(recenterTarget.x-nc.x, 2) + Math.pow(recenterTarget.y-nc.y, 2) > 50*50 ) {
					recenterStarted = new Vector2f(x, y);
					recenterTarget = new Vector2f(nc.x, nc.y);
					timeAcc = 0;
				}
				
				timeAcc= Math.min( timeAcc+frameTimeMs/500.f, 1 );

				x = recenterTarget.x*timeAcc + (1-timeAcc)*recenterStarted.x;
				y = recenterTarget.y*timeAcc + (1-timeAcc)*recenterStarted.y;
				
				recentering = timeAcc < 1.f;
				
			} else {
				x = nc.x;
				y = nc.y;
			}
		}
	}

	public ConstView createView( ConstView base ) {
		return new View(new Vector2f(x, y), base.getSize());
	}
	
	private boolean inDistance(Vector2f vec, float dist) {
		return Math.pow(x-vec.x, 2) + Math.pow(y-vec.y, 2) < dist*dist;
	}
	
}
