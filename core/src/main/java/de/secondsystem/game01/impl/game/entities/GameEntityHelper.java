package de.secondsystem.game01.impl.game.entities;

import java.util.Map;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.physics.CollisionHandlerType;
import de.secondsystem.game01.impl.map.physics.IPhysicsBody;
import de.secondsystem.game01.model.Attributes;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IMoveable;

final class GameEntityHelper {

	public static IDrawable createRepresentation( Attributes attributes ) {
		TmpPlayerRepr repr = new TmpPlayerRepr();	// TODO
		repr.shape = new RectangleShape(new Vector2f(attributes.getFloat("width"), attributes.getFloat("height")));
		repr.shape.setPosition(attributes.getFloat("x"), attributes.getFloat("y"));
		repr.shape.setFillColor(Color.WHITE);
		repr.shape.setOutlineColor(Color.BLACK);
		repr.shape.setOutlineThickness(2f);
		repr.shape.setOrigin( repr.shape.getSize().x/2, repr.shape.getSize().y/2);
		
		return repr;
	}
	public static IPhysicsBody createPhysicsBody( IGameMap map, boolean jumper, Attributes attributes ) {
		return map.getPhysicalWorld().createBody(attributes.getInteger("worldId", map.getActiveGameWorldId()),
				attributes.getFloat("x"), 
				attributes.getFloat("y"), 
				attributes.getFloat("width"), 
				attributes.getFloat("height"), 
				attributes.getFloat("rotation", 0), 
				false, CollisionHandlerType.SOLID, jumper);
	}
	
	// TODO: will be replaced by real wrapper-classes, animatedSprite, etc.
	private static class TmpPlayerRepr implements IDrawable, IMoveable {

		RectangleShape shape;
		
		@Override public void setPosition(Vector2f pos) {
			shape.setPosition(pos);
		}

		@Override public void setRotation(float degree) {
			shape.setRotation(degree);
		}

		@Override public float getRotation() {
			return shape.getRotation();
		}

		@Override public Vector2f getPosition() {
			return shape.getPosition();
		}

		@Override public void draw(RenderTarget renderTarget) {
			renderTarget.draw(shape);
		}
		
	}
	
	private GameEntityHelper() {}
}
