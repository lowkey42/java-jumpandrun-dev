package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.ArrayList;
import java.util.List;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler.EntityEventType;

public class Playback extends SequencedObject implements IPlayedBack {
	
	public class PlaybackInputOption {
		public final List<IPlayedBack> play     = new ArrayList<>();
		public final List<IPlayedBack> reverse  = new ArrayList<>();
		public final List<IPlayedBack> stop     = new ArrayList<>();
		public final List<IPlayedBack> pause    = new ArrayList<>();
		public final List<IPlayedBack> resume   = new ArrayList<>();
	}
	
	public final PlaybackInputOption inputOption = new PlaybackInputOption();
	
	@Override
	public Object handle(EntityEventType type, IGameEntity owner) {
		super.handle(type, owner);
		
		if( inputOption.play.size() > 0 )
			onPlay();
		
		if( inputOption.reverse.size() > 0 )
			onReverse();

		if( inputOption.stop.size() > 0 )
			onStop();
		
		if( inputOption.resume.size() > 0 )
			onResume();
		
		if( inputOption.pause.size() > 0 )
			onPause();

		return null;
	}

	@Override
	public void onPlay() {
		for( IPlayedBack playedBack : inputOption.play ) 
			playedBack.onPlay();
		
		for( IPlayedBack target : targets )
			target.onPlay();
	}

	@Override
	public void onReverse() {
		for( IPlayedBack playedBack : inputOption.reverse ) 
			playedBack.onReverse();
		
		for( IPlayedBack target : targets )
			target.onReverse();
	}

	@Override
	public void onStop() {
		for( IPlayedBack playedBack : inputOption.stop ) 
			playedBack.onStop();
		
		for( IPlayedBack target : targets )
			target.onStop();	
	}

	@Override
	public void onResume() {
		for( IPlayedBack playedBack : inputOption.resume ) 
			playedBack.onResume();
		
		for( IPlayedBack target : targets )
			target.onResume();		
	}

	@Override
	public void onPause() {
		for( IPlayedBack playedBack : inputOption.pause ) 
			playedBack.onPause();
		
		for( IPlayedBack target : targets )
			target.onPause();	
	}

}
