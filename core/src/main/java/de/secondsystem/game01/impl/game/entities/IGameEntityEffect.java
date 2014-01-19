package de.secondsystem.game01.impl.game.entities;

import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

public interface IGameEntityEffect {

	void draw(RenderTarget rt, Vector2f position, float rotation);
	
}
