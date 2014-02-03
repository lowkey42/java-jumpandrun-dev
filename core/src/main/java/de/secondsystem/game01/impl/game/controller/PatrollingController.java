package de.secondsystem.game01.impl.game.controller;

import java.util.ArrayList;
import java.util.List;

import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.game.entities.IControllable.HDirection;
import de.secondsystem.game01.impl.game.entities.IControllable.VDirection;
import de.secondsystem.game01.impl.game.entities.IControllableGameEntity;
import de.secondsystem.game01.impl.game.entities.IGameEntityController;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;
import de.secondsystem.game01.model.IPlayable;
import de.secondsystem.game01.model.ISerializable;
import de.secondsystem.game01.model.IUpdateable;
import de.secondsystem.game01.util.Tools;

public class PatrollingController implements IUpdateable, IGameEntityController, ISerializable, IPlayable {

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
	
	public PatrollingController(IControllableGameEntity entity, IGameMap map,
			Attributes attributes) {
		repeated = attributes.getBoolean("repeated", true);
		controlledEntity = entity;
		
		List<Attributes> tpAttributes = attributes.getObjectList("targetPoints");
		
		if( tpAttributes == null )
			System.out.println("targetPoints: null");
		
		else
			for(Attributes tp : tpAttributes)
				addTargetPoint(tp.getFloat("x"), tp.getFloat("y"));
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
		dir = targetPoint != null ? Tools.normalizedVector(targetPoint.x-controlledEntity.getPosition().x, 
				targetPoint.y-controlledEntity.getPosition().y) : new Vector2f(0,0);
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

	public void resume() {
		play();
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
			boolean reachedX = Tools.nearEqual(dir.x, 0) ? true : (dir.x<0 ? (pos.x <= targetPoint.x) : (pos.x >= targetPoint.x));
			boolean reachedY = Tools.nearEqual(dir.y, 0) ? true : (dir.y<0 ? (pos.y <= targetPoint.y) : (pos.y >= targetPoint.y));
			
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
	
	public Attributes serialize() {
		final List<Attributes> o = new ArrayList<>(targetPoints.size());
		for(Vector2f tp : targetPoints)
			o.add( new Attributes(new Attribute("x", tp.x), new Attribute("y", tp.y)) );
		
		return new Attributes(
				new Attribute("repeated", repeated),
				new Attribute("targetPoints", o),
				new Attribute(ControllerUtils.FACTORY, ControllerUtils.normalizeControllerFactory(PatrollingControllerFactory.class.getName()))
		);
	}
		
}

class PatrollingControllerFactory implements IControllerFactory {

	@Override
	public PatrollingController create(IControllableGameEntity entity, IGameMap map, Attributes attributes) {
		return new PatrollingController(entity, map, attributes);
	}
	
}
