package de.secondsystem.game01.impl.game.controller;

import java.util.Set;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.PrimitiveType;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.graphics.Vertex;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.game.entities.IControllableGameEntity;
import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.IGameEntityController;
import de.secondsystem.game01.impl.graphic.LightMap;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.physics.IDynamicPhysicsBody;
import de.secondsystem.game01.impl.map.physics.IPhysicsBody;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;
import de.secondsystem.game01.model.GameException;
import de.secondsystem.game01.model.HDirection;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.ISerializable;
import de.secondsystem.game01.model.IUpdateable;
import de.secondsystem.game01.model.VDirection;
import de.secondsystem.game01.util.Tools;

public class FlyingHunterAiController implements IUpdateable, IGameEntityController,
		ISerializable, IDrawable {
	
	private static final long RETARGET_DELAY = 250;
	
	private static final boolean DEBUG_DRAW = false;

	private static final float MIN_QDISTANCE = 100*100;
	
	private final IControllableGameEntity controlledEntity;
	
	private final float maxDistanceQuad;
	
	private final Set<IGameEntity> targetGroup;
	
	private final String targetGroupName;
	
	private IGameEntity targetEntity;
	
	private long retargetDelayLeft;
	
	private LightMap lm;
	
	public FlyingHunterAiController(IControllableGameEntity entity, IGameMap map, Attributes attributes) {
		if( !(entity.getPhysicsBody() instanceof IDynamicPhysicsBody) )
			throw new GameException("FlyingHunterAiController requires a IDynamicPhysicsBody, "+entity.getPhysicsBody()+" given.");
		
		controlledEntity = entity;
		targetGroupName = attributes.getString("targetGroup").intern();
		targetGroup = map.getEntityManager().listByGroup(targetGroupName);
		maxDistanceQuad = (float) Math.pow(attributes.getFloat("maxDistance"), 2);
		
		lm = map.getLightMap();
	}

	@Override
	public void draw(RenderTarget renderTarget) {
		if( !DEBUG_DRAW )
			return;

		for( IGameEntity ce : targetGroup ) {
			if( ce.isInWorld(controlledEntity.getWorldId()) ) {
				final Vector2f pos = controlledEntity.getPosition();
				final Vector2f target = ce.getPosition(); 

				
				final float qdist = ((target.x-pos.x)*(target.x-pos.x) + (target.y-pos.y)*(target.y-pos.y)); 
				
				Color color;
				if( qdist<MIN_QDISTANCE )
					color = Color.RED;
				else if( qdist<=maxDistanceQuad )
					color = isTargetVisible(ce) ? Color.MAGENTA : Color.GREEN;
				else
					color = Color.WHITE;

				final IPhysicsBody obs = controlledEntity.getPhysicsBody().raycastSolid(target);

				
				if( obs!=null )
					lm.draw(new Vertex[]{new Vertex(pos,color), new Vertex(obs.getPosition(), color)}, PrimitiveType.LINES);
				else
					lm.draw(new Vertex[]{new Vertex(pos,Color.BLUE), new Vertex(target, Color.BLUE)}, PrimitiveType.LINES);
			}
		}
	}
	
	private IGameEntity findNearestTarget() {
		IGameEntity minEntity = null;
		float minQdist = maxDistanceQuad;
		
		for( IGameEntity ce : targetGroup ) {
			if( ce.isInWorld(controlledEntity.getWorldId()) ) {
				
				final Vector2f pos = controlledEntity.getPosition();
				final Vector2f target = ce.getPosition();
				
				final float qdist = ((target.x-pos.x)*(target.x-pos.x) + (target.y-pos.y)*(target.y-pos.y)); 
				
				if( qdist<minQdist && (qdist<MIN_QDISTANCE || isTargetVisible(ce)) ) {
					minEntity = ce;
					minQdist = qdist;
				}
			}
		}
		
		return minEntity;
	}
	
	@Override
	public void update(long frameTimeMs) {
		if( (retargetDelayLeft-=frameTimeMs) <=0 ) {
			targetEntity = findNearestTarget();
			retargetDelayLeft = RETARGET_DELAY;
		}
		
		if( targetEntity!=null ) {
			final Vector2f pos = controlledEntity.getPosition();
			final Vector2f target = targetEntity.getPosition();
			
			final Vector2f dir = Tools.normalizedVector(target.x-pos.x, target.y-pos.y);

			// check if the target point is reached
			boolean reachedX = Tools.nearEqual(dir.x, 0) ? true : (dir.x<0 ? (pos.x <= target.x) : (pos.x >= target.x));
			boolean reachedY = Tools.nearEqual(dir.y, 0) ? true : (dir.y<0 ? (pos.y <= target.y) : (pos.y >= target.y));
			
			// if not reached move to target point
			if( !reachedX )
				controlledEntity.moveHorizontally( dir.x<0 ? HDirection.LEFT : HDirection.RIGHT, Math.abs(dir.x) );
			
			if( !reachedY )
				controlledEntity.moveVertically( dir.y<0 ? VDirection.UP : VDirection.DOWN, Math.abs(dir.y) );
			
			return;
		}
		
		((IDynamicPhysicsBody)controlledEntity.getPhysicsBody()).resetVelocity(true, true, true);
	}
	
	private boolean isTargetVisible(IGameEntity target) {
		final IPhysicsBody obs = controlledEntity.getPhysicsBody().raycastSolid(target.getPosition());
		
		return obs==null || obs.equals(target.getPhysicsBody());
	}
	
	public Attributes serialize() {
		return new Attributes(
				new Attribute("targetGroup", targetGroup),
				new Attribute("maxDistance", Math.sqrt(maxDistanceQuad)),
				new Attribute(ControllerUtils.FACTORY, ControllerUtils.normalizeControllerFactory(FlyingHunterAiControllerFactory.class.getName()))
		);
	}
		
}

class FlyingHunterAiControllerFactory implements IControllerFactory {

	@Override
	public FlyingHunterAiController create(IControllableGameEntity entity, IGameMap map, Attributes attributes) {
		return new FlyingHunterAiController(entity, map, attributes);
	}
	
}

