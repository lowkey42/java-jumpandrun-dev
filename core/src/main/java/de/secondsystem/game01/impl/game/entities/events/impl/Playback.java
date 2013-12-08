package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.HashMap;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler.EntityEventType;

public class Playback extends SequencedObject {
	
	public class PlaybackInputOption {
		public final HashMap<IGameEntity, IPlayedBack> playTrigger     = new HashMap<>();
		public final HashMap<IGameEntity, IPlayedBack> reverseTrigger  = new HashMap<>();
		public final HashMap<IGameEntity, IPlayedBack> stopTrigger     = new HashMap<>();
		public final HashMap<IGameEntity, IPlayedBack> pauseTrigger    = new HashMap<>();
		public final HashMap<IGameEntity, IPlayedBack> resumeTrigger   = new HashMap<>();
	}
	
	public final PlaybackInputOption inputOption = new PlaybackInputOption();
	
	@Override
	public Object handle(EntityEventType type, IGameEntity owner, Object... args) {
		super.handle(type, owner, args);
		
		if( inputOption.playTrigger.get(owner) != null ) 
			for( IPlayedBack target : targets )
				target.onPlay();
		
		if( inputOption.reverseTrigger.get(owner) != null ) 	
			for( IPlayedBack target : targets )
				target.onReverse();
		

		if( inputOption.stopTrigger.get(owner) != null ) 
			for( IPlayedBack target : targets )
				target.onStop();
		
		if( inputOption.pauseTrigger.get(owner) != null ) 
			for( IPlayedBack target : targets )
				target.onPause();
		
		if( inputOption.resumeTrigger.get(owner) != null ) 
			for( IPlayedBack target : targets )
				target.onResume();

		return null;
	}

}
