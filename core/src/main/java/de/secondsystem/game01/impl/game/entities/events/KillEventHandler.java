package de.secondsystem.game01.impl.game.entities.events;

import de.secondsystem.game01.impl.game.entities.IControllableGameEntity;
import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;

public class KillEventHandler implements IEventHandler {

	private long lastHit=0;
	
	public KillEventHandler() {
	}
	
	@Override
	public Object handle(Object... args) {
		if( lastHit!=0 && lastHit>System.currentTimeMillis() )
			return null;
		
		lastHit = System.currentTimeMillis() + 2000;
		
		final IGameEntity owner = (IGameEntity) args[0];
		final IGameEntity killer = (IGameEntity) args[1];
		
		switch( owner.getWorldId() ) {
			case MAIN:
				inMainWorld(owner, killer);
				break;
				
			case OTHER:
				inOtherWorld(owner, killer);
				break;
		}
		
		return null;
	}

	protected void killEntity(IGameEntity entity) {
		entity.manager().destroy(entity.uuid());
	}
	
	protected void inMainWorld(IGameEntity entity, IGameEntity other) {
		if( entity instanceof IControllableGameEntity )
			entity.forceWorld(WorldId.OTHER);
//			if( !entity.setWorld(WorldId.OTHER) ) {
//				killEntity(entity);
//			}
		
		entity.setDead(true);
	}

	protected void inOtherWorld(IGameEntity entity, IGameEntity other) {
		entity.setDead(true);
		killEntity(entity);
	}

	@Override
	public Attributes serialize() {
		return new Attributes(new Attribute(EventUtils.FACTORY, EventUtils.normalizeHandlerFactory(KillEHF.class.getName())));
	}
	
}

final class KillEHF implements IEventHandlerFactory {
	@Override
	public KillEventHandler create(IGameMap map, Attributes attributes) {
		return new KillEventHandler();
	}
}
