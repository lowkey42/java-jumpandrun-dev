package de.secondsystem.game01.impl.game.entities;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractGameEntityController {

	protected final Set<IControllableGameEntity> ges = new HashSet<>(); 
	
	protected final IControllable proxy = new ControllableProxy();
	
	public final void addGE( IControllableGameEntity ge ) {
		ges.add(ge);
	}
	
	public final void removeGE( IControllableGameEntity ge ) {
		ges.remove(ge);
	}
	
	private final class ControllableProxy implements IControllable {

		@Override public void jump() {
			for( IControllableGameEntity ge : ges )
				ge.jump();
		}

		@Override public void moveHorizontally(HDirection direction) {
			for( IControllableGameEntity ge : ges )
				ge.moveHorizontally(direction);
		}

		@Override public void moveVertically(VDirection direction) {
			for( IControllableGameEntity ge : ges )
				ge.moveVertically(direction);
		}

		@Override
		public void liftObject(boolean lift) {
			for( IControllableGameEntity ge : ges )
				ge.liftObject(lift);		
		}

		@Override
		public void switchWorlds() {
			for( IControllableGameEntity ge : ges )
				ge.switchWorlds();	
		}
		
	}
	
}
