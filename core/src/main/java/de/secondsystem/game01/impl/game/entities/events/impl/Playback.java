package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.HashMap;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler.EntityEventType;

public class Playback extends SequencedObject {
	
	public class PlaybackInputOption {
		public final HashMap<IGameEntity, IPlayedBack> play     = new HashMap<>();
		public final HashMap<IGameEntity, IPlayedBack> reverse  = new HashMap<>();
		public final HashMap<IGameEntity, IPlayedBack> stop     = new HashMap<>();
		public final HashMap<IGameEntity, IPlayedBack> pause    = new HashMap<>();
		public final HashMap<IGameEntity, IPlayedBack> resume   = new HashMap<>();
	}
	
	public final PlaybackInputOption inputOption = new PlaybackInputOption();
	
	@Override
	public Object handle(EntityEventType type, IGameEntity owner, Object... args) {
		super.handle(type, owner, args);
		
		if( inputOption.play.get(owner) != null ) {
			inputOption.play.get(owner).onPlay();
		
			for( IPlayedBack target : targets )
				target.onPlay();
		}
		
		if( inputOption.reverse.get(owner) != null ) {
			inputOption.reverse.get(owner).onReverse();
		
			for( IPlayedBack target : targets )
				target.onReverse();
		}

		if( inputOption.stop.get(owner) != null ) {
			inputOption.stop.get(owner).onStop();
		
			for( IPlayedBack target : targets )
				target.onStop();
		}
		
		if( inputOption.pause.get(owner) != null ) {
			inputOption.pause.get(owner).onPause();
		
			for( IPlayedBack target : targets )
				target.onPause();
		}
		
		if( inputOption.resume.get(owner) != null ) {
			inputOption.resume.get(owner).onResume();
		
			for( IPlayedBack target : targets )
				target.onResume();
		}

		return null;
	}

}
