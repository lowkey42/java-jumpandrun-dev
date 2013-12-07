package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.ArrayList;
import java.util.List;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler;
import de.secondsystem.game01.impl.game.entities.events.IEntityEventHandler.EntityEventType;
import de.secondsystem.game01.impl.game.entities.events.impl.Toggle.ToggleInputOption;

public class Playback extends SequencedObject {
	
//	public class PlaybackInputOption {
//		public final List<ISequencedEntity> play     = new ArrayList<>();
//		public final List<ISequencedEntity> reverse  = new ArrayList<>(); 
//		public final List<ISequencedEntity> stop     = new ArrayList<>();
//		public final List<ISequencedEntity> pause    = new ArrayList<>();
//	}
//	
//	public final PlaybackInputOption inputOption = new PlaybackInputOption();
//	
//	@Override
//	public Object handle(EntityEventType type, IGameEntity owner, List<ISequencedEntity> targets, List<IEntityEventHandler> events) {
//		super.handle(type, owner, targets, events);
//		
//		for( ISequencedEntity entity : inputOption.play ) {
//			entity.setOwner(owner);
//			entity.onPlay();
//			for( ISequencedEntity target : targets )
//				target.onTurnOn();
//		}
//		
//		for( ISequencedEntity entity : inputOption.reverse ) {
//			entity.setOwner(owner);
//			entity.onReverse();
//			for( ISequencedEntity target : targets )
//				target.onTurnOff();
//		}
//		
//		for( ISequencedEntity entity : inputOption.stop ) {
//			entity.setOwner(owner);
//			entity.onStop();
//			for( ISequencedEntity target : targets )
//				target.onToggle();
//		}
//		
//		for( ISequencedEntity entity : inputOption.pause ) {
//			entity.setOwner(owner);
//			entity.onPause();
//			for( ISequencedEntity target : targets )
//				target.onToggle();
//		}
//		
//		return null;
//	}

}
