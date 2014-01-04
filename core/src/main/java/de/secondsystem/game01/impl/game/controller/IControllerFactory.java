package de.secondsystem.game01.impl.game.controller;

import de.secondsystem.game01.impl.game.entities.IControllableGameEntity;
import de.secondsystem.game01.impl.game.entities.IGameEntityController;
import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.model.Attributes;

interface IControllerFactory {
	IGameEntityController create(IControllableGameEntity entity, IGameMap map, Attributes attributes);
}