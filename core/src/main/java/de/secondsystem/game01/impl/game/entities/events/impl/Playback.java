package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.ArrayList;
import java.util.List;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler.EntityEventType;

public class Playback extends SequencedObject {
	
	public class PlaybackInputOption {
		public final List<ISequencedEntity> play     = new ArrayList<>();
		public final List<ISequencedEntity> reverse  = new ArrayList<>();
		public final List<ISequencedEntity> stop     = new ArrayList<>();
		public final List<ISequencedEntity> pause    = new ArrayList<>();
		public final List<ISequencedEntity> resume   = new ArrayList<>();
	}
	
	public final PlaybackInputOption inputOption = new PlaybackInputOption();
	
	@Override
	public Object handle(EntityEventType type, IGameEntity owner) {
		super.handle(type, owner);
		
		for( ISequencedEntity entity : inputOption.play ) {
			((IPlayback) entity).onPlay();
			for( ISequencedEntity target : targets )
				((IPlayback) target).onPlay();
		}
		
		for( ISequencedEntity entity : inputOption.reverse ) {
			((IPlayback) entity).onReverse();
			for( ISequencedEntity target : targets )
				((IPlayback) target).onReverse();
		}
		
		for( ISequencedEntity entity : inputOption.stop ) {
			((IPlayback) entity).onStop();
			for( ISequencedEntity target : targets )
				((IPlayback) target).onStop();
		}
		
		for( ISequencedEntity entity : inputOption.pause ) {
			((IPlayback) entity).onPause();
			for( ISequencedEntity target : targets )
				((IPlayback) target).onPause();
		}
		
		for( ISequencedEntity entity : inputOption.resume ) {
			((IPlayback) entity).onResume();
			for( ISequencedEntity target : targets )
				((IPlayback) target).onResume();
		}
		
		return null;
	}

}
