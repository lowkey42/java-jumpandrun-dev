package de.secondsystem.game01.impl.game.entities.events.impl;

public abstract class AbstractSequencedEntity implements IToggled, IPlayedBack {
	
	protected boolean on = false;
	protected AbstractSequencedEntity linkedEntity = null;
	
	@Override
	public void onTurnOn() {
		on = true;
		if( linkedEntity != null )
			linkedEntity.onTurnOn();
	}

	@Override
	public void onTurnOff() {
		on = false;
		if( linkedEntity != null )
			linkedEntity.onTurnOff();
	}

	@Override
	public void onToggle() {
		if( !on )
			onTurnOn();
		else
			onTurnOff();
	}
	
	@Override
	public abstract void onPlay();
	@Override
	public abstract void onReverse();
	@Override
	public abstract void onStop();
	@Override
	public abstract void onResume();
	@Override
	public abstract void onPause();
}
