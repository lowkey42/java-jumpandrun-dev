package de.secondsystem.game01.impl.game.entities.events.impl;

import org.json.simple.JSONObject;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.model.IAnimated;
import de.secondsystem.game01.model.IAnimated.AnimationType;

public class AnimatedSequencedEntity extends SequencedEntity implements IPlayedBack {
	
	private final IGameEntity animatedEntity;
	private AnimationType animationType;
	private IAnimated animation;
	
	public AnimatedSequencedEntity(IGameEntity owner) {
		this(owner, null);
	}
	
	public AnimatedSequencedEntity(IGameEntity owner, AnimationType animationType) {
		animatedEntity = owner;
		animation = ((IAnimated) owner.getRepresentation());
		this.animationType = animationType;
	}
	
	public void setAnimationType(AnimationType animationType) {
		this.animationType = animationType;
	}
	
	@Override
	public void onTurnOn() {
		super.onTurnOn();
		
		animation.play(AnimationType.USED, 1.f, true, true, false);
	}

	@Override
	public void onTurnOff() {
		super.onTurnOff();
		
		animation.play(AnimationType.IDLE, 1.f, true, true, false);
	}

	@Override
	public void onPlay() {
		animation.play(animationType, 1.f, false, true, false);
	}

	@Override
	public void onReverse() {
		animation.reverse();
	}

	@Override
	public void onStop() {
		animation.stop();
	}

	@Override
	public void onPause() {
		animation.pause();
	}

	@Override
	public void onResume() {
		animation.resume();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject serialize() {
		JSONObject obj = super.serialize();
		obj.put("animatedEntity", animatedEntity.uuid());
		obj.put("animationType", animationType);
		
		return obj;
	}
}
