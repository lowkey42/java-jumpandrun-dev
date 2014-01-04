package de.secondsystem.game01.impl.game.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jsfml.system.Vector2f;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.secondsystem.game01.impl.game.entities.IControllable.HDirection;
import de.secondsystem.game01.impl.game.entities.IControllable.VDirection;
import de.secondsystem.game01.impl.game.entities.IControllableGameEntity;
import de.secondsystem.game01.impl.game.entities.IGameEntityController;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;
import de.secondsystem.game01.model.ISerializable;
import de.secondsystem.game01.model.IUpdateable;

public class PatrollingController implements IUpdateable, IGameEntityController, ISerializable {

	private IControllableGameEntity controlledEntity;
	private boolean repeated;
	
	private List<Vector2f> targetPoints = new ArrayList<Vector2f>();
	
	private Vector2f targetPoint;
	private Vector2f dir;
	
	private int tpIndex = 0;
	private boolean reverse = false;
	private boolean play = false;
	
	public PatrollingController() {
		
	}
	
	public PatrollingController( IControllableGameEntity controlledEntity, boolean repeated ) {
		this.controlledEntity = controlledEntity;
		this.repeated = repeated;
		controlledEntity.setController(this);
	}
	
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
	
	public void removeTargetPoint(int index) {
		if( index < 0 || index >= targetPoints.size() )
			return;
		targetPoints.remove(index);
	}
	
	private void computeDirection() {
		if( tpIndex < 0 || tpIndex >= targetPoints.size() )
			return;
		targetPoint = targetPoints.get(tpIndex);
		dir = targetPoint != null ? normalizedVector(targetPoint.x-controlledEntity.getPosition().x, targetPoint.y-controlledEntity.getPosition().y) : new Vector2f(0,0);
	}
	
	public void reverse() {
		play = true;
		reverse = true;
		tpIndex = reverse ? tpIndex-1 : tpIndex+1;
		tpIndex = tpIndex < 0 ? 0 : tpIndex >= targetPoints.size() ? targetPoints.size()-1 : tpIndex;
		computeDirection();
	}
	
	public void stop() {
		tpIndex = reverse ? targetPoints.size()-1 : 0;
		play = false;
	}
	
	public void pause() {
		play = false;
	}
	
	public void play() {
		play = true;
		reverse = false;
		tpIndex = reverse ? tpIndex-1 : tpIndex+1;
		tpIndex = tpIndex < 0 ? 0 : tpIndex >= targetPoints.size() ? targetPoints.size()-1 : tpIndex;
		computeDirection();
	}
	
	@Override
	public void update(long frameTimeMs) {
		if( play && targetPoints.size() > tpIndex && tpIndex >= 0 ) {
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
				
				if( repeated )
					if( (reverse && tpIndex == 0) || (!reverse && tpIndex == targetPoints.size()-1) )
						reverse = !reverse;
							
				tpIndex = reverse ? tpIndex-1 : tpIndex+1;
				
				computeDirection();
			}		
		}
	}
	
	@SuppressWarnings("unchecked")
	public Attributes serialize() {
		Attributes obj = new Attributes();
		obj.put("repeated", repeated);
		
		List<Attributes> o = new ArrayList<>(targetPoints.size());
		for(Vector2f tp : targetPoints)
			o.add( new Attributes(new Attribute("x", tp.x), new Attribute("y", tp.y)) );
		
		obj.put("targetPoints", o);
		
		return obj;
	}
	
	/**
	 * @return null if the controller doesn't already exist
	 */
	public PatrollingController deserialize(JSONObject obj, IGameMap map) {
		this.uuid = UUID.fromString((String) obj.get("uuid"));
		if( map.getControllerManager().get(this.uuid) != null )
			return (PatrollingController) map.getControllerManager().get(this.uuid);
		
		repeated = (boolean) obj.get("repeated");
		UUID uuid = UUID.fromString( (String) obj.get("controlledEntity") );;
		controlledEntity = (IControllableGameEntity) map.getEntityManager().get(uuid);	
		JSONObject targetPoints = (JSONObject) obj.get("targetPoints");
		
		if( targetPoints == null )
			System.out.println("targetPoints: null");;

		for(Object o : targetPoints.values()) {
			JSONArray a = ((JSONArray) o);
			float x = Float.valueOf(a.get(0).toString());
			float y = Float.valueOf(a.get(1).toString());
			
			addTargetPoint(x, y);
		}
		
		controlledEntity.setController(this);
		map.getControllerManager().add(this);
		
		return null;
	}
	
}
