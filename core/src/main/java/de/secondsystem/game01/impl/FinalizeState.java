package de.secondsystem.game01.impl;

import de.secondsystem.game01.fsm.IContext;
import de.secondsystem.game01.fsm.IState;

/**
 * Letzter Zustand vorm Beenden der Anwendung
 * @author lowkey
 *
 */
public class FinalizeState implements IState {

	@Override
	public void enter(IContext ctx) {
	}

	@Override
	public IContext exit() {
		return null;
	}

	@Override
	public IState update() {
		return null;
	}

}
