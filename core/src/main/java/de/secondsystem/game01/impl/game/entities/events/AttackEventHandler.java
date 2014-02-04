package de.secondsystem.game01.impl.game.entities.events;

import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.physics.IDynamicPhysicsBody;
import de.secondsystem.game01.impl.map.physics.IHumanoidPhysicsBody;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;
import de.secondsystem.game01.util.Tools;

public class AttackEventHandler implements IEventHandler {

	public AttackEventHandler() {
	}
	
	@Override
	public Object handle(Object... args) {
		final IGameEntity owner = (IGameEntity) args[0];
		final IGameEntity target = (IGameEntity) args[1];
		final Float force = (Float) args[2];
		
		return attack(owner, target, force!=null ? force : 0);
	}

	protected boolean attack(IGameEntity owner, IGameEntity target, float force) {
		target.notify(EventType.DAMAGED, target, owner);
		
		if( target.getPhysicsBody() instanceof IDynamicPhysicsBody && !((IDynamicPhysicsBody)target.getPhysicsBody()).isKinematic() ) {
			Vector2f vec = Vector2f.mul(Tools.normalizedVector(Vector2f.sub(target.getPosition(), owner.getPosition())), force);
			
			((IDynamicPhysicsBody)target.getPhysicsBody()).move(vec.x, vec.y);
		}
		
		return target instanceof IHumanoidPhysicsBody;
	}

	@Override
	public Attributes serialize() {
		return new Attributes(new Attribute(EventUtils.FACTORY, EventUtils.normalizeHandlerFactory(AttackEHF.class.getName())));
	}
	
}

final class AttackEHF implements IEventHandlerFactory {
	@Override
	public AttackEventHandler create(IGameMap map, Attributes attributes) {
		return new AttackEventHandler();
	}
}
