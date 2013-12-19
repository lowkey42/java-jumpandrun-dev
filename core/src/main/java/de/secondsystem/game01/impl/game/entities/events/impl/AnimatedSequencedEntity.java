package de.secondsystem.game01.impl.game.entities.events.impl;

import java.util.UUID;

import org.json.simple.JSONObject;

import de.secondsystem.game01.impl.game.entities.IGameEntity;
import de.secondsystem.game01.impl.graphic.AnimatedSprite;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.model.IAnimated;
import de.secondsystem.game01.model.IAnimated.AnimationType;

public class AnimatedSequencedEntity extends SequencedEntity implements IPlayedBack {
	
	private IGameEntity animatedEntity;
	private AnimationType animationType;
	private IAnimated animation;
	
	public AnimatedSequencedEntity() {
	}
	
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
		
		animation.play(AnimationType.UNUSED, 1.f, true, true, false);
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
		obj.put("class", "AnimatedSequencedEntity");
		
		return obj;
	}
	
	@Override
	public SequencedEntity deserialize(JSONObject obj, IGameMap map) {
		SequencedEntity seqEntity = super.deserialize(obj, map);
		if( seqEntity != null )
			return seqEntity;
		
		UUID uuid = UUID.fromString( (String) obj.get("animatedEntity") );
		animatedEntity = map.getEntityManager().get(uuid);
		animationType = obj.get("animationType") != null ? AnimationType.valueOf( (String) obj.get("animationType") ) : null;	
		animation = ((IAnimated) animatedEntity.getRepresentation());
		
		return null;
	} 
}
