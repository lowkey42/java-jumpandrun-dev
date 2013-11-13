package de.secondsystem.game01.impl.game.entities;

import java.util.HashSet;
import java.util.Set;

import de.secondsystem.game01.impl.game.entities.IControllable.Direction;


public abstract class AbstractGameEntityController {

	protected final Set<IGameEntity> ges = new HashSet<>(); 
	
	protected final IControllable proxy = new ControllableProxy();
	
	public final void addGE( IGameEntity ge ) {
		ges.add(ge);
	}
	
	public final void removeGE( IGameEntity ge ) {
		ges.remove(ge);
	}
	
	private final class ControllableProxy implements IControllable {

		@Override
		public void move(Direction direction) {
			for( IGameEntity ge : ges )
				ge.move(direction);
		}

		@Override
		public void jump() {
			for( IGameEntity ge : ges )
				ge.jump();
		}

		@Override
		public void stopJump() {
			for( IGameEntity ge : ges )
				ge.stopJump();
		}
		
	}
	
}
