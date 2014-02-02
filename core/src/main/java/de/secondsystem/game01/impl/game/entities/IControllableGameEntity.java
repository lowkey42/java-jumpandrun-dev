package de.secondsystem.game01.impl.game.entities;

public interface IControllableGameEntity extends IControllable, IGameEntity {

	void setController( IGameEntityController controller );
	
	IGameEntityController getController();
	
}
