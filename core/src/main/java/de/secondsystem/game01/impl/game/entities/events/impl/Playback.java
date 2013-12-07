package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.ArrayList;
import java.util.List;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler.EntityEventType;

public class Playback extends SequencedObject {
	
	public class PlaybackInputOption {
		public final List<SequencedEntity> play     = new ArrayList<>();
		public final List<SequencedEntity> reverse  = new ArrayList<>();
		public final List<SequencedEntity> stop     = new ArrayList<>();
		public final List<SequencedEntity> pause    = new ArrayList<>();
		public final List<SequencedEntity> resume   = new ArrayList<>();
	}
	
	public final PlaybackInputOption inputOption = new PlaybackInputOption();
	
	@Override
	public Object handle(EntityEventType type, IGameEntity owner) {
		super.handle(type, owner);
		
		for( SequencedEntity entity : inputOption.play ) {
			((IPlayback) entity).onPlay();
			for( SequencedEntity target : targets )
				((IPlayback) target).onPlay();
		}
		
		for( SequencedEntity entity : inputOption.reverse ) {
			((IPlayback) entity).onReverse();
			for( SequencedEntity target : targets )
				((IPlayback) target).onReverse();
		}
		
		for( SequencedEntity entity : inputOption.stop ) {
			((IPlayback) entity).onStop();
			for( SequencedEntity target : targets )
				((IPlayback) target).onStop();
		}
		
		for( SequencedEntity entity : inputOption.pause ) {
			((IPlayback) entity).onPause();
			for( SequencedEntity target : targets )
				((IPlayback) target).onPause();
		}
		
		for( SequencedEntity entity : inputOption.resume ) {
			((IPlayback) entity).onResume();
			for( SequencedEntity target : targets )
				((IPlayback) target).onResume();
		}
		
		return null;
	}

}
