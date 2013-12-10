package de.secondsystem.game01.impl.game.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.jsfml.system.Vector2f;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import de.secondsystem.game01.impl.game.entities.GameEntityManager;
import de.secondsystem.game01.impl.game.entities.IControllable.HDirection;
import de.secondsystem.game01.impl.game.entities.IControllable.VDirection;
import de.secondsystem.game01.impl.game.entities.IControllableGameEntity;
import de.secondsystem.game01.impl.game.entities.IGameEntityController;
import de.secondsystem.game01.impl.game.entities.IGameEntityManager;
import de.secondsystem.game01.model.IUpdateable;

public class PatrollingController implements IUpdateable, IGameEntityController {

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
	public JSONObject serialize() {
		JSONObject obj = new JSONObject();
		obj.put("repeated", repeated);
		obj.put("controlledEntity", controlledEntity.uuid().toString());
		
		JSONObject o = new JSONObject();
		int i = 0;
		for(Vector2f t : targetPoints)  {	
			JSONArray  a = new JSONArray();
			a.add(t.x);
			a.add(t.y);
			o.put(i, a);
			i++;
		}
		
		obj.put("targetPoints", o);
		
		return obj;
	}
	
	public void deserialize(JSONObject obj, IGameEntityManager entityManager) {
		repeated = (boolean) obj.get("repeated");
		UUID uuid = UUID.fromString( (String) obj.get("controlledEntity") );;
		controlledEntity = (IControllableGameEntity) entityManager.get(uuid);
		
		JSONObject targetPoints = (JSONObject) obj.get("targetPoints");
		
		if( targetPoints == null )
			return;
		
		int i = 0;
		for(@SuppressWarnings("unused") Object o : targetPoints.keySet()) {
			JSONArray a = ((JSONArray) targetPoints.get(i));
			float x = (float) a.get(0);
			float y = (float) a.get(1);
			
			addTargetPoint(x, y);
			i++;
		}
	}
}
