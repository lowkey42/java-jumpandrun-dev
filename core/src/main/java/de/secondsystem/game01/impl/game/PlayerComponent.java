package de.secondsystem.game01.impl.game;

import java.util.UUID;

import org.jsfml.graphics.Color;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.game.entities.AbstractGameEntityController;
import de.secondsystem.game01.impl.game.entities.IControllableGameEntity;
import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.IGameEntityController;
import de.secondsystem.game01.impl.game.entities.effects.GEGlowEffect;
import de.secondsystem.game01.impl.game.entities.effects.IGameEntityEffect;
import de.secondsystem.game01.impl.game.entities.events.AttackEventHandler;
import de.secondsystem.game01.impl.game.entities.events.EventType;
import de.secondsystem.game01.impl.game.entities.events.IEventHandler;
import de.secondsystem.game01.impl.game.entities.events.KillEventHandler;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.IGameMap.WorldId;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.Attributes.Attribute;
import de.secondsystem.game01.model.IUpdateable;

/**
 * TODO:
 * 
 * @author lowkey
 *
 */
final class PlayerComponent implements IUpdateable {

	private boolean isPlayerDead = false;
	
	private IControllableGameEntity otherPlayer;
	private IControllableGameEntity player;
	private IGameEntity playerPlaceholder;

	private UUID ignoreDamageEntity;
	private long ignoreDamageTimer=0;

	private IControllableGameEntity possessedEntity;
	private IGameEntityController possessedEntityController;
	private IGameEntityEffect possessionEffect;
	private Vector2f fixedPlayerPos;

	
	private final Camera camera;
	private final AbstractGameEntityController controller;
	private final IGameMap map;

	public PlayerComponent(IGameMap map, Camera camera, AbstractGameEntityController controller) {
		this.map = map;
		this.camera = camera;
		
		for( IGameEntity pe : map.getEntityManager().listByGroup("player") ) {
			if( pe instanceof IControllableGameEntity ) {
				if( pe.isInWorld(WorldId.MAIN) )
					player = (IControllableGameEntity) pe;
				
				else if( pe.isInWorld(WorldId.OTHER) )
					otherPlayer = (IControllableGameEntity) pe;
			}
		}
		
		boolean startInOtherWorld = player==null;
		
		if( otherPlayer==null )
			otherPlayer = map.getEntityManager().createControllable("player", new Attributes(new Attribute("x",0), new Attribute("y",0), new Attribute("world",WorldId.OTHER.id)) );
		
		otherPlayer.setDead(player==null);

		if( player!=null ) {
			player.addEventHandler(EventType.DAMAGED, new PlayerDeathEventHandler(map) );
			player.addEventHandler(EventType.ATTACK, new PlayerAttackEventHandler());
		}
		
		final IControllableGameEntity activePlayer = startInOtherWorld ? otherPlayer : player;
		
		this.controller = controller;
		controller.addGE(activePlayer);
		camera.setController(activePlayer);
	}

	public boolean isPlayerDead() {
		return isPlayerDead;
	}
	
	@Override
	public void update(long frameTimeMs) {
		if( fixedPlayerPos!=null ) {
			player.setPosition(fixedPlayerPos);
			fixedPlayerPos = null;
		}
		
		if( System.currentTimeMillis()>=ignoreDamageTimer ) {
			ignoreDamageEntity=null;
			unpossess();
		}
	}

	private IEventHandler possessedEntityDamagedHandler = new IEventHandler() {
		@Override public Attributes serialize() {
			return null;
		}
		
		@Override public Object handle(Object... args) {
			unpossess();
			return null;
		}
	};
	
	private void possess(IControllableGameEntity player, IControllableGameEntity target, int possessTime) {
		ignoreDamageEntity = target.uuid();
		ignoreDamageTimer = System.currentTimeMillis() + possessTime;
		
		possessedEntity = target;

		possessedEntityController = possessedEntity.getController();
		controller.addGE(possessedEntity);
		controller.removeGE(player);
		player.setWorldMask(0);
		camera.setController(possessedEntity);
		possessedEntity.addEventHandler(EventType.DAMAGED, possessedEntityDamagedHandler);

		possessedEntity.addEffect(possessionEffect=new GEGlowEffect(map, possessedEntity.getRepresentation(), 
				new Color(80, 30, 200, 255), new Color(200, 200, 255, 100), 15, 30, 15/(possessTime/1000.f) ));
	}
	private boolean unpossess() {
		if( possessedEntity!=null ) {
			ignoreDamageEntity = possessedEntity.uuid();
			ignoreDamageTimer = System.currentTimeMillis() + 5000;
			
			possessedEntity.removeEventHandler(EventType.DAMAGED, possessedEntityDamagedHandler);
			if( possessedEntity.isLiftingSomething() )
				possessedEntity.liftOrThrowObject(2);

			player.forceWorld(WorldId.MAIN);
			player.setPosition(fixedPlayerPos=possessedEntity.getPosition());
			player.setRotation(possessedEntity.getRotation());
			controller.addGE(player);
			controller.removeGE(possessedEntity);
			possessedEntity.setController(possessedEntityController);
			possessedEntity.removeEffect(possessionEffect);
			possessedEntity.addEffect(possessionEffect=new GEGlowEffect(map, possessedEntity.getRepresentation(), 
					new Color(50, 10, 100, 255), new Color(160, 100, 180, 100), 20, 30, 18/5.f), 5000);

			camera.setController(player);
			possessedEntity = null;
			
			return true;
		}
		
		return false;
	}
	
	private final class PlayerAttackEventHandler extends AttackEventHandler {
		@Override protected boolean attack(IGameEntity owner, IGameEntity target,
				float force) {
			if( owner.isInWorld(WorldId.MAIN) && target instanceof IControllableGameEntity && ((IControllableGameEntity) target).getPossessableTime()>0 ) {
				possess((IControllableGameEntity) owner, (IControllableGameEntity) target, ((IControllableGameEntity) target).getPossessableTime());
				return true;
				
			} else
				return super.attack(owner, target, force);
		}
		
	}

	private final class PlayerDeathEventHandler extends KillEventHandler {

		public PlayerDeathEventHandler(IGameMap map) {
			super(map, new Attributes());
		}

		@Override
		public Object handle(Object... args) {
			if( args[1] instanceof IGameEntity && ((IGameEntity)args[1]).uuid().equals(ignoreDamageEntity) )
				return null;
			
			return super.handle(args);
		}
		
		@Override
		protected void killEntity(IGameEntity entity) {
			isPlayerDead = true;
		}
	}
}
