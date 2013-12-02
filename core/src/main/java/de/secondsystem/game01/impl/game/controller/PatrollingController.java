package de.secondsystem.game01.impl.game.controller;

import java.util.LinkedList;
import java.util.Queue;

import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.game.entities.IControllable.HDirection;
import de.secondsystem.game01.impl.game.entities.IControllable.VDirection;
import de.secondsystem.game01.impl.game.entities.IControllableGameEntity;
import de.secondsystem.game01.impl.game.entities.IGameEntityController;
import de.secondsystem.game01.model.IUpdateable;

public class PatrollingController implements IUpdateable, IGameEntityController {

	private final IControllableGameEntity controlledEntity;
	private final boolean repeated;
	
	public PatrollingController( IControllableGameEntity controlledEntity, boolean repeated ) {
		this.controlledEntity = controlledEntity;
		this.repeated = repeated;
		controlledEntity.setController(this);
	}

	
	private Queue<Vector2f> targetPoints = new LinkedList<Vector2f>();
	
	private Vector2f targetPoint;
	private Vector2f dir;
	
	static public float vectorLength(float x, float y) {
		return (float) Math.sqrt(x*x + y*y);
	}
	
	static public Vector2f normalizedVector(Vector2f v) {
		return normalizedVector(v.x, v.y);
	}
	
	static public Vector2f normalizedVector(float x, float y) {
		float length = vectorLength(x,y);
		if( length > 0 )
			return new Vector2f(x / length, y / length);
		else
			return new Vector2f(0.f, 0.f);
	}
	
	static public boolean nearEqual(float a, float b) {
		return Math.abs(a-b) <= 0.1f;
	}
	
	public void addTargetPoint(Vector2f point) {
		addTargetPoint(point.x, point.y);		
	}
	
	public void addTargetPoint(float x, float y) {
		targetPoints.add(new Vector2f(x, y));		
		computeDirection();
	}
	
	private void computeDirection() {
		targetPoint = targetPoints.peek();
		dir = targetPoint != null ? normalizedVector(targetPoint.x-controlledEntity.getPosition().x, targetPoint.y-controlledEntity.getPosition().y) : new Vector2f(0,0);
	}
	
	@Override
	public void update(long frameTimeMs) {
	//	physicsBody.setLinearVelocity(0, 0);
		
		if( targetPoint != null ) {
			Vector2f pos = controlledEntity.getPosition();
			
			// check if the target point is reached
			boolean reachedX = nearEqual(dir.x, 0) ? true : (dir.x<0 ? (pos.x <= targetPoint.x) : (pos.x >= targetPoint.x));
			boolean reachedY = nearEqual(dir.y, 0) ? true : (dir.y<0 ? (pos.y <= targetPoint.y) : (pos.y >= targetPoint.y));
			
			// if not reached move to target point
			if( !reachedX )
				controlledEntity.moveHorizontally( dir.x<0 ? HDirection.LEFT : HDirection.RIGHT, Math.abs(dir.x) );
			
			if( !reachedY )
				controlledEntity.moveVertically( dir.y<0 ? VDirection.UP : VDirection.DOWN, Math.abs(dir.y) );
			
			// if reached move to the next target
			if( reachedX && reachedY ) {
				controlledEntity.setPosition(targetPoint);
				Vector2f p = targetPoints.poll();
				if( repeated )
					targetPoints.add(p);		
				computeDirection();
				System.out.println(pos);
			}		
		}
	}
}
