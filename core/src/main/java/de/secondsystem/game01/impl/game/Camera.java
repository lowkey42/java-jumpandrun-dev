package de.secondsystem.game01.impl.game;

import org.jsfml.graphics.ConstView;
import org.jsfml.graphics.View;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.ICameraController;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.model.IUpdateable;

public class Camera implements IUpdateable {
	
	private float x;
	private float y;
	private WorldId worldId;
	
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
	
	public void setController(ICameraController cameraController) {
		this.cameraController = cameraController;
		this.x = cameraController.getPosition().x;
		this.y = cameraController.getPosition().y;
		this.recentering = false;
		this.worldId = cameraController.getWorldId();
	}
	
	@Override
	public void update(long frameTimeMs) {
		if( worldId!=cameraController.getWorldId() ) {
			worldId = cameraController.getWorldId();
		}
		
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

				x = interpolateLinear(recenterStarted.x, recenterTarget.x, timeAcc);
				y = interpolateLinear(recenterStarted.y, recenterTarget.y, timeAcc);
				
				recentering = timeAcc <= 1.f;
				
			} else {
				x = nc.x;
				y = nc.y;
			}
		}
	}
	
	private static float interpolateLinear(float valStart, float valTarget, float timeDiff) {
		return valStart+(valTarget-valStart)*timeDiff;
	}
	
	public ConstView createView( ConstView base ) {
		return new View(new Vector2f(x, y), base.getSize());
	}
	
	public WorldId getWorldId() {
		return worldId;
	}
	
	private boolean inDistance(Vector2f vec, float dist) {
		return Math.pow(x-vec.x, 2) + Math.pow(y-vec.y, 2) < dist*dist;
	}
	
}
