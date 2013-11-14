package de.secondsystem.game01.impl.map.physics;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import de.secondsystem.game01.impl.map.GameMap;

public abstract class PhysicsBody {
	
	protected Body body = null;
	protected final World world;
	protected int gameWorldId;
	
	protected PhysicsBody(int gameWorldId)
	{
		this.world = GameMap.physicsWorld;
		this.gameWorldId = gameWorldId;
	}
	
	protected void createBody(float x, float y, float width, float height, float rotation, boolean isStatic)
	{
		assert(body == null);
		
		// body definition
		BodyDef bd = new BodyDef();
		float sf = GameMap.BOX2D_SCALE_FACTOR;
		bd.position.set(x*sf, y*sf);  
		bd.angle = (float)Math.toRadians(rotation);
		bd.type = isStatic ? BodyType.STATIC : BodyType.DYNAMIC;
		
		// define shape of the body
		PolygonShape s = new PolygonShape();
		
		// input half extents
		s.setAsBox(width/2f*sf, height/2f*sf);
		
		// create the body
		body = world.createBody(bd);
		
		if( !isStatic )
		{
			// fixture definition
			FixtureDef fd = new FixtureDef();
			fd.shape = s;
			fd.density = 1.0f;
			fd.friction = 0.4f;
			fd.restitution = 0.0f;
			
			//add fixture to body
			body.createFixture(fd);
		}
		else
			body.createFixture(s, 0f);	
		
		body.setUserData(this);
	}
	
	public Body getBody()
	{
		return body;
	}
	
	public void setGameWorldId(int id)
	{
		gameWorldId = id;
	}
	
	public int getGameWorldId()
	{
		return gameWorldId;
	}
	
	protected abstract void beginContact(PhysicsBody with);
	protected abstract void endContact(PhysicsBody with);

}
