package de.secondsystem.game01.impl.map;

import org.jsfml.graphics.RenderTarget;

import de.secondsystem.game01.impl.map.IGameMap.WorldId;

public interface IWorldDrawable {

	void draw(RenderTarget renderTarget, WorldId world);
	
}
