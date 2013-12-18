package de.secondsystem.game01.impl.game.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.secondsystem.game01.impl.game.entities.IControllableGameEntity;

public final class ControllerManager {
	private final Map<UUID, IController> controllers = new HashMap<>();
	
	public PatrollingController createPatrollingController(IControllableGameEntity controlledEntity, boolean repeated) {
		PatrollingController controller = new PatrollingController(UUID.randomUUID(), controlledEntity, repeated);
		controllers.put(controller.uuid(), controller);
		
		return controller;
	}
	
	public IController get(UUID uuid) {
		return controllers.get(uuid);
	}
	
	public void add(IController controller) {
		controllers.put(controller.uuid(), controller);
	}
}
