package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.UUID;

import org.json.simple.JSONObject;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.game.entities.IGameEntityManager;
import de.secondsystem.game01.model.IAnimated;
import de.secondsystem.game01.model.IAnimated.AnimationType;

public class AnimatedSequencedEntity extends SequencedEntity implements IPlayedBack {
	
	private IGameEntity animatedEntity;
	private AnimationType animationType;
	private IAnimated animation;
	
	public AnimatedSequencedEntity(UUID uuid, IGameEntity owner) {
		this(uuid, owner, null);
	}
	
	public AnimatedSequencedEntity(UUID uuid, IGameEntity owner, AnimationType animationType) {
		super(uuid);
		
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
		obj.put("animatedEntity", animatedEntity.uuid().toString());
		obj.put("animationType", animationType == null ? null : animationType.toString());
		
		return obj;
	}
	
	@Override
	public SequencedEntity deserialize(JSONObject obj, IGameEntityManager entityManager, SequenceManager sequenceManager) {
		SequencedEntity seqEntity = super.deserialize(obj, entityManager, sequenceManager);
		if( seqEntity != null )
			return seqEntity;
		
		UUID uuid = (UUID) obj.get("animatedEntity");
		animatedEntity = entityManager.get(uuid);
		animationType = AnimationType.valueOf( (String) obj.get("animationType") );
		
		return null;
	} 
}
