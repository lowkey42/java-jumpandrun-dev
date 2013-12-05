package de.secondsystem.game01.impl.game.entities;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractGameEntityController implements IGameEntityController {

	protected final Set<IControllableGameEntity> ges = new HashSet<>(); 
	
	protected final IControllable proxy = new ControllableProxy();
	
	public final void addGE( IControllableGameEntity ge ) {
		ges.add(ge);
		ge.setController(this);
	}
	
	public final void removeGE( IControllableGameEntity ge ) {
		ges.remove(ge);
		ge.setController(null);
	}
	
	private final class ControllableProxy implements IControllable {

		@Override public void jump() {
			for( IControllableGameEntity ge : ges )
				ge.jump();
		}

		@Override public void moveHorizontally(HDirection direction, float factor) {
			for( IControllableGameEntity ge : ges )
				ge.moveHorizontally(direction, factor);
		}

		@Override public void moveVertically(VDirection direction, float factor) {
			for( IControllableGameEntity ge : ges )
				ge.moveVertically(direction, factor);
		}

		@Override
		public void liftObject() {
			for( IControllableGameEntity ge : ges )
				ge.liftObject();		
		}

		@Override
		public void switchWorlds() {
			for( IControllableGameEntity ge : ges )
				ge.switchWorlds();	
		}
		
		@Override
		public void incThrowingPower() {
			for( IControllableGameEntity ge : ges )
				ge.incThrowingPower();	
		}

		@Override
		public void use() {
			for( IControllableGameEntity ge : ges )
				ge.use();	
		}
		
	}
	
}
