package de.secondsystem.game01.impl.game.controller;

import de.secondsystem.game01.impl.game.entities.IControllableGameEntity;
import de.secondsystem.game01.impl.game.entities.IGameEntityController;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.physics.IDynamicPhysicsBody;
import de.secondsystem.game01.impl.map.physics.IHumanoidPhysicsBody;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;
import de.secondsystem.game01.model.GameException;
import de.secondsystem.game01.model.HDirection;
import de.secondsystem.game01.model.ISerializable;
import de.secondsystem.game01.model.IUpdateable;

public class SimpleAiController implements IUpdateable, IGameEntityController,
		ISerializable {

	private static final long COOLDOWN = 100;
	
	private long stableCooldown = 0;
	
	private final IControllableGameEntity controlledEntity;
	
	private final boolean stayOnPlatform;
	
	private HDirection dir = HDirection.RIGHT;
	
	public SimpleAiController(IControllableGameEntity entity, IGameMap map,	Attributes attributes) {
		if( !(entity.getPhysicsBody() instanceof IDynamicPhysicsBody) )
			throw new GameException("SimpleAiController requires a IDynamicPhysicsBody, "+entity.getPhysicsBody()+" given.");
		
		controlledEntity = entity;
		stayOnPlatform = attributes.getBoolean("stayOnPlatform", true) && entity.getPhysicsBody() instanceof IHumanoidPhysicsBody;
	}

	@Override
	public void update(long frameTimeMs) {
		if( stableCooldown<=0 ) {
			if( Math.abs(((IDynamicPhysicsBody)controlledEntity.getPhysicsBody()).getVelocity().x) < 0.1
				|| (stayOnPlatform && !((IHumanoidPhysicsBody)controlledEntity.getPhysicsBody()).isStable()  ) ) {
					dir = dir==HDirection.LEFT ? HDirection.RIGHT : HDirection.LEFT;
					stableCooldown = COOLDOWN;
			}
		
		} else if( stableCooldown>0 )
			stableCooldown-=frameTimeMs;
		else if( stableCooldown!=0 )
			stableCooldown=0;
		
		
		controlledEntity.moveHorizontally(dir, 1);
	}
	
	public Attributes serialize() {
		return new Attributes(
				new Attribute("stayOnPlatform", stayOnPlatform),
				new Attribute(ControllerUtils.FACTORY, ControllerUtils.normalizeControllerFactory(SAiControllerFactory.class.getName()))
		);
	}
		
}

class SAiControllerFactory implements IControllerFactory {

	@Override
	public SimpleAiController create(IControllableGameEntity entity, IGameMap map, Attributes attributes) {
		return new SimpleAiController(entity, map, attributes);
	}
	
}

