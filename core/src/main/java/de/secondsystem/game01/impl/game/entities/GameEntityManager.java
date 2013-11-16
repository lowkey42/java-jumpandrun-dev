package de.secondsystem.game01.impl.game.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Vector2f;

import de.secondsystem.game01.impl.map.IGameMap;
import de.secondsystem.game01.impl.map.physics.CollisionHandlerType;
import de.secondsystem.game01.impl.map.physics.IPhysicsBody;
import de.secondsystem.game01.model.IDrawable;
import de.secondsystem.game01.model.IMoveable;

public final class GameEntityManager implements IGameEntityManager {

	private final Map<UUID, IGameEntity> entities = new HashMap<>();
	
	final IGameMap map;
	
	public GameEntityManager(IGameMap map) {
		this.map = map;
	}
	
/*	public IGameEntity create(IDrawable representation, IPhysicsBody physicsBody) {
		return new GameEntity(UUID.randomUUID(), representation, physicsBody);
	}*/
	
	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.game.entities.IGameEntityManager#createPlayer(float, float)
	 */
	@Override
	public IControllableGameEntity createPlayer( float x, float y ) {
		TmpPlayerRepr repr = new TmpPlayerRepr();
		repr.shape = new RectangleShape(new Vector2f(50, 50));
		repr.shape.setPosition(x, y);
		repr.shape.setFillColor(Color.WHITE);
		repr.shape.setOutlineColor(Color.BLACK);
		repr.shape.setOutlineThickness(2f);
		repr.shape.setOrigin( repr.shape.getSize().x/2, repr.shape.getSize().y/2);
				
		final IPhysicsBody physicsBody = map.getPhysicalWorld().createBody(map.getActiveGameWorldId(), x, y, 50, 50, 0, false, CollisionHandlerType.SOLID);
		
		ControllableGameEntity e = new ControllableGameEntity(UUID.randomUUID(), this,
				map.getActiveGameWorldId(), 
				repr, 
				physicsBody, 
				2.f, 
				25.f, 
				4.f, 
				120.f );
		
		entities.put(e.uuid(), e);
		
		return e;
	}
	
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

	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.game.entities.IGameEntityManager#destroy(java.util.UUID)
	 */
	@Override
	public void destroy( UUID eId ) {
		// TODO
	}

	/* (non-Javadoc)
	 * @see de.secondsystem.game01.impl.game.entities.IGameEntityManager#get(java.util.UUID)
	 */
	@Override
	public IGameEntity get( UUID eId ) {
		return null; // TODO
	}

	@Override
	public void draw(RenderTarget renderTarget) {
		for( IGameEntity entity : entities.values() )
			entity.draw(renderTarget);
	}

	@Override
	public void update(long frameTimeMs) {
		for( IGameEntity entity : entities.values() )
			entity.update(frameTimeMs);
	}
	
}
