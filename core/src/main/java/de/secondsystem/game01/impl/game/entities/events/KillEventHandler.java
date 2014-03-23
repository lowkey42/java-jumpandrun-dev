package de.secondsystem.game01.impl.game.entities.events;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.impl.scripting.timer.TimerManager;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;

public class KillEventHandler implements IEventHandler {

	private final class OtherWorldSpawnInfo {

		final TimerManager timerManager;
		
		final String type;
		
		final Attributes attributes;
		
		OtherWorldSpawnInfo(IGameMap map, Attributes attributes) {
			timerManager = map.getScriptEnv().getTimerManager();
			
			this.type = attributes.getString("type");
			this.attributes = attributes.getObject("attributes"); 
		}
		void create(final IGameEntity org, final WorldId worldId) {
			// delay creation (after physics-update)
			timerManager.createTimer(0, false, new Runnable() {
				@Override
				public void run() {
					org.manager().create(type, new Attributes(attributes, new Attributes(
							new Attribute("x", org.getPosition().x),
							new Attribute("y", org.getPosition().y),
							new Attribute("rotation", org.getRotation()),
							new Attribute("worldId", worldId.id)
					)) );
				}
			});
		}
	}
	
	private final OtherWorldSpawnInfo spawnInfo;
	
	private final boolean noAfterlife;
	
	private long lastHit=0;
	
	public KillEventHandler(IGameMap map, Attributes attributes) {
		Attributes spAttr = attributes.getObject("spawn");
		spawnInfo = spAttr==null ? null : new OtherWorldSpawnInfo(map, spAttr);
		noAfterlife = attributes.getBoolean("noAfterlife", false);
	}
	
	@Override
	public Object handle(Object... args) {
		if( lastHit!=0 && lastHit>System.currentTimeMillis() )
			return null;
		
		lastHit = System.currentTimeMillis() + 1000;
		
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
		entity.setDead(true);
		entity.manager().destroy(entity.uuid());
	}
	
	protected void inMainWorld(IGameEntity entity, IGameEntity other) {
		if( noAfterlife )
			killEntity(entity);
		
		else if( spawnInfo!=null ) {
			killEntity(entity);
			spawnInfo.create(entity, WorldId.OTHER);
			
		} else {
			entity.forceWorld(WorldId.OTHER);
			entity.setDead(true);
		}
	}

	protected void inOtherWorld(IGameEntity entity, IGameEntity other) {
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
		return new KillEventHandler(map, attributes);
	}
}
