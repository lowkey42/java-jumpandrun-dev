package de.secondsystem.game01.impl.game.entities.events.impl;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.model.IAnimated;
import de.secondsystem.game01.model.IAnimated.AnimationType;

public class AnimatedSequencedEntity extends SequencedEntity implements IPlayback {
	
	private final IAnimated animatedEntity;
	private AnimationType animationType;
	
	public AnimatedSequencedEntity(IGameEntity owner) {
		this(owner, null, null);
	}
	
	public AnimatedSequencedEntity(IGameEntity owner, ISequencedEntity linkedEntity) {
		this(owner, linkedEntity, null);
	}
	
	public AnimatedSequencedEntity(IGameEntity owner, AnimationType animationType) {
		this(owner, null, animationType);
	}
	
	public AnimatedSequencedEntity(IGameEntity owner, ISequencedEntity linkedEntity, AnimationType animationType) {
		this.linkedEntity = linkedEntity;
		
		animatedEntity = ((IAnimated) owner.getRepresentation());
		this.animationType = animationType;
	}
	
	@Override
	public void onTurnOn() {
		super.onTurnOn();
		
		animatedEntity.play(AnimationType.USED, 1.f, true, true, false);
	}

	@Override
	public void onTurnOff() {
		super.onTurnOff();
		
		animatedEntity.play(AnimationType.IDLE, 1.f, true, true, false);
	}

	@Override
	public void onPlay() {
		animatedEntity.play(animationType, 1.f, false, true, false);
	}

	@Override
	public void onReverse() {
		animatedEntity.reverse();
	}

	@Override
	public void onStop() {
		animatedEntity.stop();
	}

	@Override
	public void onPause() {
		animatedEntity.pause();
	}

	@Override
	public void onResume() {
		animatedEntity.resume();
	}
}
