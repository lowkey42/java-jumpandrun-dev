package de.secondsystem.game01.impl.game.entities;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.game.entities.events.EntityEventHandler;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.model.Attributes;

public class MovingPlatform extends GameEntity {
	
	private Queue<Vector2f> targetPoints = new LinkedList<Vector2f>();
	
	private float moveSpeed;
	private Vector2f targetPoint;
	private Vector2f dir;
	private boolean repeated = false;
	
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
	
	public MovingPlatform(UUID uuid, GameEntityManager em, IGameMap map,
			EntityEventHandler eventHandler, Attributes attributes) {
		super(uuid, em, attributes.getInteger("worldId", map.getActiveWorldId().id), 
				GameEntityHelper.createRepresentation(attributes), GameEntityHelper.createPhysicsBody(map, false, false, false, attributes), map, eventHandler);
		
		moveSpeed = attributes.getFloat("moveSpeed");
		repeated  = attributes.getBoolean("repeated");
		
//		if( repeated )
//			addTargetPoint(getPosition());
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
		dir = targetPoint != null ? normalizedVector(targetPoint.x-getPosition().x, targetPoint.y-getPosition().y) : new Vector2f(0,0);
	}
	
	@Override
	public void update(long frameTimeMs) {
		physicsBody.setLinearVelocity(0, 0);
		
		if( targetPoint != null ) {
			Vector2f pos = getPosition();
			
			// move to target point
			physicsBody.setLinearVelocity(dir.x*moveSpeed*frameTimeMs, dir.y*moveSpeed*frameTimeMs);
			
			// check if the target point is reached
			boolean reachedX = nearEqual(pos.x, targetPoint.x) ? true : dir.x > 0 ?  pos.x > targetPoint.x : pos.x < targetPoint.x; 
			boolean reachedY = nearEqual(pos.y, targetPoint.y) ? true : dir.y > 0 ?  pos.y > targetPoint.y : pos.y < targetPoint.y;
			
			if( reachedX && reachedY ) {
				Vector2f p = targetPoints.poll();
				if( repeated )
					targetPoints.add(p);		
				computeDirection();
				System.out.println(pos);
			}		
		}
		
		super.update(frameTimeMs);
	}
}
